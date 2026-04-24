package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * DietaryRule entity — maps health conditions to food category recommendations.
 * Supports dynamic condition expressions for the improved rule engine.
 */
@Entity
@Table(name = "dietary_rules")
public class DietaryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String condition; // NONE, DIABETES, HYPERTENSION, OBESITY

    @Column(nullable = false)
    private String foodCategory; // GRAINS, PROTEINS, VEGETABLES, FRUITS, DAIRY, FATS, SWEETS

    @Column(nullable = false)
    private String recommendation; // RECOMMENDED, LIMITED, AVOID

    @Column(nullable = false)
    private int priority = 1;

    // Dynamic rule expression: e.g. "BMI > 25", "diabetes = true AND age > 40"
    @Column(length = 500)
    private String conditionExpression;

    // Generic action type for expression-based rules
    private String actionType;

    // ---- Constructors ----
    public DietaryRule() {}

    public DietaryRule(String condition, String foodCategory, String recommendation, int priority) {
        this.condition = condition;
        this.foodCategory = foodCategory;
        this.recommendation = recommendation;
        this.priority = priority;
    }

    public DietaryRule(String condition, String foodCategory, String recommendation,
                       int priority, String conditionExpression, String actionType) {
        this.condition = condition;
        this.foodCategory = foodCategory;
        this.recommendation = recommendation;
        this.priority = priority;
        this.conditionExpression = conditionExpression;
        this.actionType = actionType;
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

    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
}
