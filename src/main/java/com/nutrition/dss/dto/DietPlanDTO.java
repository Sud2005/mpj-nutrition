package com.nutrition.dss.dto;

import java.time.LocalDateTime;

/** Diet plan DTO for history and API responses */
public class DietPlanDTO {

    private Long id;
    private Long userId;
    private LocalDateTime generatedAt;
    private String planSummary;
    private String planJson;
    private double bmiAtGeneration;
    private String conditionAtGeneration;
    private String approvalStatus;

    public DietPlanDTO() {}

    public DietPlanDTO(Long id, Long userId, LocalDateTime generatedAt, String planSummary,
                       String planJson, double bmiAtGeneration, String conditionAtGeneration,
                       String approvalStatus) {
        this.id = id;
        this.userId = userId;
        this.generatedAt = generatedAt;
        this.planSummary = planSummary;
        this.planJson = planJson;
        this.bmiAtGeneration = bmiAtGeneration;
        this.conditionAtGeneration = conditionAtGeneration;
        this.approvalStatus = approvalStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getPlanSummary() { return planSummary; }
    public void setPlanSummary(String planSummary) { this.planSummary = planSummary; }

    public String getPlanJson() { return planJson; }
    public void setPlanJson(String planJson) { this.planJson = planJson; }

    public double getBmiAtGeneration() { return bmiAtGeneration; }
    public void setBmiAtGeneration(double bmiAtGeneration) { this.bmiAtGeneration = bmiAtGeneration; }

    public String getConditionAtGeneration() { return conditionAtGeneration; }
    public void setConditionAtGeneration(String conditionAtGeneration) { this.conditionAtGeneration = conditionAtGeneration; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}
