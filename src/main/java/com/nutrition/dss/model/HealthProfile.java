package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * ============================================================
 *  PERSON 1 - MODEL LAYER
 *  Health Profile stores the user's physical stats.
 *  BMI is calculated automatically when height/weight are set.
 *
 *  HOW TO TINKER:
 *  - Change the BMI formula (currently weight / height^2)
 *  - Add a new health condition like "KIDNEY_DISEASE"
 *  - Add new activity levels like "ATHLETE"
 *  - Change age ranges to see different recommendations
 * ============================================================
 */
@Entity
@Table(name = "health_profiles")
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- TINKER ZONE: Change these fields -----

    private int age;
    private String gender;      // "MALE" or "FEMALE"
    private double heightCm;    // height in centimetres
    private double weightKg;    // weight in kilograms
    private double bmi;         // calculated automatically

    // Activity level — SEDENTARY, MODERATE, ACTIVE
    private String activityLevel;

    // Health condition — NONE, DIABETES, HYPERTENSION, OBESITY
    // TINKER: Add more conditions and then add rules for them in DietaryRule
    private String healthCondition = "NONE";

    // ----- END TINKER ZONE -----

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---- Constructors ----
    public HealthProfile() {}

    // ---- BMI Calculation ----
    /**
     * TINKER: Change this formula!
     * Standard BMI = weight(kg) / height(m)^2
     * You could add age-adjusted BMI here.
     */
    public void calculateBMI() {
        if (heightCm > 0) {
            double heightM = heightCm / 100.0;
            this.bmi = Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
        }
    }

    /**
     * TINKER: Change these ranges to see different BMI categories!
     * WHO standard: Underweight < 18.5, Normal 18.5-24.9, Overweight 25-29.9, Obese >= 30
     */
    public String getBMICategory() {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25.0) return "Normal";
        if (bmi < 30.0) return "Overweight";
        return "Obese";
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public String getHealthCondition() { return healthCondition; }
    public void setHealthCondition(String healthCondition) { this.healthCondition = healthCondition; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
