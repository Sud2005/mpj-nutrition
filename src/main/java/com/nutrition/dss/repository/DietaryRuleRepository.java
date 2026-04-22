package com.nutrition.dss.repository;

import com.nutrition.dss.model.DietaryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 *  PERSON 2 - FOOD & RULES LAYER
 *  Database access for DietaryRule.
 *
 *  HOW TO TINKER:
 *  - Add: List<DietaryRule> findByPriorityGreaterThan(int priority);
 *    → Get only high-priority rules
 * ============================================================
 */
@Repository
public interface DietaryRuleRepository extends JpaRepository<DietaryRule, Long> {

    // Find all rules for a given health condition
    List<DietaryRule> findByConditionOrderByPriorityDesc(String condition);

    // Find rules matching NONE (applies to everyone)
    List<DietaryRule> findByCondition(String condition);
}
