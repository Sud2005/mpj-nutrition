package com.nutrition.dss.controller;

import com.nutrition.dss.model.DietaryRule;
import com.nutrition.dss.model.FoodItem;
import com.nutrition.dss.repository.DietaryRuleRepository;
import com.nutrition.dss.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

/**
 * ============================================================
 *  PERSON 4 - ADMIN CONTROLLER
 *  Handles admin-only pages: managing foods and rules.
 *
 *  URL MAPPING:
 *  GET  /admin            → admin dashboard
 *  POST /admin/food/add   → add a food item
 *  POST /admin/food/delete/{id} → delete a food
 *  POST /admin/rule/add   → add a dietary rule
 *  POST /admin/rule/delete/{id} → delete a rule
 *
 *  HOW TO TINKER:
 *  - Add a /admin/food/edit/{id} endpoint
 *  - Add input validation before saving
 *  - Add a success/error message system
 * ============================================================
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private DietaryRuleRepository dietaryRuleRepository;

    // Helper: check if logged in user is admin
    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("userRole"));
    }

    // ===================== ADMIN DASHBOARD =====================
    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("foods", foodItemRepository.findAll());
        model.addAttribute("rules", dietaryRuleRepository.findAll());
        return "admin";
    }

    // ===================== ADD FOOD =====================
    @PostMapping("/food/add")
    public String addFood(@RequestParam String name,
                          @RequestParam String category,
                          @RequestParam(required = false) String description,
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        FoodItem food = new FoodItem(name, category, description);
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
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        DietaryRule rule = new DietaryRule(condition, foodCategory, recommendation, priority);
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
}
