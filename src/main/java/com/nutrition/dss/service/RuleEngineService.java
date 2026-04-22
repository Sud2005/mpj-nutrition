package com.nutrition.dss.service;

import com.nutrition.dss.model.*;
import com.nutrition.dss.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ============================================================
 *  PERSON 3 - RULE ENGINE (Core of the whole system!)
 *  This is the most important Java file in the project.
 *  It reads the rules from the database and decides what
 *  foods are RECOMMENDED, LIMITED, or AVOID for a user.
 *
 *  HOW THE RULE ENGINE WORKS:
 *  1. Load ALL food items from DB
 *  2. Load rules for "NONE" (applies to everyone)
 *  3. Load rules for the user's specific condition (e.g. DIABETES)
 *  4. For each food's category, check rules — higher priority wins
 *  5. Default (no rule) = RECOMMENDED
 *
 *  HOW TO TINKER:
 *  - Change the default from RECOMMENDED to LIMITED
 *  - Add BMI-based rules (see the BMI TINKER section below)
 *  - Change how conflicts are resolved (currently: highest priority wins)
 *  - Add age-based rules (seniors get different recommendations)
 * ============================================================
 */
@Service
public class RuleEngineService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private DietaryRuleRepository dietaryRuleRepository;

    @Autowired
    private DietPlanRepository dietPlanRepository;

    /**
     * Main method: given a user's health profile, generate a diet plan.
     * Returns a map: { "RECOMMENDED": [food1, food2], "LIMITED": [...], "AVOID": [...] }
     */
    public Map<String, List<FoodItem>> generateDietPlan(HealthProfile profile) {

        // Step 1: Get all food items
        List<FoodItem> allFoods = foodItemRepository.findAll();

        // Step 2: Load general rules (apply to EVERYONE)
        List<DietaryRule> generalRules = dietaryRuleRepository.findByCondition("NONE");

        // Step 3: Load condition-specific rules (e.g., DIABETES rules)
        List<DietaryRule> conditionRules = new ArrayList<>();
        if (profile.getHealthCondition() != null && !profile.getHealthCondition().equals("NONE")) {
            conditionRules = dietaryRuleRepository
                    .findByConditionOrderByPriorityDesc(profile.getHealthCondition());
        }

        // ----- BMI TINKER ZONE -----
        // TINKER: Uncomment this to add BMI-based rules!
        // If user is Obese, automatically add rules for high-calorie foods
        /*
        if (profile.getBmi() >= 30.0) {
            DietaryRule obesityFatsRule = new DietaryRule("NONE", "FATS", "AVOID", 8);
            conditionRules.add(obesityFatsRule);
            DietaryRule obesitySweetsRule = new DietaryRule("NONE", "SWEETS", "AVOID", 8);
            conditionRules.add(obesitySweetsRule);
        }
        */

        // ----- AGE TINKER ZONE -----
        // TINKER: Add age-based rules
        /*
        if (profile.getAge() > 60) {
            // Seniors: recommend more DAIRY for calcium
            DietaryRule seniorDairy = new DietaryRule("NONE", "DAIRY", "RECOMMENDED", 5);
            conditionRules.add(seniorDairy);
        }
        */

        // Step 4: Build a map of category → best recommendation
        Map<String, String> categoryDecision = new HashMap<>();

        // Apply general rules first
        for (DietaryRule rule : generalRules) {
            categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
        }

        // Apply condition rules — these can OVERRIDE general rules
        // Higher priority wins (already ordered by priority desc from DB)
        for (DietaryRule rule : conditionRules) {
            String existing = categoryDecision.get(rule.getFoodCategory());
            if (existing == null) {
                // No rule yet — just set it
                categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
            } else {
                // Conflict! Higher priority wins.
                // Since conditionRules are already sorted by priority desc,
                // the first one we see is the highest priority.
                // TINKER: Change this logic! e.g. "most restrictive always wins"
                categoryDecision.putIfAbsent(rule.getFoodCategory(), rule.getRecommendation());
            }
        }

        // Step 5: Categorize each food item
        // ----- TINKER: Change "RECOMMENDED" default to "LIMITED" to be more conservative -----
        String DEFAULT_RECOMMENDATION = "RECOMMENDED";

        Map<String, List<FoodItem>> result = new LinkedHashMap<>();
        result.put("RECOMMENDED", new ArrayList<>());
        result.put("LIMITED", new ArrayList<>());
        result.put("AVOID", new ArrayList<>());

        for (FoodItem food : allFoods) {
            String decision = categoryDecision.getOrDefault(food.getCategory(), DEFAULT_RECOMMENDATION);
            result.get(decision).add(food);
        }

        return result;
    }

    /**
     * Save a generated plan to history.
     */
    public DietPlan savePlan(User user, Map<String, List<FoodItem>> plan, HealthProfile profile) {
        // Convert the plan map to a summary string
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, List<FoodItem>> entry : plan.entrySet()) {
            summary.append(entry.getKey()).append(":");
            for (FoodItem food : entry.getValue()) {
                summary.append(food.getName()).append(",");
            }
            summary.append("|");
        }

        DietPlan dietPlan = new DietPlan(user, summary.toString(),
                profile.getBmi(), profile.getHealthCondition());
        return dietPlanRepository.save(dietPlan);
    }

    /**
     * Get all past plans for a user.
     */
    public List<DietPlan> getHistory(User user) {
        return dietPlanRepository.findByUserOrderByGeneratedAtDesc(user);
    }
}
