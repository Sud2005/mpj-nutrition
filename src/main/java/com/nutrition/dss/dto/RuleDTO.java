package com.nutrition.dss.dto;

/** Rule DTO for API CRUD operations */
public class RuleDTO {

    private Long id;
    private String condition;
    private String foodCategory;
    private String recommendation;
    private int priority;
    private String conditionExpression;
    private String actionType;

    public RuleDTO() {}

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
