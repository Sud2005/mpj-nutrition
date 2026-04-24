package com.nutrition.dss.repository;

import com.nutrition.dss.model.User;
import com.nutrition.dss.model.WeightMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeightMeasurementRepository extends JpaRepository<WeightMeasurement, Long> {
    List<WeightMeasurement> findTop10ByUserOrderByMeasuredAtDesc(User user);
    List<WeightMeasurement> findTop5ByUserOrderByMeasuredAtDesc(User user);
    Optional<WeightMeasurement> findFirstByUserOrderByMeasuredAtDesc(User user);
}
