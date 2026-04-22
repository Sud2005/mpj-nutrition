package com.nutrition.dss;

import com.nutrition.dss.model.*;
import com.nutrition.dss.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 *  DATA SEEDER — Runs automatically when the app starts!
 *  This fills the database with sample food items and rules.
 *
 *  HOW TO TINKER:
 *  - Add new FoodItem entries (new foods for users to see)
 *  - Add new DietaryRule entries (new rules for conditions)
 *  - Change an existing rule from "AVOID" to "LIMITED"
 *    → Reload the app and generate a plan to see the change!
 *  - Add a new condition "ANEMIA" with rules for PROTEINS → RECOMMENDED
 *
 *  RULES EXPLANATION:
 *  new DietaryRule("CONDITION", "FOOD_CATEGORY", "RECOMMENDATION", priority)
 *  Example: new DietaryRule("DIABETES", "SWEETS", "AVOID", 10)
 *  → For diabetics, sweets should be AVOIDED (priority 10)
 * ============================================================
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private FoodItemRepository foodItemRepository;
    @Autowired private DietaryRuleRepository dietaryRuleRepository;

    @Override
    public void run(String... args) throws Exception {

        // =============================================
        //  SEED ADMIN USER
        //  Login: admin@dss.com / admin123
        // =============================================
        if (userRepository.findByEmail("admin@dss.com").isEmpty()) {
            userRepository.save(new User("Admin", "admin@dss.com", "admin123", "ADMIN"));
        }

        // Demo user
        if (userRepository.findByEmail("demo@dss.com").isEmpty()) {
            userRepository.save(new User("Demo User", "demo@dss.com", "demo123", "USER"));
        }

        // =============================================
        //  SEED FOOD ITEMS
        //  TINKER: Add more foods here!
        //  Format: new FoodItem("Name", "CATEGORY", "Description")
        // =============================================
        if (foodItemRepository.count() == 0) {

            // GRAINS
            foodItemRepository.save(new FoodItem("Brown Rice", "GRAINS", "Whole grain, high fibre"));
            foodItemRepository.save(new FoodItem("White Rice", "GRAINS", "Refined grain, lower fibre"));
            foodItemRepository.save(new FoodItem("Oats", "GRAINS", "High fibre, good for heart"));
            foodItemRepository.save(new FoodItem("Whole Wheat Bread", "GRAINS", "Better than white bread"));
            foodItemRepository.save(new FoodItem("White Bread", "GRAINS", "Refined flour, low fibre"));

            // PROTEINS
            foodItemRepository.save(new FoodItem("Chicken Breast", "PROTEINS", "Lean protein source"));
            foodItemRepository.save(new FoodItem("Eggs", "PROTEINS", "Complete protein"));
            foodItemRepository.save(new FoodItem("Lentils (Dal)", "PROTEINS", "Plant-based protein, high fibre"));
            foodItemRepository.save(new FoodItem("Paneer", "PROTEINS", "High protein dairy"));
            foodItemRepository.save(new FoodItem("Fish (Salmon)", "PROTEINS", "Rich in Omega-3"));

            // VEGETABLES
            foodItemRepository.save(new FoodItem("Spinach", "VEGETABLES", "Iron-rich leafy green"));
            foodItemRepository.save(new FoodItem("Broccoli", "VEGETABLES", "High in vitamins C and K"));
            foodItemRepository.save(new FoodItem("Carrot", "VEGETABLES", "Rich in beta-carotene"));
            foodItemRepository.save(new FoodItem("Bitter Gourd", "VEGETABLES", "Helps regulate blood sugar"));
            foodItemRepository.save(new FoodItem("Potato", "VEGETABLES", "Starchy, high GI vegetable"));

            // FRUITS
            foodItemRepository.save(new FoodItem("Apple", "FRUITS", "High fibre, low sugar"));
            foodItemRepository.save(new FoodItem("Banana", "FRUITS", "High potassium, good energy"));
            foodItemRepository.save(new FoodItem("Mango", "FRUITS", "High sugar content"));
            foodItemRepository.save(new FoodItem("Guava", "FRUITS", "Low GI, high vitamin C"));
            foodItemRepository.save(new FoodItem("Watermelon", "FRUITS", "Hydrating, moderate sugar"));

            // DAIRY
            foodItemRepository.save(new FoodItem("Low-Fat Milk", "DAIRY", "Good calcium source"));
            foodItemRepository.save(new FoodItem("Full-Fat Milk", "DAIRY", "High fat dairy"));
            foodItemRepository.save(new FoodItem("Yogurt (Curd)", "DAIRY", "Probiotic-rich"));
            foodItemRepository.save(new FoodItem("Cheese", "DAIRY", "High in saturated fat"));

            // FATS
            foodItemRepository.save(new FoodItem("Olive Oil", "FATS", "Healthy monounsaturated fat"));
            foodItemRepository.save(new FoodItem("Coconut Oil", "FATS", "High saturated fat"));
            foodItemRepository.save(new FoodItem("Butter", "FATS", "High saturated fat"));
            foodItemRepository.save(new FoodItem("Almonds", "FATS", "Healthy nuts, good fats"));

            // SWEETS
            foodItemRepository.save(new FoodItem("White Sugar", "SWEETS", "Refined sugar, no nutrients"));
            foodItemRepository.save(new FoodItem("Honey", "SWEETS", "Natural sweetener, use sparingly"));
            foodItemRepository.save(new FoodItem("Jaggery", "SWEETS", "Less refined than white sugar"));
            foodItemRepository.save(new FoodItem("Sweets / Mithai", "SWEETS", "High sugar and fat"));

            System.out.println("✅ Food items seeded successfully!");
        }

        // =============================================
        //  SEED DIETARY RULES
        //  TINKER: Change recommendations and priorities!
        //  Try changing "AVOID" to "LIMITED" for diabetes/sweets
        //  and see the results page update.
        // =============================================
        if (dietaryRuleRepository.count() == 0) {

            // --- GENERAL RULES (apply to EVERYONE with condition = "NONE") ---
            dietaryRuleRepository.save(new DietaryRule("NONE", "VEGETABLES", "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "PROTEINS",   "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "GRAINS",     "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "FRUITS",     "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "DAIRY",      "RECOMMENDED", 1));
            dietaryRuleRepository.save(new DietaryRule("NONE", "FATS",       "LIMITED",     2));
            dietaryRuleRepository.save(new DietaryRule("NONE", "SWEETS",     "LIMITED",     2));

            // --- DIABETES RULES ---
            // TINKER: Change priority to see which rules win in conflicts
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "SWEETS",      "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "GRAINS",      "LIMITED",     8));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "FRUITS",      "LIMITED",     7));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "VEGETABLES",  "RECOMMENDED", 9));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "PROTEINS",    "RECOMMENDED", 8));
            dietaryRuleRepository.save(new DietaryRule("DIABETES", "FATS",        "LIMITED",     6));

            // --- HYPERTENSION (HIGH BLOOD PRESSURE) RULES ---
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "FATS",       "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "SWEETS",     "AVOID",       9));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "DAIRY",      "LIMITED",     7));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "VEGETABLES", "RECOMMENDED", 9));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "PROTEINS",   "RECOMMENDED", 8));
            dietaryRuleRepository.save(new DietaryRule("HYPERTENSION", "GRAINS",     "RECOMMENDED", 7));

            // --- OBESITY RULES ---
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "SWEETS", "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "FATS",   "AVOID",       10));
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "GRAINS", "LIMITED",     7));
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "DAIRY",  "LIMITED",     6));
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "VEGETABLES", "RECOMMENDED", 9));
            dietaryRuleRepository.save(new DietaryRule("OBESITY", "PROTEINS",   "RECOMMENDED", 8));

            System.out.println("✅ Dietary rules seeded successfully!");
        }

        System.out.println("===========================================");
        System.out.println("  🥦 Nutrition DSS is ready!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("  Admin: admin@dss.com / admin123");
        System.out.println("  Demo:  demo@dss.com  / demo123");
        System.out.println("  DB Console: http://localhost:8080/h2-console");
        System.out.println("===========================================");
    }
}
