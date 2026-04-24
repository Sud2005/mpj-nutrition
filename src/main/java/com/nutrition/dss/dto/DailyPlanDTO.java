package com.nutrition.dss.dto;

public class DailyPlanDTO {
    private String day;
    private MealDTO breakfast;
    private MealDTO lunch;
    private MealDTO dinner;
    
    public DailyPlanDTO() {}
    
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    
    public MealDTO getBreakfast() { return breakfast; }
    public void setBreakfast(MealDTO breakfast) { this.breakfast = breakfast; }
    
    public MealDTO getLunch() { return lunch; }
    public void setLunch(MealDTO lunch) { this.lunch = lunch; }
    
    public MealDTO getDinner() { return dinner; }
    public void setDinner(MealDTO dinner) { this.dinner = dinner; }
}
