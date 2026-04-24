package com.nutrition.dss.dto;

/** Food item DTO with full nutritional data */
public class FoodItemDTO {

    private Long id;
    private String name;
    private String category;
    private String description;
    private int caloriesPer100g;
    private double protein;
    private double carbs;
    private double fats;
    private String type;
    private String calorieLabel;
    private String justification;

    public FoodItemDTO() {}

    public FoodItemDTO(Long id, String name, String category, String description,
                       int caloriesPer100g, double protein, double carbs, double fats,
                       String type, String calorieLabel, String justification) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.caloriesPer100g = caloriesPer100g;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.type = type;
        this.calorieLabel = calorieLabel;
        this.justification = justification;
    }

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

    public String getCalorieLabel() { return calorieLabel; }
    public void setCalorieLabel(String calorieLabel) { this.calorieLabel = calorieLabel; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
}
