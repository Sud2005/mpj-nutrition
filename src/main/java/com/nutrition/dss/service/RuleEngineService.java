package com.nutrition.dss.service;

import com.nutrition.dss.dto.DietOutputDTO;
import com.nutrition.dss.dto.FoodItemDTO;
import com.nutrition.dss.model.*;
import com.nutrition.dss.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Core rule engine — evaluates dietary rules against a user's health profile
 * and produces categorized food recommendations.
 */
@Service
public class RuleEngineService {

    private final FoodItemRepository foodItemRepository;
    private final DietaryRuleRepository dietaryRuleRepository;
    private final DietPlanRepository dietPlanRepository;
    private final RuleEvaluatorService ruleEvaluatorService;
    private final GroqService groqService;
    private final ObjectMapper objectMapper;

    public RuleEngineService(FoodItemRepository foodItemRepository,
                             DietaryRuleRepository dietaryRuleRepository,
                             DietPlanRepository dietPlanRepository,
                             RuleEvaluatorService ruleEvaluatorService,
                             GroqService groqService) {
        this.foodItemRepository = foodItemRepository;
        this.dietaryRuleRepository = dietaryRuleRepository;
        this.dietPlanRepository = dietPlanRepository;
        this.ruleEvaluatorService = ruleEvaluatorService;
        this.groqService = groqService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate a diet plan map for Thymeleaf rendering.
     * Returns: { "RECOMMENDED": [...], "LIMITED": [...], "AVOID": [...] }
     */
    public Map<String, List<FoodItem>> generateDietPlan(HealthProfile profile) {
        List<FoodItem> allFoodsDb = foodItemRepository.findAll();

        // Filter by dietary preference
        String preference = profile.getDietaryPreference();
        if (preference == null) preference = "VEG";
        List<FoodItem> allFoods = new ArrayList<>();
        for (FoodItem f : allFoodsDb) {
            if (preference.equals(f.getType())) {
                allFoods.add(f);
            }
        }

        // Load general rules (NONE = applies to everyone)
        List<DietaryRule> generalRules = dietaryRuleRepository.findByCondition("NONE");

        // Load condition-specific rules
        List<DietaryRule> conditionRules = new ArrayList<>();
        if (profile.getHealthCondition() != null && !profile.getHealthCondition().equals("NONE")) {
            List<String> conditions = Arrays.asList(profile.getHealthCondition().split("\\s*,\\s*"));
            conditionRules = dietaryRuleRepository.findByConditionIn(conditions);
        }

        // Evaluate dynamic expression-based rules
        List<DietaryRule> dynamicRules = ruleEvaluatorService.evaluateDynamicRules(profile);
        conditionRules.addAll(dynamicRules);

        // Build category → recommendation map (highest priority wins)
        Map<String, String> categoryDecision = new HashMap<>();
        Map<String, Integer> categoryPriority = new HashMap<>();
        Map<String, DietaryRule> winningRules = new HashMap<>();
        Map<String, String> categoryJustification = new HashMap<>();

        for (DietaryRule rule : generalRules) {
            categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
            categoryPriority.put(rule.getFoodCategory(), rule.getPriority());
            winningRules.put(rule.getFoodCategory(), rule);
        }

        for (DietaryRule rule : conditionRules) {
            int currentPriority = categoryPriority.getOrDefault(rule.getFoodCategory(), -1);
            if (rule.getPriority() > currentPriority) {
                DietaryRule currentWinner = winningRules.get(rule.getFoodCategory());

                categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
                categoryPriority.put(rule.getFoodCategory(), rule.getPriority());
                winningRules.put(rule.getFoodCategory(), rule);

                String source = rule.getConditionExpression() != null
                        ? "expression '" + rule.getConditionExpression() + "'"
                        : rule.getCondition() + " rule";
                String just = rule.getFoodCategory() + " " + rule.getRecommendation().toLowerCase()
                        + " because " + source;
                if (currentWinner != null && !currentWinner.getCondition().equals("NONE")) {
                    just += " has higher priority than " + currentWinner.getCondition() + " rule";
                } else {
                    just += " applies";
                }
                categoryJustification.put(rule.getFoodCategory(), just);
            }
        }

        // Categorize food items
        String DEFAULT_RECOMMENDATION = "RECOMMENDED";
        Map<String, List<FoodItem>> result = new LinkedHashMap<>();
        result.put("RECOMMENDED", new ArrayList<>());
        result.put("LIMITED", new ArrayList<>());
        result.put("AVOID", new ArrayList<>());

        for (FoodItem food : allFoods) {
            String decision = categoryDecision.getOrDefault(food.getCategory(), DEFAULT_RECOMMENDATION);
            String just = categoryJustification.get(food.getCategory());
            if (just == null) {
                just = "General recommendation for " + food.getCategory();
            }
            food.setJustification(just);
            result.get(decision).add(food);
        }

        // --- LLM Refinement based on Allergies and BMI ---
        if (groqService.isAvailable()) {
            DietOutputDTO baseDto = new DietOutputDTO(
                    toFoodItemDTOs(result.get("RECOMMENDED")),
                    toFoodItemDTOs(result.get("LIMITED")),
                    toFoodItemDTOs(result.get("AVOID"))
            );
            
            DietOutputDTO refinedDto = groqService.generateLLMDietPlan(baseDto, profile);
            
            // Map the DTO lists back to FoodItem entities
            result.get("RECOMMENDED").clear();
            result.get("LIMITED").clear();
            result.get("AVOID").clear();
            
            if (refinedDto.getRecommended() != null) {
                refinedDto.getRecommended().forEach(dto -> result.get("RECOMMENDED").add(toFoodItem(dto)));
            }
            if (refinedDto.getLimited() != null) {
                refinedDto.getLimited().forEach(dto -> result.get("LIMITED").add(toFoodItem(dto)));
            }
            if (refinedDto.getAvoid() != null) {
                refinedDto.getAvoid().forEach(dto -> result.get("AVOID").add(toFoodItem(dto)));
            }
        }

        return result;
    }

    /**
     * Generate structured diet output (DTO) for API consumption.
     * Optionally refined by Groq LLM.
     */
    public DietOutputDTO generateDietOutputDTO(HealthProfile profile) {
        Map<String, List<FoodItem>> plan = generateDietPlan(profile);

        DietOutputDTO output = new DietOutputDTO(
                toFoodItemDTOs(plan.get("RECOMMENDED")),
                toFoodItemDTOs(plan.get("LIMITED")),
                toFoodItemDTOs(plan.get("AVOID"))
        );

        // Try LLM refinement
        if (groqService.isAvailable()) {
            String refinement = groqService.refineDietPlan(output, profile);
            output.setLlmRefinement(refinement);
        }

        return output;
    }

    /**
     * Helper to map FoodItemDTO back to FoodItem
     */
    private FoodItem toFoodItem(FoodItemDTO dto) {
        FoodItem item = new FoodItem();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setCaloriesPer100g(dto.getCaloriesPer100g());
        item.setProtein(dto.getProtein());
        item.setCarbs(dto.getCarbs());
        item.setFats(dto.getFats());
        item.setType(dto.getType());
        item.setJustification(dto.getJustification());
        return item;
    }

    /** Save a generated plan to history */
    public DietPlan savePlan(User user, Map<String, List<FoodItem>> plan, HealthProfile profile) {
        // Build summary string (backward compatible)
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, List<FoodItem>> entry : plan.entrySet()) {
            summary.append(entry.getKey()).append(":");
            for (FoodItem food : entry.getValue()) {
                summary.append(food.getName()).append(",");
            }
            summary.append("|");
        }

        // Build JSON output
        String planJson = null;
        try {
            DietOutputDTO dto = new DietOutputDTO(
                    toFoodItemDTOs(plan.get("RECOMMENDED")),
                    toFoodItemDTOs(plan.get("LIMITED")),
                    toFoodItemDTOs(plan.get("AVOID"))
            );
            planJson = objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            System.err.println("Failed to serialize plan JSON: " + e.getMessage());
        }

        DietPlan dietPlan = new DietPlan(user, summary.toString(), profile.getBmi(), profile.getHealthCondition());
        dietPlan.setPlanJson(planJson);

        // All plans are set to PENDING for the nutritionist (Admin) to review
        dietPlan.setApprovalStatus("PENDING");

        return dietPlanRepository.save(dietPlan);
    }

    /** Get all past plans for a user */
    public List<DietPlan> getHistory(User user) {
        return dietPlanRepository.findByUserOrderByGeneratedAtDesc(user);
    }

    /** Convert FoodItem entities to DTOs */
    private List<FoodItemDTO> toFoodItemDTOs(List<FoodItem> foods) {
        if (foods == null) return List.of();
        return foods.stream()
                .map(f -> new FoodItemDTO(
                        f.getId(), f.getName(), f.getCategory(), f.getDescription(),
                        f.getCaloriesPer100g(), f.getProtein(), f.getCarbs(), f.getFats(),
                        f.getType(), f.getCalorieLabel(), f.getJustification()))
                .toList();
    }
}
