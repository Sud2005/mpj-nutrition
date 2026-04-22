package com.nutrition.dss.repository;

import com.nutrition.dss.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ============================================================
 *  PERSON 2 - FOOD & RULES LAYER
 *  Database access for FoodItem.
 *
 *  HOW TO TINKER:
 *  - Add: List<FoodItem> findByNameContaining(String keyword);
 *    → Search foods by name (like a search bar)
 * ============================================================
 */
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    // Find all foods in a given category (e.g. "GRAINS")
    List<FoodItem> findByCategory(String category);

    // TINKER: Add search
    // List<FoodItem> findByNameContainingIgnoreCase(String keyword);
}
