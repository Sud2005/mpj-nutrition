package com.nutrition.dss.repository;

import com.nutrition.dss.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    // Find all foods in a given category (e.g. "GRAINS")
    List<FoodItem> findByCategory(String category);

    // Find foods by calories
    List<FoodItem> findByCaloriesPer100gLessThan(int calories);


}
