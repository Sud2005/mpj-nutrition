package com.nutrition.dss.controller.api;

import com.nutrition.dss.dto.DietOutputDTO;
import com.nutrition.dss.dto.HealthProfileDTO;
import com.nutrition.dss.exception.ResourceNotFoundException;
import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.model.User;
import com.nutrition.dss.repository.UserRepository;
import com.nutrition.dss.service.RuleEngineService;
import com.nutrition.dss.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for diet plan generation.
 * Requires JWT authentication.
 */
@RestController
@RequestMapping("/api/diet")
public class DietApiController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RuleEngineService ruleEngineService;

    public DietApiController(UserService userService,
                             UserRepository userRepository,
                             RuleEngineService ruleEngineService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.ruleEngineService = ruleEngineService;
    }

    /**
     * POST /api/diet/generate
     * Generate a diet plan for the authenticated user.
     * Optionally accepts profile overrides in the request body.
     */
    @PostMapping("/generate")
    public ResponseEntity<DietOutputDTO> generatePlan(
            @Valid @RequestBody(required = false) HealthProfileDTO profileOverride,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        HealthProfile profile;
        if (profileOverride != null && profileOverride.getAge() > 0) {
            profile = userService.saveHealthProfile(user,
                    profileOverride.getAge(), profileOverride.getGender(),
                    profileOverride.getHeightCm(), profileOverride.getWeightKg(),
                    profileOverride.getActivityLevel(), profileOverride.getHealthCondition(),
                    profileOverride.getDietaryPreference(), profileOverride.getAllergies());
        } else {
            profile = userService.getHealthProfile(user)
                    .orElseThrow(() -> new RuntimeException("Health profile not found. Save a profile first."));
        }

        // Generate structured output
        DietOutputDTO output = ruleEngineService.generateDietOutputDTO(profile);

        // Save to history
        var plan = ruleEngineService.generateDietPlan(profile);
        ruleEngineService.savePlan(user, plan, profile);

        return ResponseEntity.ok(output);
    }
}
