package com.nutrition.dss.controller;

import com.nutrition.dss.model.DietaryRule;
import com.nutrition.dss.model.FoodItem;
import com.nutrition.dss.repository.DietPlanRepository;
import com.nutrition.dss.repository.DietaryRuleRepository;
import com.nutrition.dss.repository.FoodItemRepository;
import com.nutrition.dss.service.DashboardService;
import com.nutrition.dss.model.DietPlan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * Admin controller for Thymeleaf pages.
 * Manages foods, rules, and displays dashboard statistics.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FoodItemRepository foodItemRepository;
    private final DietaryRuleRepository dietaryRuleRepository;
    private final DietPlanRepository dietPlanRepository;
    private final DashboardService dashboardService;

    public AdminController(FoodItemRepository foodItemRepository,
                           DietaryRuleRepository dietaryRuleRepository,
                           DietPlanRepository dietPlanRepository,
                           DashboardService dashboardService) {
        this.foodItemRepository = foodItemRepository;
        this.dietaryRuleRepository = dietaryRuleRepository;
        this.dietPlanRepository = dietPlanRepository;
        this.dashboardService = dashboardService;
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("userRole"));
    }

    // ===================== ADMIN DASHBOARD =====================
    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("foods", foodItemRepository.findAll());
        model.addAttribute("rules", dietaryRuleRepository.findAll());
        model.addAttribute("dashboard", dashboardService.getDashboardStats());
        
        // Add pending plans for review
        model.addAttribute("pendingPlans", dietPlanRepository.findAll().stream()
                .filter(p -> "PENDING".equals(p.getApprovalStatus()))
                .toList());
                
        return "admin";
    }

    // ===================== ADD FOOD =====================
    @PostMapping("/food/add")
    public String addFood(@RequestParam String name,
                          @RequestParam String category,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false, defaultValue = "0") int caloriesPer100g,
                          @RequestParam(required = false, defaultValue = "0") double protein,
                          @RequestParam(required = false, defaultValue = "0") double carbs,
                          @RequestParam(required = false, defaultValue = "0") double fats,
                          @RequestParam(required = false, defaultValue = "VEG") String type,
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        FoodItem food = new FoodItem(name, category, description, caloriesPer100g, protein, carbs, fats, type);
        foodItemRepository.save(food);
        return "redirect:/admin?foodAdded=true";
    }

    // ===================== DELETE FOOD =====================
    @PostMapping("/food/delete/{id}")
    public String deleteFood(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        foodItemRepository.deleteById(id);
        return "redirect:/admin";
    }

    // ===================== ADD RULE =====================
    @PostMapping("/rule/add")
    public String addRule(@RequestParam String condition,
                          @RequestParam String foodCategory,
                          @RequestParam String recommendation,
                          @RequestParam int priority,
                          @RequestParam(required = false) String conditionExpression,
                          @RequestParam(required = false) String actionType,
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        DietaryRule rule = new DietaryRule(condition, foodCategory, recommendation, priority,
                conditionExpression, actionType);
        dietaryRuleRepository.save(rule);
        return "redirect:/admin?ruleAdded=true";
    }

    // ===================== DELETE RULE =====================
    @PostMapping("/rule/delete/{id}")
    public String deleteRule(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        dietaryRuleRepository.deleteById(id);
        return "redirect:/admin";
    }
    // ===================== REVIEW PLANS =====================
    @PostMapping("/plan/approve/{id}")
    public String approvePlan(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        DietPlan plan = dietPlanRepository.findById(id).orElse(null);
        if (plan != null) {
            plan.setApprovalStatus("APPROVED (Manual)");
            dietPlanRepository.save(plan);
        }
        return "redirect:/admin?planReviewed=true";
    }

    @PostMapping("/plan/reject/{id}")
    public String rejectPlan(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        DietPlan plan = dietPlanRepository.findById(id).orElse(null);
        if (plan != null) {
            plan.setApprovalStatus("REJECTED (Manual)");
            dietPlanRepository.save(plan);
        }
        return "redirect:/admin?planReviewed=true";
    }
}
