package com.nutrition.dss.dto;

import java.util.Map;

/** Admin dashboard statistics */
public class DashboardDTO {

    private long totalUsers;
    private long totalDietPlans;
    private Map<String, Long> bmiDistribution; // e.g. {"Underweight": 3, "Normal": 15, ...}

    public DashboardDTO() {}

    public DashboardDTO(long totalUsers, long totalDietPlans, Map<String, Long> bmiDistribution) {
        this.totalUsers = totalUsers;
        this.totalDietPlans = totalDietPlans;
        this.bmiDistribution = bmiDistribution;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalDietPlans() { return totalDietPlans; }
    public void setTotalDietPlans(long totalDietPlans) { this.totalDietPlans = totalDietPlans; }

    public Map<String, Long> getBmiDistribution() { return bmiDistribution; }
    public void setBmiDistribution(Map<String, Long> bmiDistribution) { this.bmiDistribution = bmiDistribution; }
}
