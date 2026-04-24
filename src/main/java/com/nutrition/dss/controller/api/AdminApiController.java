package com.nutrition.dss.controller.api;

import com.nutrition.dss.dto.DashboardDTO;
import com.nutrition.dss.dto.FoodItemDTO;
import com.nutrition.dss.dto.RuleDTO;
import com.nutrition.dss.exception.ResourceNotFoundException;
import com.nutrition.dss.model.DietaryRule;
import com.nutrition.dss.model.FoodItem;
import com.nutrition.dss.repository.DietaryRuleRepository;
import com.nutrition.dss.repository.FoodItemRepository;
import com.nutrition.dss.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for admin operations.
 * Requires ADMIN role (enforced by SecurityConfig).
 * Endpoints: dashboard stats, food CRUD, rule CRUD.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final DashboardService dashboardService;
    private final FoodItemRepository foodItemRepository;
    private final DietaryRuleRepository dietaryRuleRepository;

    public AdminApiController(DashboardService dashboardService,
                              FoodItemRepository foodItemRepository,
                              DietaryRuleRepository dietaryRuleRepository) {
        this.dashboardService = dashboardService;
        this.foodItemRepository = foodItemRepository;
        this.dietaryRuleRepository = dietaryRuleRepository;
    }

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    // ==================== FOOD ITEMS ====================

    @GetMapping("/foods")
    public ResponseEntity<List<FoodItemDTO>> getAllFoods() {
        List<FoodItemDTO> foods = foodItemRepository.findAll().stream()
                .map(this::toFoodDTO)
                .toList();
        return ResponseEntity.ok(foods);
    }

    @PostMapping("/foods")
    public ResponseEntity<FoodItemDTO> addFood(@RequestBody FoodItemDTO dto) {
        FoodItem food = new FoodItem(dto.getName(), dto.getCategory(), dto.getDescription(),
                dto.getCaloriesPer100g(), dto.getProtein(), dto.getCarbs(), dto.getFats(),
                dto.getType() != null ? dto.getType() : "VEG");
        food = foodItemRepository.save(food);
        return ResponseEntity.ok(toFoodDTO(food));
    }

    @PutMapping("/foods/{id}")
    public ResponseEntity<FoodItemDTO> updateFood(@PathVariable Long id, @RequestBody FoodItemDTO dto) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", id));

        food.setName(dto.getName());
        food.setCategory(dto.getCategory());
        food.setDescription(dto.getDescription());
        food.setCaloriesPer100g(dto.getCaloriesPer100g());
        food.setProtein(dto.getProtein());
        food.setCarbs(dto.getCarbs());
        food.setFats(dto.getFats());
        if (dto.getType() != null) food.setType(dto.getType());

        food = foodItemRepository.save(food);
        return ResponseEntity.ok(toFoodDTO(food));
    }

    @DeleteMapping("/foods/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("FoodItem", id);
        }
        foodItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== DIETARY RULES ====================

    @GetMapping("/rules")
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        List<RuleDTO> rules = dietaryRuleRepository.findAll().stream()
                .map(this::toRuleDTO)
                .toList();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/rules")
    public ResponseEntity<RuleDTO> addRule(@RequestBody RuleDTO dto) {
        DietaryRule rule = new DietaryRule(dto.getCondition(), dto.getFoodCategory(),
                dto.getRecommendation(), dto.getPriority(),
                dto.getConditionExpression(), dto.getActionType());
        rule = dietaryRuleRepository.save(rule);
        return ResponseEntity.ok(toRuleDTO(rule));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable Long id, @RequestBody RuleDTO dto) {
        DietaryRule rule = dietaryRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DietaryRule", id));

        rule.setCondition(dto.getCondition());
        rule.setFoodCategory(dto.getFoodCategory());
        rule.setRecommendation(dto.getRecommendation());
        rule.setPriority(dto.getPriority());
        rule.setConditionExpression(dto.getConditionExpression());
        rule.setActionType(dto.getActionType());

        rule = dietaryRuleRepository.save(rule);
        return ResponseEntity.ok(toRuleDTO(rule));
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        if (!dietaryRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("DietaryRule", id);
        }
        dietaryRuleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MAPPERS ====================

    private FoodItemDTO toFoodDTO(FoodItem f) {
        return new FoodItemDTO(f.getId(), f.getName(), f.getCategory(), f.getDescription(),
                f.getCaloriesPer100g(), f.getProtein(), f.getCarbs(), f.getFats(),
                f.getType(), f.getCalorieLabel(), null);
    }

    private RuleDTO toRuleDTO(DietaryRule r) {
        RuleDTO dto = new RuleDTO();
        dto.setId(r.getId());
        dto.setCondition(r.getCondition());
        dto.setFoodCategory(r.getFoodCategory());
        dto.setRecommendation(r.getRecommendation());
        dto.setPriority(r.getPriority());
        dto.setConditionExpression(r.getConditionExpression());
        dto.setActionType(r.getActionType());
        return dto;
    }
}
