package com.nutrition.dss.model;

import jakarta.persistence.*;

/**
 * ============================================================
 *  PERSON 2 - FOOD & RULES LAYER
 *  A FoodItem represents one food in the database.
 *  Admins can add/edit/delete food items.
 *
 *  HOW TO TINKER:
 *  - Add a new food category like "SNACKS" or "BEVERAGES"
 *  - Add a calories field → display it in the frontend
 *  - Add a "protein", "carbs", "fat" field for macros
 * ============================================================
 */
@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- TINKER ZONE -----

    @Column(nullable = false)
    private String name;   // e.g. "Brown Rice", "White Sugar"

    // Food category — GRAINS, PROTEINS, VEGETABLES, FRUITS, DAIRY, FATS, SWEETS
    // TINKER: Add new categories here AND update the admin form in admin.html
    @Column(nullable = false)
    private String category;

    // Optional short description shown on the results page
    private String description;

    // TINKER: Add calories per 100g to display nutritional info
    // private int caloriesPer100g;

    // ----- END TINKER ZONE -----

    // ---- Constructors ----
    public FoodItem() {}

    public FoodItem(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
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
}
