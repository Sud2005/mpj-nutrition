package com.nutrition.dss.service;

import com.nutrition.dss.config.GroqConfig;
import com.nutrition.dss.dto.DietOutputDTO;
import com.nutrition.dss.dto.FoodItemDTO;
import com.nutrition.dss.model.HealthProfile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Groq LLM integration using LLaMA 3.1.
 * Provides diet plan refinement and nutritionist approval simulation.
 * Gracefully degrades when API key is not configured.
 */
@Service
public class GroqService {

    private final GroqConfig config;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GroqService(GroqConfig config) {
        this.config = config;
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    /** Check if Groq integration is available */
    public boolean isAvailable() {
        return config.isConfigured();
    }

    /**
     * Refine a diet plan using LLM.
     * Returns a refinement note, or null if unavailable.
     */
    public String refineDietPlan(DietOutputDTO dietOutput, HealthProfile profile) {
        if (!isAvailable()) {
            return null;
        }

        try {
            String prompt = buildRefinementPrompt(dietOutput, profile);
            return callGroqApi(prompt);
        } catch (Exception e) {
            System.err.println("Groq API call failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Re-generate or filter the diet plan using LLM based on BMI and Allergies.
     * Returns a new DietOutputDTO parsed from the LLM's JSON response.
     */
    public DietOutputDTO generateLLMDietPlan(DietOutputDTO baseOutput, HealthProfile profile) {
        if (!isAvailable()) {
            return baseOutput; // Fallback to rule engine if LLM unavailable
        }

        try {
            String prompt = buildGenerationPrompt(baseOutput, profile);
            String jsonResponse = callGroqApiForJson(prompt);
            
            if (jsonResponse != null) {
                // Parse the JSON string back into a DietOutputDTO
                return objectMapper.readValue(jsonResponse, DietOutputDTO.class);
            }
        } catch (Exception e) {
            System.err.println("Groq Diet Generation failed: " + e.getMessage());
        }
        return baseOutput; // Fallback on failure
    }

    private String buildRefinementPrompt(DietOutputDTO output, HealthProfile profile) {
        String recommended = formatFoodList(output.getRecommended());
        String limited = formatFoodList(output.getLimited());
        String avoid = formatFoodList(output.getAvoid());

        return String.format("""
            You are a professional nutritionist. Review this diet plan and provide brief refinement suggestions.
            
            Patient Profile:
            - Age: %d, Gender: %s
            - BMI: %.1f (%s)
            - Health Conditions: %s
            - Activity Level: %s
            - Dietary Preference: %s
            
            Current Diet Plan:
            RECOMMENDED: %s
            LIMITED: %s
            AVOID: %s
            
            Provide 2-3 short, actionable refinement suggestions. Be concise (max 200 words).
            """,
                profile.getAge(), profile.getGender(),
                profile.getBmi(), profile.getBMICategory(),
                profile.getHealthCondition(),
                profile.getActivityLevel(),
                profile.getDietaryPreference(),
                recommended, limited, avoid);
    }

    private String buildGenerationPrompt(DietOutputDTO output, HealthProfile profile) {
        try {
            String basePlanJson = objectMapper.writeValueAsString(output);
            return String.format("""
                You are a professional clinical nutritionist. Your task is to generate a personalized diet plan based on the provided patient profile and a base rule-engine plan.
                
                Patient Profile:
                - Age: %d, Gender: %s
                - BMI: %.1f (%s)
                - Health Conditions: %s
                - Dietary Preference: %s
                - Allergies: %s
                
                Base Rule-Engine Plan (JSON):
                %s
                
                Instructions:
                1. Review the base plan.
                2. STRICTLY REMOVE any foods the patient is allergic to from all categories.
                3. Adjust the Recommended, Limited, and Avoid lists based on their BMI and conditions. For example, if obese, move high-calorie foods to Limited or Avoid.
                4. You may rewrite the 'justification' field for foods to explain why you kept/moved them based on BMI or allergies.
                5. Return the result STRICTLY as a valid JSON object matching the exact structure of the input Base Plan JSON (containing 'recommended', 'limited', and 'avoid' arrays of food objects). Do not include any markdown formatting, backticks, or explanatory text outside the JSON object.
                """,
                    profile.getAge(), profile.getGender(),
                    profile.getBmi(), profile.getBMICategory(),
                    profile.getHealthCondition(),
                    profile.getDietaryPreference(),
                    profile.getAllergies() == null || profile.getAllergies().isEmpty() ? "None" : profile.getAllergies(),
                    basePlanJson);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String callGroqApi(String prompt) {
        return executeGroqCall(prompt, false);
    }

    private String callGroqApiForJson(String prompt) {
        // Find the JSON block if the model wraps it in markdown
        String response = executeGroqCall(prompt, true);
        if (response != null) {
            if (response.contains("```json")) {
                int start = response.indexOf("```json") + 7;
                int end = response.lastIndexOf("```");
                if (start < end) {
                    return response.substring(start, end).trim();
                }
            } else if (response.contains("```")) {
                int start = response.indexOf("```") + 3;
                int end = response.lastIndexOf("```");
                if (start < end) {
                    return response.substring(start, end).trim();
                }
            }
            return response.trim();
        }
        return null;
    }

    private String executeGroqCall(String prompt, boolean jsonMode) {
        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        
        // Use JSON response format if supported by the model/endpoint, 
        // otherwise rely on prompt instructions. LLaMA 3.1 supports it via response_format.
        Map<String, Object> requestBody;
        if (jsonMode) {
            requestBody = Map.of(
                    "model", config.getModel(),
                    "messages", List.of(message),
                    "temperature", 0.1, // Low temperature for consistent JSON
                    "max_tokens", 4000,
                    "response_format", Map.of("type", "json_object")
            );
        } else {
            requestBody = Map.of(
                    "model", config.getModel(),
                    "messages", List.of(message),
                    "temperature", config.getTemperature(),
                    "max_tokens", 500
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(config.getBaseUrl() + "/chat/completions")
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response != null && response.containsKey("choices")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (!choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> messageResp = (Map<String, Object>) choices.get(0).get("message");
                return (String) messageResp.get("content");
            }
        }
        return null;
    }

    private String formatFoodList(List<FoodItemDTO> foods) {
        if (foods == null || foods.isEmpty()) return "None";
        return foods.stream()
                .map(FoodItemDTO::getName)
                .collect(Collectors.joining(", "));
    }
}
