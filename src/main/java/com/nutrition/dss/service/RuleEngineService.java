package com.nutrition.dss.service;

import com.nutrition.dss.dto.DietOutputDTO;
import com.nutrition.dss.dto.FoodItemDTO;
import com.nutrition.dss.dto.WeeklyPlanDTO;
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
    private final WeightMeasurementRepository weightMeasurementRepository;
    private final RuleEvaluatorService ruleEvaluatorService;
    private final GroqService groqService;
    private final ObjectMapper objectMapper;

    public RuleEngineService(FoodItemRepository foodItemRepository,
                             DietaryRuleRepository dietaryRuleRepository,
                             DietPlanRepository dietPlanRepository,
                             WeightMeasurementRepository weightMeasurementRepository,
                             RuleEvaluatorService ruleEvaluatorService,
                             GroqService groqService) {
        this.foodItemRepository = foodItemRepository;
        this.dietaryRuleRepository = dietaryRuleRepository;
        this.dietPlanRepository = dietPlanRepository;
        this.weightMeasurementRepository = weightMeasurementRepository;
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

        // --- Base Rule Engine Plan Complete ---
        return result;
    }

    /**
     * Generate a full 7-day WeeklyPlanDTO. Uses Groq LLM if available, otherwise mocks a basic plan based on rules.
     */
    public WeeklyPlanDTO generateWeeklyPlan(HealthProfile profile) {
        Map<String, List<FoodItem>> basePlan = generateDietPlan(profile);
        DietOutputDTO baseDto = new DietOutputDTO(
                toFoodItemDTOs(basePlan.get("RECOMMENDED")),
                toFoodItemDTOs(basePlan.get("LIMITED")),
                toFoodItemDTOs(basePlan.get("AVOID"))
        );

        if (groqService.isAvailable()) {
            String weightTrendContext = buildWeightTrendContext(profile);
            WeeklyPlanDTO llmPlan = groqService.generateWeeklyDietPlan(baseDto, profile, weightTrendContext);
            if (llmPlan != null && llmPlan.getDays() != null && !llmPlan.getDays().isEmpty()) {
                return llmPlan;
            }
        }

        // Fallback: Generate a basic mock weekly plan using the recommended foods
        return generateMockWeeklyPlan(baseDto.getRecommended());
    }

    private WeeklyPlanDTO generateMockWeeklyPlan(List<FoodItemDTO> recommended) {
        List<com.nutrition.dss.dto.DailyPlanDTO> days = new ArrayList<>();
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (String day : dayNames) {
            com.nutrition.dss.dto.DailyPlanDTO daily = new com.nutrition.dss.dto.DailyPlanDTO();
            daily.setDay(day);
            
            com.nutrition.dss.dto.MealDTO bfast = new com.nutrition.dss.dto.MealDTO();
            bfast.setName("Healthy Breakfast");
            bfast.setDescription("Oats and fruits (Fallback rule-based)");
            
            com.nutrition.dss.dto.MealDTO lunch = new com.nutrition.dss.dto.MealDTO();
            lunch.setName("Balanced Lunch");
            lunch.setDescription("Grains and proteins (Fallback rule-based)");
            
            com.nutrition.dss.dto.MealDTO dinner = new com.nutrition.dss.dto.MealDTO();
            dinner.setName("Light Dinner");
            dinner.setDescription("Vegetables and proteins (Fallback rule-based)");
            
            daily.setBreakfast(bfast);
            daily.setLunch(lunch);
            daily.setDinner(dinner);
            days.add(daily);
        }
        
        return new WeeklyPlanDTO(days);
    }

    /**
     * Build recent weight-history context to help the LLM adapt meal intensity
     * based on whether the user is gaining, losing, or maintaining weight.
     */
    private String buildWeightTrendContext(HealthProfile profile) {
        if (profile == null || profile.getUser() == null) {
            return "No historical measurements available.";
        }

        List<WeightMeasurement> recent = weightMeasurementRepository.findTop5ByUserOrderByMeasuredAtDesc(profile.getUser());
        if (recent.isEmpty()) {
            return "No historical measurements available.";
        }

        List<WeightMeasurement> chronological = new ArrayList<>(recent);
        Collections.reverse(chronological);

        StringBuilder context = new StringBuilder("Recent measurements (oldest to newest): ");
        for (WeightMeasurement m : chronological) {
            context.append(String.format("[%.1fkg, BMI %.1f at %s] ",
                    m.getWeightKg(), m.getBmi(), m.getFormattedMeasuredAt()));
        }

        if (chronological.size() >= 2) {
            double start = chronological.get(0).getWeightKg();
            double latest = chronological.get(chronological.size() - 1).getWeightKg();
            double delta = latest - start;
            if (Math.abs(delta) < 0.1) {
                context.append("Trend: weight stable.");
            } else if (delta > 0) {
                context.append(String.format("Trend: gained %.1f kg.", delta));
            } else {
                context.append(String.format("Trend: lost %.1f kg.", Math.abs(delta)));
            }
        } else {
            context.append("Trend: only one data point.");
        }
        return context.toString();
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

    /** Save a generated weekly plan to history */
    public DietPlan saveWeeklyPlan(User user, WeeklyPlanDTO weeklyPlan, HealthProfile profile) {
        String planJson = null;
        try {
            planJson = objectMapper.writeValueAsString(weeklyPlan);
        } catch (Exception e) {
            System.err.println("Failed to serialize weekly plan JSON: " + e.getMessage());
        }

        DietPlan dietPlan = new DietPlan(user, "Weekly Meal Plan", profile.getBmi(), profile.getHealthCondition());
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
