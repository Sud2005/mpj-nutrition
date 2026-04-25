# Nutrition Decision Support System (DSS)

An intelligent, rule-based, and LLM-enhanced clinical nutrition system that generates personalized 7-day meal plans based on patient health profiles, BMI, specific medical conditions, and allergies. 

## Features

- **Personalized Health Profiles**: Track Age, Gender, Height, Weight, Activity Level, Dietary Preference, and Allergies.
- **Rule-Based Engine**: A robust decision support system that filters out foods based on predefined rules (e.g., Hypertension -> Limit sodium, Obesity -> Avoid high-calorie items).
- **LLM-Enhanced Generation**: Integrates with Groq API (LLaMA 3.1) to intelligently synthesize the rule-based suggestions into a complete, structured 7-day meal plan (Breakfast, Lunch, Dinner).
- **Allergy Aware**: Strictly removes any allergens from the meal plans.
- **Nutritionist Review Workflow**: 
  - Plans are initially marked as `PENDING`.
  - Nutritionists (Admin users) can review the raw generated plans in a dedicated dashboard and manually `Approve` or `Reject` them.
- **Graceful Degradation**: If the LLM API is unavailable, the system safely falls back to a generalized, rule-based dummy weekly plan.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: H2 (In-memory database for rapid prototyping)
- **Frontend**: Thymeleaf, HTML5, Vanilla CSS
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **AI Integration**: Groq REST API (LLaMA 3.1)

## Getting Started

### Prerequisites
- JDK 17+
- Maven
- A Groq API Key (Optional, for LLM 7-day generation)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Sud2005/mpj-nutrition.git
   cd mpj-nutrition
   ```

2. Configure Environment Variables (Optional for LLM features):
   Create a `.env` file in the root directory (this file is ignored by Git):
   ```env
   GROQ_API_KEY=your_api_key_here
   GROQ_ENABLED=true
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   Open your browser and navigate to `http://localhost:8080`

### Demo Accounts
Upon startup, the database is seeded with two default accounts:
- **Admin (Nutritionist):** `admin@dss.com` | Password: `admin`
- **User:** `test@user.com` | Password: `password`

## Project Structure

- `src/main/java/.../controller`: Web and API endpoints.
- `src/main/java/.../model`: Database entities (`User`, `FoodItem`, `DietaryRule`, `DietPlan`, `HealthProfile`).
- `src/main/java/.../dto`: Data Transfer Objects (`WeeklyPlanDTO`, etc.) mapping JSON between the LLM and the frontend.
- `src/main/java/.../service`: Core business logic (`RuleEngineService`, `GroqService`).
- `src/main/resources/templates`: Thymeleaf HTML views.

## Architecture & Workflow

1. **User Input:** User saves their health metrics, dietary preferences, and allergies.
2. **Rule Evaluation (`RuleEngineService`):** The system queries the `DietaryRule` database, calculating dynamic rules (like BMI) to categorize foods into `RECOMMENDED`, `LIMITED`, and `AVOID`.
3. **LLM Synthesis (`GroqService`):** The categorized lists are sent to Groq. The model is prompted to synthesize a strict JSON 7-day schedule, safely filtering out known allergens and rearranging meals based on the clinical constraints.
4. **Nutritionist Review (`AdminController`):** The plan is saved as `PENDING`. An admin reviews the JSON output and updates the status.
5. **Final Output:** The user views the visually pleasing 7-day schedule on their dashboard, badged as "Suggested by a Nutritionist" and labeled with its approval status.

##Contributing 

Feel free to open issues or submit pull requests for any improvements!

## License
MIT License
