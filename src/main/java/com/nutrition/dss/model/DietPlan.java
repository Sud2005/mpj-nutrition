package com.nutrition.dss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DietPlan entity — stores generated diet plan results.
 * Includes both summary string and structured JSON output.
 */
@Entity
@Table(name = "diet_plans")
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime generatedAt;

    // Simple text summary: "RECOMMENDED:Brown Rice,Oats|LIMITED:White Rice|AVOID:White Sugar"
    @Column(length = 2000)
    private String planSummary;

    // Structured JSON output for API consumption
    @Column(length = 10000)
    private String planJson;

    private double bmiAtGeneration;
    private String conditionAtGeneration;

    // PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private String approvalStatus = "PENDING";

    // ---- Constructors ----
    public DietPlan() {
        this.generatedAt = LocalDateTime.now();
    }

    public DietPlan(User user, String planSummary, double bmi, String condition) {
        this.user = user;
        this.planSummary = planSummary;
        this.bmiAtGeneration = bmi;
        this.conditionAtGeneration = condition;
        this.generatedAt = LocalDateTime.now();
        this.approvalStatus = "PENDING";
    }

    // ---- Helpers ----
    public String getFormattedDate() {
        if (generatedAt == null) return "Unknown";
        return generatedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
