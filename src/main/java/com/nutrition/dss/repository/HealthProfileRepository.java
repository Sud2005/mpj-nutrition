package com.nutrition.dss.repository;

import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ============================================================
 *  PERSON 1 - REPOSITORY LAYER
 *  Database access for HealthProfile.
 *
 *  HOW TO TINKER:
 *  - Add: List<HealthProfile> findByHealthCondition(String condition);
 *    → Find all users with a specific condition
 *  - Add: List<HealthProfile> findByAgeGreaterThan(int age);
 *    → Find all users older than X
 * ============================================================
 */
@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {

    Optional<HealthProfile> findByUser(User user);

    // TINKER: Add more finders
    // List<HealthProfile> findByHealthCondition(String condition);
    // List<HealthProfile> findByAgeGreaterThan(int age);
}
