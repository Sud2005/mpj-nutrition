package com.nutrition.dss.service;

import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.model.User;
import com.nutrition.dss.repository.HealthProfileRepository;
import com.nutrition.dss.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * ============================================================
 *  PERSON 1 - SERVICE LAYER
 *  Handles user registration, login, and profile management.
 *
 *  HOW TO TINKER:
 *  - Add password validation rules in register()
 *    e.g. password must be 8+ characters
 *  - Add email format validation
 *  - Add account locking after 3 failed login attempts
 * ============================================================
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthProfileRepository healthProfileRepository;

    /**
     * Register a new user.
     * Returns the saved User, or throws an exception if email is taken.
     *
     * TINKER: Add password strength check here!
     * e.g. if (password.length() < 8) throw new RuntimeException("Password too short");
     */
    public User register(String fullName, String email, String password) {
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }

        // TINKER: Add validation
        // if (password.length() < 6) throw new RuntimeException("Password must be 6+ characters");
        // if (!email.contains("@")) throw new RuntimeException("Invalid email");

        User user = new User(fullName, email, password, "USER");
        return userRepository.save(user);
    }

    /**
     * Login: find user by email + password.
     * Returns the User if credentials match, or empty if not.
     */
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    /**
     * Save or update a user's health profile.
     * Automatically calculates BMI.
     */
    public HealthProfile saveHealthProfile(User user, int age, String gender,
                                           double heightCm, double weightKg,
                                           String activityLevel, String healthCondition) {
        // Check if profile already exists
        Optional<HealthProfile> existing = healthProfileRepository.findByUser(user);
        HealthProfile profile = existing.orElse(new HealthProfile());

        profile.setUser(user);
        profile.setAge(age);
        profile.setGender(gender);
        profile.setHeightCm(heightCm);
        profile.setWeightKg(weightKg);
        profile.setActivityLevel(activityLevel);
        profile.setHealthCondition(healthCondition);

        // BMI is calculated here!
        profile.calculateBMI();

        return healthProfileRepository.save(profile);
    }

    /**
     * Get a user's health profile.
     */
    public Optional<HealthProfile> getHealthProfile(User user) {
        return healthProfileRepository.findByUser(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
