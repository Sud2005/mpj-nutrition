package com.nutrition.dss.repository;

import com.nutrition.dss.model.DietPlan;
import com.nutrition.dss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    // Get all plans for a user, newest first
    List<DietPlan> findByUserOrderByGeneratedAtDesc(User user);

    // Get the latest plan for a user
    Optional<DietPlan> findFirstByUserOrderByGeneratedAtDesc(User user);

    // Get plans by user ID directly
    List<DietPlan> findByUserIdOrderByGeneratedAtDesc(Long userId);
}
