package com.nutrition.dss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  PERSON 3 - SERVICE LAYER (used by)
 *  A DietPlan is the saved result of running the rule engine.
 *  It stores what was recommended for a user on a given date.
 *
 *  HOW TO TINKER:
 *  - Add a "notes" field for the user to annotate their plan
 *  - Change the date format in getFormattedDate()
 *  - Add a "score" field (e.g. health score 0-100)
 * ============================================================
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

    // When was this plan generated?
    private LocalDateTime generatedAt;

    // We store the plan as a simple text summary
    // Format: "RECOMMENDED:Brown Rice,Oats|LIMITED:White Rice|AVOID:White Sugar"
    @Column(length = 2000)
    private String planSummary;

    // BMI at the time of generation (snapshot)
    private double bmiAtGeneration;

    // Health condition at the time
    private String conditionAtGeneration;

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
    }

    // ---- Helper: format date nicely for display ----
    // TINKER: Change the pattern to show date differently
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

    public double getBmiAtGeneration() { return bmiAtGeneration; }
    public void setBmiAtGeneration(double bmiAtGeneration) { this.bmiAtGeneration = bmiAtGeneration; }

    public String getConditionAtGeneration() { return conditionAtGeneration; }
    public void setConditionAtGeneration(String conditionAtGeneration) { this.conditionAtGeneration = conditionAtGeneration; }
}
