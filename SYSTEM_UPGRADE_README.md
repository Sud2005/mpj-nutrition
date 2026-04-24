# Nutrition DSS v2.0 — System Upgrade Documentation

## Overview

The Nutrition DSS has been upgraded from a beginner-level Spring Boot demo into a production-ready Decision Support System for Personalized Nutrition Planning. This document covers all new features, architecture, setup, and API reference.

---

## New Features

| # | Feature | Description |
|---|---------|-------------|
| 1 | **Database (H2)** | JPA entities with proper relationships, repositories, DTOs |
| 2 | **Diet Plan History** | Every plan stored in DB with JSON + summary format |
| 3 | **Admin Module** | Dashboard stats, food/rule CRUD with macronutrients |
| 4 | **RBAC** | JWT authentication for APIs, session auth for web |
| 5 | **Dynamic Rule Engine** | Expression-based rules (BMI > 25, age > 60) |
| 6 | **Structured Diet Output** | `{ recommended: [], limited: [], avoid: [] }` JSON |
| 7 | **Input Validation** | Jakarta Validation on DTOs with proper error responses |
| 8 | **Admin Dashboard** | Total users, plans, BMI distribution statistics |
| 9 | **Groq LLM Integration** | Optional AI diet refinement via LLaMA 3.1 |

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│  ┌──────────────┐                    ┌──────────────────────┐   │
│  │  Thymeleaf   │   (Browser)        │  REST API Client     │   │
│  │  Templates   │                    │  (Postman/Frontend)  │   │
│  └──────┬───────┘                    └──────────┬───────────┘   │
└─────────┼───────────────────────────────────────┼───────────────┘
          │ Session Auth                          │ JWT Auth
┌─────────┼───────────────────────────────────────┼───────────────┐
│         ▼               SECURITY LAYER          ▼               │
│  ┌──────────────┐                    ┌──────────────────────┐   │
│  │ SecurityConfig│──────────────────▶│ JwtAuthFilter        │   │
│  │ (permitAll)  │                    │ (Bearer token)       │   │
│  └──────────────┘                    └──────────────────────┘   │
│         │                                       │               │
│  ┌──────▼───────┐                    ┌──────────▼───────────┐   │
│  │ BCrypt       │                    │ JwtUtil              │   │
│  │ PasswordEnc  │                    │ (generate/validate)  │   │
│  └──────────────┘                    └──────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │                                       │
┌─────────┼───────────────────────────────────────┼───────────────┐
│         ▼           CONTROLLER LAYER            ▼               │
│  ┌──────────────┐                    ┌──────────────────────┐   │
│  │ AppController │                   │ api/AuthApiController │   │
│  │ AdminCtrl     │                   │ api/UserApiController │   │
│  │ (Thymeleaf)   │                   │ api/DietApiController │   │
│  └──────┬───────┘                    │ api/AdminApiController│   │
│         │                            └──────────┬───────────┘   │
└─────────┼───────────────────────────────────────┼───────────────┘
          │                                       │
┌─────────┼───────────────────────────────────────┼───────────────┐
│         ▼            SERVICE LAYER              ▼               │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  UserService          │  RuleEngineService             │     │
│  │  DashboardService     │  RuleEvaluatorService          │     │
│  │  GroqService (LLM)    │                                │     │
│  └───────────────────────┴────────────────────────────────┘     │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────┐
│                            ▼                                    │
│                    REPOSITORY LAYER                             │
│  ┌─────────────┐ ┌──────────────┐ ┌───────────────────────┐    │
│  │UserRepository│ │FoodItemRepo  │ │DietPlanRepository     │    │
│  │HealthProfile│ │DietaryRuleR  │ │                       │    │
│  └──────┬──────┘ └──────┬───────┘ └───────────┬───────────┘    │
└─────────┼───────────────┼─────────────────────┼────────────────┘
          │               │                     │
┌─────────┼───────────────┼─────────────────────┼────────────────┐
│         ▼               ▼                     ▼                │
│                    H2 DATABASE                                 │
│         ┌────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐   │
│         │ users  │ │food_items│ │diet_plans│ │dietary_rules│   │
│         └────────┘ └──────────┘ └──────────┘ └────────────┘   │
│         ┌────────────────┐                                     │
│         │health_profiles │                                     │
│         └────────────────┘                                     │
└────────────────────────────────────────────────────────────────┘
```

---

## Setup Instructions

### Prerequisites
- Java 17+
- IntelliJ IDEA (or any IDE with Maven support)

### Running the Application

```bash
# From IntelliJ: Right-click DssApplication.java → Run
# Or from terminal with Maven:
mvn spring-boot:run
```

### Default Credentials
| Role  | Email            | Password |
|-------|------------------|----------|
| Admin | admin@dss.com    | admin123 |
| User  | demo@dss.com     | demo123  |

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:nutritiondb`
- Username: `sa`, Password: (empty)

### Groq LLM Setup (Optional)
1. Get API key from [groq.com](https://groq.com)
2. Set environment variables:
   ```
   GROQ_API_KEY=your_key_here
   GROQ_ENABLED=true
   ```
3. Or edit `application.properties` directly
4. The app works without Groq — it auto-approves plans if LLM is unavailable

---

## Database Schema

### users
| Column     | Type         | Notes            |
|------------|--------------|------------------|
| id         | BIGINT (PK)  | Auto-generated   |
| full_name  | VARCHAR      | NOT NULL         |
| email      | VARCHAR      | UNIQUE, NOT NULL |
| password   | VARCHAR      | BCrypt hashed    |
| role       | VARCHAR      | USER or ADMIN    |
| created_at | TIMESTAMP    | Auto-set         |

### health_profiles
| Column             | Type         | Notes                |
|--------------------|--------------|----------------------|
| id                 | BIGINT (PK)  | Auto-generated       |
| user_id            | BIGINT (FK)  | References users     |
| age                | INT          |                      |
| gender             | VARCHAR      | MALE / FEMALE        |
| height_cm          | DOUBLE       |                      |
| weight_kg          | DOUBLE       |                      |
| bmi                | DOUBLE       | Calculated           |
| activity_level     | VARCHAR      | SEDENTARY/MODERATE/ACTIVE |
| health_condition   | VARCHAR      | Comma-separated      |
| dietary_preference | VARCHAR      | VEG / NON_VEG        |

### food_items
| Column          | Type         | Notes                |
|-----------------|--------------|----------------------|
| id              | BIGINT (PK)  | Auto-generated       |
| name            | VARCHAR      | NOT NULL             |
| category        | VARCHAR      | Food category        |
| description     | VARCHAR      |                      |
| calories_per100g| INT          | Per 100g serving     |
| protein         | DOUBLE       | Grams per 100g       |
| carbs           | DOUBLE       | Grams per 100g       |
| fats            | DOUBLE       | Grams per 100g       |
| type            | VARCHAR      | VEG / NON_VEG        |

### dietary_rules
| Column               | Type         | Notes                          |
|----------------------|--------------|--------------------------------|
| id                   | BIGINT (PK)  | Auto-generated                 |
| condition            | VARCHAR      | NONE/DIABETES/HYPERTENSION     |
| food_category        | VARCHAR      | Target food category           |
| recommendation       | VARCHAR      | RECOMMENDED/LIMITED/AVOID      |
| priority             | INT          | Higher wins in conflicts       |
| condition_expression | VARCHAR      | Dynamic: "BMI > 25"            |
| action_type          | VARCHAR      | Description of action          |

### diet_plans
| Column                  | Type         | Notes                       |
|-------------------------|--------------|------------------------------|
| id                      | BIGINT (PK)  | Auto-generated              |
| user_id                 | BIGINT (FK)  | References users            |
| generated_at            | TIMESTAMP    | Auto-set                    |
| plan_summary            | VARCHAR(2000)| Pipe-delimited summary      |
| plan_json               | VARCHAR(5000)| Structured JSON output      |
| bmi_at_generation       | DOUBLE       | BMI snapshot                |
| condition_at_generation | VARCHAR      | Condition snapshot          |
| approval_status         | VARCHAR      | PENDING/APPROVED/REJECTED   |

---

## API Reference

### Authentication

#### POST /api/auth/register
```json
// Request
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "secure123"
}
// Response: 200
{
  "id": 3,
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "createdAt": "2024-01-15T10:30:00"
}
```

#### POST /api/auth/login
```json
// Request
{
  "email": "demo@dss.com",
  "password": "demo123"
}
// Response: 200
{
  "token": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "user": {
    "id": 2,
    "fullName": "Demo User",
    "email": "demo@dss.com",
    "role": "USER"
  }
}
```

### User Endpoints (JWT Required)

#### GET /api/users/{id}
Returns user profile (no password).

#### GET /api/users/{id}/diet-history
Returns all diet plans for a user, newest first.

#### GET /api/users/{id}/latest-plan
Returns the most recent diet plan.

### Diet Generation (JWT Required)

#### POST /api/diet/generate
```json
// Optional Request Body (profile override)
{
  "age": 30,
  "gender": "MALE",
  "heightCm": 175,
  "weightKg": 80,
  "activityLevel": "MODERATE",
  "healthCondition": "DIABETES",
  "dietaryPreference": "VEG"
}
// Response: 200
{
  "recommended": [
    {
      "id": 1,
      "name": "Brown Rice",
      "category": "GRAINS",
      "caloriesPer100g": 111,
      "protein": 2.6,
      "carbs": 23.0,
      "fats": 0.9
    }
  ],
  "limited": [...],
  "avoid": [...],
  "llmRefinement": "Consider adding more leafy greens..."
}
```

### Admin Endpoints (ADMIN JWT Required)

#### GET /api/admin/dashboard
```json
{
  "totalUsers": 5,
  "totalDietPlans": 12,
  "bmiDistribution": {
    "Underweight": 1,
    "Normal": 3,
    "Overweight": 1,
    "Obese": 0
  }
}
```

#### CRUD Operations
| Method | Endpoint              | Description         |
|--------|-----------------------|---------------------|
| GET    | /api/admin/foods      | List all foods      |
| POST   | /api/admin/foods      | Add food            |
| PUT    | /api/admin/foods/{id} | Update food         |
| DELETE | /api/admin/foods/{id} | Delete food         |
| GET    | /api/admin/rules      | List all rules      |
| POST   | /api/admin/rules      | Add rule            |
| PUT    | /api/admin/rules/{id} | Update rule         |
| DELETE | /api/admin/rules/{id} | Delete rule         |

---

## Groq LLM Integration

### How It Works
1. **Rule Engine** generates the initial diet plan using database rules
2. **GroqService** (optional) sends the plan + patient profile to LLaMA 3.1
3. LLM provides **refinement suggestions** and **approval simulation**
4. Results are stored in the `diet_plans` table with `approval_status`

### Prompt Templates
- **Refinement**: "You are a professional nutritionist. Review this diet plan..."
- **Approval**: "Does this plan seem safe and appropriate? Reply APPROVED or REJECTED..."

### Graceful Degradation
If the Groq API key is not configured or the API call fails:
- Plans are auto-approved
- No LLM refinement note is shown
- All other functionality works normally

---

## Dynamic Rule Engine

### Expression Syntax
Rules can now include `conditionExpression` strings:

| Expression        | Meaning                              |
|-------------------|--------------------------------------|
| `BMI > 25`        | Triggers for overweight users        |
| `BMI >= 30`       | Triggers for obese users             |
| `age > 60`        | Triggers for senior users            |
| `age < 18`        | Triggers for young users             |
| `diabetes = true` | Triggers if condition contains DIABETES |

### Conflict Resolution
When multiple rules target the same food category, the **highest priority wins**.

---

## Project Structure

```
src/main/java/com/nutrition/dss/
├── DssApplication.java          # Main entry point
├── DataSeeder.java              # Seeds demo data (BCrypt passwords)
├── config/
│   ├── SecurityConfig.java      # Dual auth: session + JWT
│   └── GroqConfig.java          # LLM API configuration
├── security/
│   ├── JwtUtil.java             # JWT token operations
│   ├── JwtAuthenticationFilter.java  # API auth filter
│   └── CustomUserDetailsService.java # Spring Security UserDetails
├── model/
│   ├── User.java                # User entity (createdAt, BCrypt)
│   ├── HealthProfile.java       # BMI, conditions, preferences
│   ├── FoodItem.java            # Macronutrients (P/C/F)
│   ├── DietPlan.java            # JSON output, approval status
│   └── DietaryRule.java         # Expression-based rules
├── dto/                         # 11 DTO classes
├── repository/                  # 5 JPA repositories
├── service/
│   ├── UserService.java         # BCrypt auth
│   ├── RuleEngineService.java   # Core diet engine
│   ├── RuleEvaluatorService.java# Dynamic expression evaluator
│   ├── DashboardService.java    # Admin statistics
│   └── GroqService.java         # LLM integration
├── controller/
│   ├── AppController.java       # Thymeleaf pages
│   ├── AdminController.java     # Admin Thymeleaf pages
│   └── api/                     # 4 REST API controllers
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

---

## Technologies Used

| Technology       | Purpose                    |
|------------------|----------------------------|
| Spring Boot 3.2  | Application framework      |
| Spring Security  | RBAC, JWT, BCrypt          |
| Spring Data JPA  | ORM / database access      |
| H2 Database      | In-memory persistence      |
| Thymeleaf        | Server-side HTML rendering |
| JJWT 0.12        | JWT token library          |
| Jakarta Validation| Input validation          |
| Groq API         | LLM integration (optional) |
