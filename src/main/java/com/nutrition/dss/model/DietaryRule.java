package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * ============================================================
 *  PERSON 2 - FOOD & RULES LAYER
 *  A DietaryRule links a health condition to a food category
 *  and says what to do: RECOMMENDED, LIMITED, or AVOID.
 *
 *  Example rule:
 *    condition = "DIABETES"
 *    foodCategory = "SWEETS"
 *    recommendation = "AVOID"
 *    priority = 10
 *
 *  HOW TO TINKER:
 *  - Change "AVOID" to "LIMITED" for a rule → see it change on results
 *  - Add a new rule for "OBESITY" + "FATS" → "AVOID"
 *  - Change priority numbers to affect rule conflict resolution
 *  - Add a "reason" field to explain WHY a food is recommended
 * ============================================================
 */
@Entity
@Table(name = "dietary_rules")
public class DietaryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- TINKER ZONE -----

    // What health condition triggers this rule?
    // Values: NONE, DIABETES, HYPERTENSION, OBESITY
    // TINKER: Add new conditions matching HealthProfile.healthCondition
    @Column(nullable = false)
    private String condition;

    // Which food category does this rule apply to?
    // Values: GRAINS, PROTEINS, VEGETABLES, FRUITS, DAIRY, FATS, SWEETS
    @Column(nullable = false)
    private String foodCategory;

    // What should the user do with this food?
    // Values: RECOMMENDED, LIMITED, AVOID
    // TINKER: Change this and watch the results page update!
    @Column(nullable = false)
    private String recommendation;

    // Higher priority = this rule wins when two rules conflict
    // TINKER: Change priority numbers to see different outcomes
    @Column(nullable = false)
    private int priority = 1;

    // TINKER: Add a "reason" field to show WHY on the frontend
    // private String reason;

    // ----- END TINKER ZONE -----

    // ---- Constructors ----
    public DietaryRule() {}

    public DietaryRule(String condition, String foodCategory, String recommendation, int priority) {
        this.condition = condition;
        this.foodCategory = foodCategory;
        this.recommendation = recommendation;
        this.priority = priority;
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getFoodCategory() { return foodCategory; }
    public void setFoodCategory(String foodCategory) { this.foodCategory = foodCategory; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
