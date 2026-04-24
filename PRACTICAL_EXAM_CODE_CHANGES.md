---
# PRACTICAL EXAM: CODE CHANGES
---

## 1. Multi-Disease Support & Priority Rule Resolution

FILE: `src/main/java/com/nutrition/dss/service/RuleEngineService.java`

LOCATION: `generateDietPlan(HealthProfile profile)` method

**BEFORE:**
```java
List<FoodItem> allFoods = foodItemRepository.findAll();
// ...
for (DietaryRule rule : conditionRules) {
    int currentPriority = categoryPriority.getOrDefault(rule.getFoodCategory(), -1);
    if (rule.getPriority() > currentPriority) {
        categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
        categoryPriority.put(rule.getFoodCategory(), rule.getPriority());
    }
}
```

**AFTER:**
```java
List<FoodItem> allFoodsDb = foodItemRepository.findAll();
String preference = profile.getDietaryPreference();
if (preference == null) preference = "VEG"; 
List<FoodItem> allFoods = new ArrayList<>();
for(FoodItem f : allFoodsDb) {
    if (preference.equals(f.getType())) {
        allFoods.add(f);
    }
}
// ...
Map<String, DietaryRule> winningRules = new HashMap<>();
Map<String, String> categoryJustification = new HashMap<>();
// ...
for (DietaryRule rule : conditionRules) {
    int currentPriority = categoryPriority.getOrDefault(rule.getFoodCategory(), -1);
    if (rule.getPriority() > currentPriority) {
        DietaryRule currentWinner = winningRules.get(rule.getFoodCategory());
        
        categoryDecision.put(rule.getFoodCategory(), rule.getRecommendation());
        categoryPriority.put(rule.getFoodCategory(), rule.getPriority());
        winningRules.put(rule.getFoodCategory(), rule);
        
        String just = rule.getFoodCategory() + " " + rule.getRecommendation().toLowerCase() + " because " + rule.getCondition() + " rule";
        if (currentWinner != null && !currentWinner.getCondition().equals("NONE")) {
            just += " has higher priority than " + currentWinner.getCondition() + " rule";
        } else {
            just += " applies";
        }
        categoryJustification.put(rule.getFoodCategory(), just);
    }
}
```

**WHAT CHANGED:**
Added a filter for dietary preference. Updated the rule loop to track *which* rule wins a conflict and to automatically construct a dynamic string explaining the justification behind the recommendation based on priority scores.

---

## 2. Dietary Preference (VEG/NON-VEG)

FILE: `src/main/java/com/nutrition/dss/model/HealthProfile.java`

LOCATION: Class properties

**BEFORE:**
```java
    private String healthCondition = "NONE";
```

**AFTER:**
```java
    private String healthCondition = "NONE";
    private String dietaryPreference = "VEG"; // VEG or NON_VEG
```

**WHAT CHANGED:**
Added `dietaryPreference` to save the user's choice to the database.

---

FILE: `src/main/java/com/nutrition/dss/service/UserService.java`

LOCATION: `saveHealthProfile` method

**BEFORE:**
```java
    public HealthProfile saveHealthProfile(User user, int age, String gender,
                                           double heightCm, double weightKg,
                                           String activityLevel, String healthCondition) {
        // ...
        profile.setHealthCondition(healthCondition);
```

**AFTER:**
```java
    public HealthProfile saveHealthProfile(User user, int age, String gender,
                                           double heightCm, double weightKg,
                                           String activityLevel, String healthCondition,
                                           String dietaryPreference) {
        // ...
        profile.setHealthCondition(healthCondition);
        profile.setDietaryPreference(dietaryPreference);
```

**WHAT CHANGED:**
Updated the service logic to save the new dietary preference coming from the frontend form.

---

FILE: `src/main/resources/templates/dashboard.html`

LOCATION: Form fields

**BEFORE:**
```html
<option value="OBESITY" th:selected="...">Obesity</option>
```

**AFTER:**
```html
<div class="form-group">
    <label for="dietaryPreference">Dietary Preference</label>
    <select id="dietaryPreference" name="dietaryPreference" required>
        <option value="VEG" th:selected="${profile == null || profile.dietaryPreference == 'VEG'}">Vegetarian</option>
        <option value="NON_VEG" th:selected="${profile != null && profile.dietaryPreference == 'NON_VEG'}">Non-Vegetarian</option>
    </select>
</div>
```

**WHAT CHANGED:**
Removed the obsolete Obesity option and explicitly added a dropdown allowing the user to select Vegetarian or Non-Vegetarian.

---

## 3. Calorie Info & Labels

FILE: `src/main/java/com/nutrition/dss/model/FoodItem.java`

LOCATION: Class properties & methods

**BEFORE:**
```java
    private int caloriesPer100g;
```

**AFTER:**
```java
    private int caloriesPer100g;

    @Column(nullable = false)
    private String type; 

    @Transient
    private String justification;

    public String getCalorieLabel() {
        if (caloriesPer100g == 0) return "Unknown";
        if (caloriesPer100g < 100) return "Low";
        if (caloriesPer100g <= 250) return "Moderate";
        return "High";
    }
```

**WHAT CHANGED:**
Added the `type` column to distinguish veg/non-veg, a `@Transient` string to hold rule justifications, and the `getCalorieLabel` method to categorize foods by calorie density.

---

FILE: `src/main/java/com/nutrition/dss/DataSeeder.java`

LOCATION: Food seeding block

**BEFORE:**
```java
foodItemRepository.save(new FoodItem("Chicken Breast", "PROTEINS", "Lean protein source"));
// ...
// --- OBESITY RULES ---
dietaryRuleRepository.save(new DietaryRule("OBESITY", "SWEETS", "AVOID", 10));
```

**AFTER:**
```java
foodItemRepository.save(new FoodItem("Chicken Breast", "PROTEINS", "Lean protein source", 165, "NON_VEG"));
// ...
// OBESITY RULES completely removed
```

**WHAT CHANGED:**
Updated every single `FoodItem` creation to use the 5-argument constructor, ensuring every item has realistic `caloriesPer100g` and the correct `type` ("VEG" or "NON_VEG"). Removed all OBESITY rules.

---

## 4. Frontend Rendering

FILE: `src/main/resources/templates/results.html`

LOCATION: Food display loop

**BEFORE:**
```html
<span class="food-desc" th:if="${food.caloriesPer100g > 0}" th:text="${food.caloriesPer100g} + ' kcal/100g'">Calories</span>
```

**AFTER:**
```html
<span class="food-desc" th:if="${food.caloriesPer100g > 0}" th:text="${food.caloriesPer100g} + ' kcal/100g (' + ${food.calorieLabel} + ')'">Calories</span>
<span class="food-desc justification-text" th:text="${food.justification}" style="font-style: italic; color: #666; font-size: 0.85em; margin-top: 4px; display: block;">Justification</span>
```

**WHAT CHANGED:**
Appended the dynamically generated calorie label (e.g. "(Low)") to the calorie text and injected a new span block underneath each item to visually display the rule justification.
