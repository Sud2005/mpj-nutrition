package com.nutrition.dss;

import com.nutrition.dss.model.*;
import com.nutrition.dss.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final DietaryRuleRepository dietaryRuleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      FoodItemRepository foodItemRepository,
                      DietaryRuleRepository dietaryRuleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.foodItemRepository = foodItemRepository;
        this.dietaryRuleRepository = dietaryRuleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // =============================================
        //  SEED USERS (passwords are BCrypt hashed)
        //  Login: admin@dss.com / admin123
        //  Login: demo@dss.com  / demo123
        // =============================================
        if (userRepository.findByEmail("admin@dss.com").isEmpty()) {
            userRepository.save(new User("Admin", "admin@dss.com",
                    passwordEncoder.encode("admin123"), "ADMIN"));
        }
        if (userRepository.findByEmail("demo@dss.com").isEmpty()) {
            userRepository.save(new User("Demo User", "demo@dss.com",
                    passwordEncoder.encode("demo123"), "USER"));
        }

        // =============================================
        //  SEED FOOD ITEMS (with macronutrients)
        //  Format: name, category, description, kcal/100g, protein, carbs, fats, type
        // =============================================
        if (foodItemRepository.count() == 0) {

            // GRAINS
            foodItemRepository.save(new FoodItem("Brown Rice", "GRAINS", "Whole grain, high fibre", 111, 2.6, 23.0, 0.9, "VEG"));
            foodItemRepository.save(new FoodItem("White Rice", "GRAINS", "Refined grain, lower fibre", 130, 2.7, 28.2, 0.3, "VEG"));
            foodItemRepository.save(new FoodItem("Oats", "GRAINS", "High fibre, good for heart", 389, 16.9, 66.3, 6.9, "VEG"));
            foodItemRepository.save(new FoodItem("Whole Wheat Bread", "GRAINS", "Better than white bread", 250, 13.0, 43.0, 3.4, "VEG"));
            foodItemRepository.save(new FoodItem("White Bread", "GRAINS", "Refined flour, low fibre", 265, 9.0, 49.0, 3.2, "VEG"));

            // PROTEINS
            foodItemRepository.save(new FoodItem("Chicken Breast", "PROTEINS", "Lean protein source", 165, 31.0, 0.0, 3.6, "NON_VEG"));
            foodItemRepository.save(new FoodItem("Eggs", "PROTEINS", "Complete protein", 155, 13.0, 1.1, 11.0, "NON_VEG"));
            foodItemRepository.save(new FoodItem("Lentils (Dal)", "PROTEINS", "Plant-based protein, high fibre", 116, 9.0, 20.0, 0.4, "VEG"));
            foodItemRepository.save(new FoodItem("Paneer", "PROTEINS", "High protein dairy", 265, 18.3, 1.2, 20.8, "VEG"));
            foodItemRepository.save(new FoodItem("Fish (Salmon)", "PROTEINS", "Rich in Omega-3", 208, 20.0, 0.0, 13.0, "NON_VEG"));

            // VEGETABLES
            foodItemRepository.save(new FoodItem("Spinach", "VEGETABLES", "Iron-rich leafy green", 23, 2.9, 3.6, 0.4, "VEG"));
            foodItemRepository.save(new FoodItem("Broccoli", "VEGETABLES", "High in vitamins C and K", 34, 2.8, 7.0, 0.4, "VEG"));
            foodItemRepository.save(new FoodItem("Carrot", "VEGETABLES", "Rich in beta-carotene", 41, 0.9, 10.0, 0.2, "VEG"));
            foodItemRepository.save(new FoodItem("Bitter Gourd", "VEGETABLES", "Helps regulate blood sugar", 17, 1.0, 3.7, 0.2, "VEG"));
            foodItemRepository.save(new FoodItem("Potato", "VEGETABLES", "Starchy, high GI vegetable", 86, 1.7, 20.0, 0.1, "VEG"));

            // FRUITS
            foodItemRepository.save(new FoodItem("Apple", "FRUITS", "High fibre, low sugar", 52, 0.3, 14.0, 0.2, "VEG"));
            foodItemRepository.save(new FoodItem("Banana", "FRUITS", "High potassium, good energy", 89, 1.1, 23.0, 0.3, "VEG"));
            foodItemRepository.save(new FoodItem("Mango", "FRUITS", "High sugar content", 60, 0.8, 15.0, 0.4, "VEG"));
            foodItemRepository.save(new FoodItem("Guava", "FRUITS", "Low GI, high vitamin C", 68, 2.6, 14.0, 1.0, "VEG"));
            foodItemRepository.save(new FoodItem("Watermelon", "FRUITS", "Hydrating, moderate sugar", 30, 0.6, 8.0, 0.2, "VEG"));

            // DAIRY
            foodItemRepository.save(new FoodItem("Low-Fat Milk", "DAIRY", "Good calcium source", 42, 3.4, 5.0, 1.0, "VEG"));
            foodItemRepository.save(new FoodItem("Full-Fat Milk", "DAIRY", "High fat dairy", 61, 3.2, 4.8, 3.3, "VEG"));
            foodItemRepository.save(new FoodItem("Yogurt (Curd)", "DAIRY", "Probiotic-rich", 98, 11.0, 3.6, 5.0, "VEG"));
            foodItemRepository.save(new FoodItem("Cheese", "DAIRY", "High in saturated fat", 402, 25.0, 1.3, 33.0, "VEG"));

            // FATS
            foodItemRepository.save(new FoodItem("Olive Oil", "FATS", "Healthy monounsaturated fat", 884, 0.0, 0.0, 100.0, "VEG"));
            foodItemRepository.save(new FoodItem("Coconut Oil", "FATS", "High saturated fat", 862, 0.0, 0.0, 100.0, "VEG"));
            foodItemRepository.save(new FoodItem("Butter", "FATS", "High saturated fat", 717, 0.9, 0.1, 81.0, "VEG"));
            foodItemRepository.save(new FoodItem("Almonds", "FATS", "Healthy nuts, good fats", 579, 21.2, 21.6, 49.9, "VEG"));

            // SWEETS
            foodItemRepository.save(new FoodItem("White Sugar", "SWEETS", "Refined sugar, no nutrients", 387, 0.0, 100.0, 0.0, "VEG"));
            foodItemRepository.save(new FoodItem("Honey", "SWEETS", "Natural sweetener, use sparingly", 304, 0.3, 82.0, 0.0, "VEG"));
            foodItemRepository.save(new FoodItem("Jaggery", "SWEETS", "Less refined than white sugar", 383, 0.4, 98.0, 0.1, "VEG"));
            foodItemRepository.save(new FoodItem("Sweets / Mithai", "SWEETS", "High sugar and fat", 450, 5.0, 60.0, 22.0, "VEG"));

            System.out.println("✅ Food items seeded (with macronutrients)!");
        }

        // =============================================
        //  SEED DIETARY RULES (condition-based + expression-based)
        // =============================================
        if (dietaryRuleRepository.count() == 0) {

            // --- General rules (NONE = everyone) ---
            dietaryRuleRepository.save(new DietaryRule("NONE", "VEGETABLES", "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "PROTEINS",   "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "GRAINS",     "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "FRUITS",     "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "DAIRY",      "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "FATS",       "LIMITED",     2));
            dietaryRuleRepository.save(new DietaryRule("NONE", "SWEETS",     "LIMITED",     2));

            // --- Diabetes rules ---
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "SWEETS",      "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "GRAINS",      "LIMITED",     8));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "FRUITS",      "LIMITED",     7));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "VEGETABLES",  "RECOMMENDED", 9));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "PROTEINS",    "RECOMMENDED", 8));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "FATS",        "LIMITED",     6));

            // --- Hypertension rules ---
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "FATS",       "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "SWEETS",     "AVOID",       9));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "DAIRY",      "LIMITED",     7));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "VEGETABLES", "RECOMMENDED", 9));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "PROTEINS",   "RECOMMENDED", 8));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "GRAINS",     "RECOMMENDED", 7));

            // --- Dynamic expression-based rules (BMI/age triggers) ---
            dietaryRuleRepository.save(new DietaryRule("NONE", "FATS", "AVOID", 8,
                    "BMI >= 30", "Restrict high-fat foods for obese BMI"));
            dietaryRuleRepository.save(new DietaryRule("NONE", "SWEETS", "AVOID", 8,
                    "BMI >= 30", "Restrict sugary foods for obese BMI"));
            dietaryRuleRepository.save(new DietaryRule("NONE", "DAIRY", "RECOMMENDED", 5,
                    "age > 60", "Extra calcium for seniors"));
            dietaryRuleRepository.save(new DietaryRule("NONE", "FATS", "AVOID", 7,
                    "BMI > 25", "Reduce fats for overweight BMI"));

            System.out.println("✅ Dietary rules seeded (with dynamic expressions)!");
        }

        System.out.println("===========================================");
        System.out.println("  🥦 Nutrition DSS v2.0 is ready!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("  Admin: admin@dss.com / admin123");
        System.out.println("  Demo:  demo@dss.com  / demo123");
        System.out.println("  DB Console: http://localhost:8080/h2-console");
        System.out.println("  API Base: http://localhost:8080/api/");
        System.out.println("===========================================");
    }
}
