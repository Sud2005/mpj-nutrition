# Nutrition Decision Support System (DSS) - Technical Documentation

This document provides a comprehensive theoretical overview of the Nutrition DSS application. It explains the software architecture, the role of each component, and how the different parts of the Java codebase interact to deliver the system's functionality.

## 1. Architectural Overview

The application is built using **Java** and the **Spring Boot** framework. It follows the classic **Model-View-Controller (MVC)** architectural pattern combined with a **Service Layer** for business logic and a **Repository Layer** for database access.

### The Layers:
*   **Model Layer (`com.nutrition.dss.model`)**: Represents the data structures (Entities) and maps directly to the underlying database tables (using JPA/Hibernate).
*   **Repository Layer (`com.nutrition.dss.repository`)**: Interfaces that handle data access operations (CRUD: Create, Read, Update, Delete) without needing to write SQL queries manually.
*   **Service Layer (`com.nutrition.dss.service`)**: The "brain" of the application. It contains all the business logic, calculations, and rules. It acts as a bridge between the Controllers and the Repositories.
*   **Controller Layer (`com.nutrition.dss.controller`)**: The "traffic directors". They receive HTTP requests from the user's browser, call the appropriate Services to process the request, and return the correct View (HTML page) along with the necessary data.

---

## 2. Detailed Code Breakdown

Below is a theoretical explanation of what each specific part of the code does.

### A. The Core Logic: The Service Layer

The most critical part of this application is how it makes decisions. This is handled by the services.

#### `RuleEngineService.java`
This is the absolute core of the Decision Support System. It acts as an expert system that determines which foods a user should eat based on their health profile.

**How it works (The Theory):**
1.  **Data Gathering**: It fetches all available `FoodItem`s from the database.
2.  **General Rules Application**: It fetches rules assigned to the "NONE" condition. These are baseline dietary rules that apply to everyone (e.g., "Vegetables are RECOMMENDED").
3.  **Condition-Specific Rules Application**: It checks the user's `HealthProfile` for specific conditions (like "DIABETES" or "HYPERTENSION"). It fetches rules tied specifically to that condition.
4.  **Conflict Resolution**: If a general rule says "Sweets are LIMITED" but a Diabetes rule says "Sweets are AVOID", the engine must resolve this conflict. It does so using a **Priority System**. The rule with the highest numerical priority value wins and overwrites the lower-priority rule.
5.  **Categorization**: It iterates through every food item, checks its category against the resolved rules, and places the food into one of three buckets: `RECOMMENDED`, `LIMITED`, or `AVOID`. If no rule exists for a category, it defaults to `RECOMMENDED`.
6.  **Extensibility (Tinker Zones)**: The theory behind this file allows for easy expansion. For example, logic can be added to factor in BMI (e.g., restricting fats if BMI > 30) or Age (e.g., recommending dairy for seniors).

#### `UserService.java`
Handles the business logic regarding user accounts.
*   **Authentication**: Validates passwords during login.
*   **Registration**: Ensures emails are unique and safely saves new users.
*   **Profile Management**: Links a `HealthProfile` to a specific `User`.

---

### B. The Traffic Directors: The Controller Layer

The controllers map URLs (like `/login` or `/generate`) to specific Java methods.

#### `AppController.java`
Manages the user-facing web application.
*   **Routing**: Directs users to `index`, `register`, `login`, and `dashboard` pages.
*   **Session Management**: Keeps track of who is logged in using HTTP Sessions (`HttpSession`). If a user tries to visit `/dashboard` without being logged in, the controller intercepts this and redirects them to the login page.
*   **Action Handling**: When a user submits their health profile form, the `saveProfile` method intercepts the POST request, extracts the data (age, weight, condition), and passes it to the `UserService` to be saved.
*   **Triggering the Engine**: When a user goes to `/generate`, the controller fetches their profile, hands it to the `RuleEngineService`, receives the resulting diet plan, and passes that data to the `results` HTML view to be displayed.

#### `AdminController.java`
Manages the administrative side of the application.
*   **Security Check**: Every method first verifies if the logged-in user has the `ADMIN` role.
*   **Data Management**: Provides endpoints (URLs) to add or delete `FoodItem`s and `DietaryRule`s, allowing administrators to update the expert system's knowledge base without changing code.

---

### C. Data Representation: The Model Layer

These Java classes define the shape of the data flowing through the system.

*   **`User.java`**: Stores credentials (email, password, role) and links to the user's generated plans and health profile.
*   **`HealthProfile.java`**: Stores biometric data (age, height, weight, BMI) and the specific health condition (e.g., DIABETES). The BMI is theoretically calculated automatically based on height and weight.
*   **`FoodItem.java`**: Represents a specific food (e.g., "Brown Rice") and categorizes it (e.g., "GRAINS"). The DSS engine uses the category, not the specific name, to apply rules.
*   **`DietaryRule.java`**: The "Knowledge Base" of the expert system. It dictates that for a specific `condition` (e.g., "OBESITY"), a certain `foodCategory` (e.g., "FATS") has a specific `recommendation` (e.g., "AVOID") with a certain `priority`.
*   **`DietPlan.java`**: An archival model. Once the `RuleEngineService` generates a plan, it is serialized into a string and saved here so the user can view their history.

---

### D. Data Initialization: `DataSeeder.java`

Since a Decision Support System is useless without data and rules, `DataSeeder.java` acts as the initial knowledge injection point.
*   It implements `CommandLineRunner`, meaning Spring Boot executes it the moment the application starts.
*   It checks if the database is empty. If it is, it populates it with default users (admin and demo), a comprehensive list of `FoodItem`s mapped to categories, and the initial set of `DietaryRule`s for conditions like Diabetes, Hypertension, and Obesity.

---

## 3. Functional Workflow: Generating a Diet Plan

To understand how the theory comes together, here is the step-by-step flow when a user requests a diet plan:

1.  **User Action**: The user clicks "Generate Plan" on the dashboard. The browser sends a `GET` request to the `/generate` URL.
2.  **Controller Interception**: `AppController.generatePlan()` receives the request. It checks the session to identify the user.
3.  **Data Retrieval**: The controller asks `UserService` for the user's `HealthProfile`.
4.  **Engine Activation**: The controller passes the `HealthProfile` to `RuleEngineService.generateDietPlan()`.
5.  **Decision Making**:
    *   The engine reads all foods and rules from the Repositories.
    *   It applies the priority-based conflict resolution algorithm based on the user's condition.
    *   It sorts every food into lists (Recommended, Limited, Avoid).
6.  **Archiving**: The generated plan is passed to `RuleEngineService.savePlan()` to be saved in the database history.
7.  **Display**: The controller takes the resulting lists, attaches them to a `Model` object, and tells Spring Boot to render the `results.html` view. The user sees their personalized plan on screen.
