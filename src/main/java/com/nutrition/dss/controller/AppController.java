package com.nutrition.dss.controller;

import com.nutrition.dss.model.*;
import com.nutrition.dss.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

/**
 * ============================================================
 *  PERSON 4 - CONTROLLER LAYER
 *  Controllers handle HTTP requests and return web pages.
 *  Think of them as the "traffic director" of the app.
 *
 *  URL MAPPING (what URL → what method):
 *  GET  /           → home page
 *  GET  /register   → show register form
 *  POST /register   → process registration
 *  GET  /login      → show login form
 *  POST /login      → process login
 *  GET  /dashboard  → user dashboard
 *  POST /profile    → save health profile
 *  GET  /generate   → generate diet plan
 *  GET  /history    → view past plans
 *  GET  /logout     → logout
 *
 *  HOW TO TINKER:
 *  - Add a new page: add a @GetMapping + create an HTML file
 *  - Change redirect after login from /dashboard to /profile
 *  - Add a flash message on successful registration
 * ============================================================
 */
@Controller
public class AppController {

    @Autowired
    private UserService userService;

    @Autowired
    private RuleEngineService ruleEngineService;

    // ===================== HOME =====================
    @GetMapping("/")
    public String home() {
        return "index";  // shows index.html
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
            // TINKER: Change this redirect to go anywhere after registration
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

            // TINKER: Redirect admins to a different page
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

        // Check if profile exists
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
                              HttpSession session,
                              Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        userService.saveHealthProfile(user, age, gender, heightCm, weightKg,
                activityLevel, healthCondition);

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

        // *** THIS IS WHERE THE RULE ENGINE RUNS ***
        Map<String, List<FoodItem>> plan = ruleEngineService.generateDietPlan(profile);

        // Save the plan to history
        ruleEngineService.savePlan(user, plan, profile);

        // Pass results to the HTML page
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("recommended", plan.get("RECOMMENDED"));
        model.addAttribute("limited", plan.get("LIMITED"));
        model.addAttribute("avoid", plan.get("AVOID"));

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
