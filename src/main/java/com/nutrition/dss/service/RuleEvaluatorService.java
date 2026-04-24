package com.nutrition.dss.service;

import com.nutrition.dss.model.DietaryRule;
import com.nutrition.dss.model.HealthProfile;
import com.nutrition.dss.repository.DietaryRuleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic rule evaluator using strategy pattern.
 * Evaluates conditionExpression strings against a user's health profile.
 * Supports expressions like: "BMI > 25", "age > 60", "diabetes = true"
 */
@Service
public class RuleEvaluatorService {

    private final DietaryRuleRepository ruleRepository;

    public RuleEvaluatorService(DietaryRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    /**
     * Find all rules whose conditionExpression matches the given profile.
     * Returns dynamic rules that should be applied on top of condition-based rules.
     */
    public List<DietaryRule> evaluateDynamicRules(HealthProfile profile) {
        List<DietaryRule> allRules = ruleRepository.findAll();
        List<DietaryRule> matched = new ArrayList<>();

        for (DietaryRule rule : allRules) {
            if (rule.getConditionExpression() != null && !rule.getConditionExpression().isBlank()) {
                if (evaluateExpression(rule.getConditionExpression(), profile)) {
                    matched.add(rule);
                }
            }
        }
        return matched;
    }

    /**
     * Evaluate a simple condition expression against a health profile.
     * Supported formats:
     *   "BMI > 25"
     *   "BMI >= 30"
     *   "age > 60"
     *   "age < 18"
     *   "diabetes = true"
     *   "hypertension = true"
     */
    boolean evaluateExpression(String expression, HealthProfile profile) {
        if (expression == null || expression.isBlank()) return false;

        String expr = expression.trim().toLowerCase();

        try {
            // Handle boolean condition checks: "diabetes = true"
            if (expr.contains("= true")) {
                String conditionName = expr.replace("= true", "").trim();
                String healthCondition = profile.getHealthCondition();
                if (healthCondition == null) return false;
                return healthCondition.toLowerCase().contains(conditionName);
            }

            // Handle comparison operators
            if (expr.startsWith("bmi")) {
                return evaluateComparison(expr.substring(3).trim(), profile.getBmi());
            }
            if (expr.startsWith("age")) {
                return evaluateComparison(expr.substring(3).trim(), profile.getAge());
            }
            if (expr.startsWith("weight")) {
                return evaluateComparison(expr.substring(6).trim(), profile.getWeightKg());
            }
            if (expr.startsWith("height")) {
                return evaluateComparison(expr.substring(6).trim(), profile.getHeightCm());
            }

        } catch (Exception e) {
            // Log but don't fail — invalid expressions are skipped
            System.err.println("Failed to evaluate rule expression: " + expression + " — " + e.getMessage());
        }

        return false;
    }

    private boolean evaluateComparison(String operatorAndValue, double actualValue) {
        String trimmed = operatorAndValue.trim();

        if (trimmed.startsWith(">=")) {
            return actualValue >= Double.parseDouble(trimmed.substring(2).trim());
        } else if (trimmed.startsWith("<=")) {
            return actualValue <= Double.parseDouble(trimmed.substring(2).trim());
        } else if (trimmed.startsWith(">")) {
            return actualValue > Double.parseDouble(trimmed.substring(1).trim());
        } else if (trimmed.startsWith("<")) {
            return actualValue < Double.parseDouble(trimmed.substring(1).trim());
        } else if (trimmed.startsWith("=")) {
            return actualValue == Double.parseDouble(trimmed.substring(1).trim());
        }

        return false;
    }
}
