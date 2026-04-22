package com.nutrition.dss.repository;

import com.nutrition.dss.model.DietPlan;
import com.nutrition.dss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 *  PERSON 3 - SERVICE LAYER
 *  Database access for DietPlan history.
 *
 *  HOW TO TINKER:
 *  - Change findByUserOrderByGeneratedAtDesc to Asc
 *    → History shows oldest first instead of newest first
 * ============================================================
 */
@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    // Get all plans for a user, newest first
    List<DietPlan> findByUserOrderByGeneratedAtDesc(User user);
}
