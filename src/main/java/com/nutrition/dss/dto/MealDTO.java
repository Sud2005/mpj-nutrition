package com.nutrition.dss.dto;

public class MealDTO {
    private String name;
    private String description;
    
    public MealDTO() {}
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
