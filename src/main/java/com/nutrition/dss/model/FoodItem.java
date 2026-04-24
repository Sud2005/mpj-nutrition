package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * FoodItem entity — stores nutritional data for each food.
 * Includes macronutrient breakdown per 100g.
 */
@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // GRAINS, PROTEINS, VEGETABLES, FRUITS, DAIRY, FATS, SWEETS

    private String description;

    private int caloriesPer100g;

    // Macronutrients per 100g
    private double protein;
    private double carbs;
    private double fats;

    @Column(nullable = false)
    private String type; // VEG or NON_VEG

    @Transient
    private String justification;

    // ---- Constructors ----
    public FoodItem() {}

    public FoodItem(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.type = "VEG";
    }

    public FoodItem(String name, String category, String description, int caloriesPer100g) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.caloriesPer100g = caloriesPer100g;
        this.type = "VEG";
    }

    public FoodItem(String name, String category, String description, String type) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.type = type;
    }

    public FoodItem(String name, String category, String description, int caloriesPer100g, String type) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.caloriesPer100g = caloriesPer100g;
        this.type = type;
    }

    /** Full constructor with macronutrients */
    public FoodItem(String name, String category, String description,
                    int caloriesPer100g, double protein, double carbs, double fats, String type) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.caloriesPer100g = caloriesPer100g;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.type = type;
    }

    // ---- Helpers ----
    public String getCalorieLabel() {
        if (caloriesPer100g == 0) return "Unknown";
        if (caloriesPer100g < 100) return "Low";
        if (caloriesPer100g <= 250) return "Moderate";
        return "High";
    }

    // ---- Getters & Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCaloriesPer100g() { return caloriesPer100g; }
    public void setCaloriesPer100g(int caloriesPer100g) { this.caloriesPer100g = caloriesPer100g; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getFats() { return fats; }
    public void setFats(double fats) { this.fats = fats; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
}
