package com.nutrition.dss.controller;

import com.nutrition.dss.model.*;
import com.nutrition.dss.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

/**
 * Main application controller for Thymeleaf pages.
 * Handles registration, login, dashboard, diet generation, and history.
 */
@Controller
public class AppController {

    private final UserService userService;
    private final RuleEngineService ruleEngineService;
    private final GroqService groqService;

    public AppController(UserService userService,
                         RuleEngineService ruleEngineService,
                         GroqService groqService) {
        this.userService = userService;
        this.ruleEngineService = ruleEngineService;
        this.groqService = groqService;
    }

    // ===================== HOME =====================
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ===================== REGISTER =====================
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model) {
        try {
            userService.register(fullName, email, password);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // ===================== LOGIN =====================
    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) {
            model.addAttribute("success", "Registration successful! Please log in.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        var userOpt = userService.login(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userRole", user.getRole());

            if (user.isAdmin()) {
                return "redirect:/admin";
            }
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    // ===================== DASHBOARD =====================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        var profileOpt = userService.getHealthProfile(user);
        profileOpt.ifPresent(p -> model.addAttribute("profile", p));

        return "dashboard";
    }

    // ===================== HEALTH PROFILE =====================
    @PostMapping("/profile")
    public String saveProfile(@RequestParam int age,
                              @RequestParam String gender,
                              @RequestParam double heightCm,
                              @RequestParam double weightKg,
                              @RequestParam String activityLevel,
                              @RequestParam String healthCondition,
                              @RequestParam String dietaryPreference,
                              @RequestParam(required = false, defaultValue = "") String allergies,
                              HttpSession session,
                              Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        userService.saveHealthProfile(user, age, gender, heightCm, weightKg,
                activityLevel, healthCondition, dietaryPreference, allergies);

        return "redirect:/dashboard?profileSaved=true";
    }

    // ===================== GENERATE DIET PLAN =====================
    @GetMapping("/generate")
    public String generatePlan(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        var profileOpt = userService.getHealthProfile(user);
        if (profileOpt.isEmpty()) {
            return "redirect:/dashboard?noProfile=true";
        }

        HealthProfile profile = profileOpt.get();

        // Run the rule engine
        Map<String, List<FoodItem>> plan = ruleEngineService.generateDietPlan(profile);

        // Save the plan to history
        ruleEngineService.savePlan(user, plan, profile);

        // Pass results to the HTML page
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("recommended", plan.get("RECOMMENDED"));
        model.addAttribute("limited", plan.get("LIMITED"));
        model.addAttribute("avoid", plan.get("AVOID"));
        model.addAttribute("groqAvailable", groqService.isAvailable());

        return "results";
    }

    // ===================== HISTORY =====================
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        var plans = ruleEngineService.getHistory(user);
        model.addAttribute("plans", plans);
        model.addAttribute("user", user);

        return "history";
    }

    // ===================== LOGOUT =====================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
