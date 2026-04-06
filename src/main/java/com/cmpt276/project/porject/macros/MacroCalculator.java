package com.cmpt276.project.porject.macros;

import com.cmpt276.project.porject.meals.FoodApiService;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.cmpt276.project.porject.auth.User;

/**
 * Service to dynamically set nutrient goals based on Millfen-St Jeor Equation
 */
@Service
public class MacroCalculator {
    private final FoodApiService foodApiService;

    MacroCalculator(FoodApiService foodApiService) {
        this.foodApiService = foodApiService;
    }

    public enum Goal {
        INTENSIVE_WEIGHT_LOSS("Intensive Weight Loss", -0.30, 2.4, 0.20),
        MODERATE_WEIGHT_LOSS("Moderate Weight Loss", -0.20, 2.0, 0.25),
        MILD_WEIGHT_LOSS("Mild Weight Loss", -0.10, 1.8, 0.25),
        MAINTAIN_WEIGHT("Maintain Weight", 0.00, 1.8, 0.30),
        MILD_WEIGHT_GAIN("Mild Weight Gain", 0.10, 1.8, 0.25),
        MODERATE_WEIGHT_GAIN("Moderate Weight Gain", 0.20, 2.0, 0.25),
        INTENSIVE_WEIGHT_GAIN("Intensive Weight Gain", 0.30, 2.2, 0.20);

        private final String goalName;
        private final double calorieAdjustment;
        private final double proteinPerKg;
        private final double fatPercentage;

        Goal(String displayName, double calorieAdjustment, double proteinPerKg, double fatPercentage) {
            this.goalName = displayName;
            this.calorieAdjustment = calorieAdjustment;
            this.proteinPerKg = proteinPerKg;
            this.fatPercentage = fatPercentage;
        }
    }

    public enum ActivityLevel {
        NOT_ACTIVE("Little or no excersise", 1.2),
        LIGHTLY_ACTIVE("Lightly active (excersise 1-3 days/week)", 1.375),
        MODERATELY_ACTIVE("Moderately active (excersise 3-5 days/week)", 1.55),
        VERY_ACTIVE("Very active (excersise 6-7 days/week)", 1.725),
        EXTRA_ACTIVE("Extra active (an athlete/physical job)", 1.9);

        private final String displayName;
        private final double multiplier;

        ActivityLevel(String displayName, double multiplier) {
            this.displayName = displayName;
            this.multiplier = multiplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    //helper to conv dob to age
    private int calcAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 30;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    //Calc base metabolic rate
    public double calcBMR(User user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = calcAge(user.getDateOfBirth());

        if("Male".equalsIgnoreCase(user.getSex())) {
            return (10 * weight) + (6.25 * height) + (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25 * height) + (5 * age) - 161;
        }
    }

    //calc total daily energy expenditure TDEE
    public double calcTDEE(User user, ActivityLevel act) {
        return calcBMR(user) * act.getMultiplier();
    }

    public CalculatedTargets calcNutrients(User user, ActivityLevel act, Goal goal) {
        double tdee = calcTDEE(user, act);
        double targetCals = tdee * (1 + goal.calorieAdjustment);

        //protien
        double protienGrams = user.getWeight() * goal.proteinPerKg;
        double protienCals = protienGrams * 4;

        //fat
        double fatCals = targetCals * goal.fatPercentage;
        double fatGrams = fatCals / 9;
        
        //carbs
        double carbCals = targetCals - protienCals - fatCals;
        double carbGrams = carbCals / 4;

        return new CalculatedTargets(targetCals, protienGrams, fatGrams, carbGrams, targetCals * 7);
    }

    public static class CalculatedTargets {
        public final double dailyCalories;
        public final double dailyProtein;
        public final double dailyFats;
        public final double dailyCarbs;
        public final double weeklyCalories;

        public CalculatedTargets(double dailyCalories, double dailyProtein, double dailyFats, double dailyCarbs, double weeklyCalories) {
            this.dailyCalories = dailyCalories;
            this.dailyProtein = dailyProtein;
            this.dailyCarbs = dailyCarbs;
            this.dailyFats = dailyFats;
            this.weeklyCalories = weeklyCalories;
        }
    }
}
