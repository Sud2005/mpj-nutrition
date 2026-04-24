package com.nutrition.dss.service;

import com.nutrition.dss.dto.UserDTO;
import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.model.User;
import com.nutrition.dss.model.WeightMeasurement;
import com.nutrition.dss.repository.HealthProfileRepository;
import com.nutrition.dss.repository.UserRepository;
import com.nutrition.dss.repository.WeightMeasurementRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handles user registration, login, and profile management.
 * Passwords are BCrypt hashed.
 */
@Service
public class UserService {
    private static final double HEIGHT_TOLERANCE_CM = 0.01;
    private static final double WEIGHT_TOLERANCE_KG = 0.01;

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final WeightMeasurementRepository weightMeasurementRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       HealthProfileRepository healthProfileRepository,
                       WeightMeasurementRepository weightMeasurementRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.weightMeasurementRepository = weightMeasurementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Register a new user with BCrypt-hashed password */
    public User register(String fullName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered: " + email);
        }
        if (password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (!email.contains("@")) {
            throw new RuntimeException("Invalid email format");
        }

        User user = new User(fullName, email, passwordEncoder.encode(password), "USER");
        return userRepository.save(user);
    }

    /** Login: find user by email, verify password with BCrypt */
    public Optional<User> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public HealthProfile saveHealthProfile(User user, int age, String gender,
                                           double heightCm, double weightKg,
                                           String activityLevel, String healthCondition,
                                           String dietaryPreference, String allergies) {
        Optional<HealthProfile> existing = healthProfileRepository.findByUser(user);
        HealthProfile profile = existing.orElse(new HealthProfile());

        profile.setUser(user);
        profile.setAge(age);
        profile.setGender(gender);
        profile.setHeightCm(heightCm);
        profile.setWeightKg(weightKg);
        profile.setActivityLevel(activityLevel);
        profile.setHealthCondition(healthCondition);
        profile.setDietaryPreference(dietaryPreference);
        profile.setAllergies(allergies != null ? allergies : "");
        profile.calculateBMI();

        HealthProfile saved = healthProfileRepository.save(profile);
        saveWeightMeasurementSnapshot(user, saved, "MANUAL_PROFILE_UPDATE");
        return saved;
    }

    public Optional<HealthProfile> getHealthProfile(User user) {
        return healthProfileRepository.findByUser(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<WeightMeasurement> getRecentWeightMeasurements(User user) {
        return weightMeasurementRepository.findTop10ByUserOrderByMeasuredAtDesc(user);
    }

    /** Convert User entity to DTO (no password exposed) */
    public UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole(), user.getCreatedAt());
    }

    private void saveWeightMeasurementSnapshot(User user, HealthProfile profile, String source) {
        Optional<WeightMeasurement> latestOpt = weightMeasurementRepository.findFirstByUserOrderByMeasuredAtDesc(user);
        if (latestOpt.isPresent()) {
            WeightMeasurement latest = latestOpt.get();
            boolean sameHeight = Math.abs(latest.getHeightCm() - profile.getHeightCm()) < HEIGHT_TOLERANCE_CM;
            boolean sameWeight = Math.abs(latest.getWeightKg() - profile.getWeightKg()) < WEIGHT_TOLERANCE_KG;
            if (sameHeight && sameWeight) {
                return;
            }
        }

        WeightMeasurement measurement = new WeightMeasurement(
                user,
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getBmi(),
                source
        );
        weightMeasurementRepository.save(measurement);
    }
}
