package com.nutrition.dss.dto;

import java.util.List;

public class WeeklyPlanDTO {
    private List<DailyPlanDTO> days;
    
    public WeeklyPlanDTO() {}
    public WeeklyPlanDTO(List<DailyPlanDTO> days) { this.days = days; }
    
    public List<DailyPlanDTO> getDays() { return days; }
    public void setDays(List<DailyPlanDTO> days) { this.days = days; }
}
