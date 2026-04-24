package com.nutrition.dss.controller.api;

import com.nutrition.dss.dto.DietPlanDTO;
import com.nutrition.dss.dto.UserDTO;
import com.nutrition.dss.exception.ResourceNotFoundException;
import com.nutrition.dss.model.DietPlan;
import com.nutrition.dss.model.User;
import com.nutrition.dss.service.UserService;
import com.nutrition.dss.repository.DietPlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for user data and diet plan history.
 * Requires JWT authentication.
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;
    private final DietPlanRepository dietPlanRepository;

    public UserApiController(UserService userService, DietPlanRepository dietPlanRepository) {
        this.userService = userService;
        this.dietPlanRepository = dietPlanRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return ResponseEntity.ok(userService.toDTO(user));
    }

    /** GET /api/users/{id}/diet-history — all diet plans for user */
    @GetMapping("/{id}/diet-history")
    public ResponseEntity<List<DietPlanDTO>> getDietHistory(@PathVariable Long id) {
        userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        List<DietPlan> plans = dietPlanRepository.findByUserIdOrderByGeneratedAtDesc(id);
        List<DietPlanDTO> dtos = plans.stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /** GET /api/users/{id}/latest-plan — most recent diet plan */
    @GetMapping("/{id}/latest-plan")
    public ResponseEntity<DietPlanDTO> getLatestPlan(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        DietPlan plan = dietPlanRepository.findFirstByUserOrderByGeneratedAtDesc(user)
                .orElseThrow(() -> new ResourceNotFoundException("No diet plans found for user " + id));
        return ResponseEntity.ok(toDTO(plan));
    }

    private DietPlanDTO toDTO(DietPlan plan) {
        return new DietPlanDTO(
                plan.getId(), plan.getUser().getId(), plan.getGeneratedAt(),
                plan.getPlanSummary(), plan.getPlanJson(),
                plan.getBmiAtGeneration(), plan.getConditionAtGeneration(),
                plan.getApprovalStatus());
    }
}
