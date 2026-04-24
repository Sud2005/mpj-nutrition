package com.nutrition.dss.repository;

import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {

    Optional<HealthProfile> findByUser(User user);

    // BMI distribution queries for admin dashboard
    long countByBmiLessThan(double bmi);

    long countByBmiBetween(double min, double max);

    long countByBmiGreaterThanEqual(double bmi);

    List<HealthProfile> findByHealthCondition(String condition);
}
