package com.nutrition.dss.service;

import com.nutrition.dss.dto.DashboardDTO;
import com.nutrition.dss.repository.DietPlanRepository;
import com.nutrition.dss.repository.HealthProfileRepository;
import com.nutrition.dss.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Aggregates statistics for the admin dashboard */
@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final DietPlanRepository dietPlanRepository;
    private final HealthProfileRepository healthProfileRepository;

    public DashboardService(UserRepository userRepository,
                            DietPlanRepository dietPlanRepository,
                            HealthProfileRepository healthProfileRepository) {
        this.userRepository = userRepository;
        this.dietPlanRepository = dietPlanRepository;
        this.healthProfileRepository = healthProfileRepository;
    }

    /** Build the complete dashboard statistics */
    public DashboardDTO getDashboardStats() {
        long totalUsers = userRepository.countByRole("USER");
        long totalPlans = dietPlanRepository.count();

        Map<String, Long> bmiDist = new LinkedHashMap<>();
        bmiDist.put("Underweight", healthProfileRepository.countByBmiLessThan(18.5));
        bmiDist.put("Normal", healthProfileRepository.countByBmiBetween(18.5, 24.99));
        bmiDist.put("Overweight", healthProfileRepository.countByBmiBetween(25.0, 29.99));
        bmiDist.put("Obese", healthProfileRepository.countByBmiGreaterThanEqual(30.0));

        return new DashboardDTO(totalUsers, totalPlans, bmiDist);
    }
}
