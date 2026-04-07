package com.cmpt276.project.porject.macros;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.cmpt276.project.porject.auth.User;

@Service
public class MacroCalcService {

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

        public String getGoalName() { return goalName; }
        public double getCalorieAdjustment() { return calorieAdjustment; }
        public double getProteinPerKg() { return proteinPerKg; }
        public double getFatPercentage() { return fatPercentage; }
    }

    // helper to conv dob to age
    private int calcAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 30;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // Calc base metabolic rate
    public double calcBMR(User user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = calcAge(user.getDateOfBirth());

        if ("Male".equalsIgnoreCase(user.getSex())) {
            return (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
    }

    // Calc activity multiplier based on weekly workout goal
    public double calcActivityMult(int workoutGoal) {
        if (workoutGoal <= 1) {
            return 1.2;
        } else if (workoutGoal <= 3) {
            return 1.375;
        } else if (workoutGoal <= 5) {
            return 1.55;
        } else if (workoutGoal <= 6) {
            return 1.725;
        } else {
            return 1.9;
        }
    }

    // calc total daily energy expenditure TDEE
    public double calcTDEE(User user, int weeklyWorkoutGoal) {
        double bmr = calcBMR(user);
        double multiplier = calcActivityMult(weeklyWorkoutGoal);
        return bmr * multiplier;
    }

    public CalculatedTargets calcTargets(User user, Goal goal, int weeklyWorkoutGoal) {
        double tdee = calcTDEE(user, weeklyWorkoutGoal);
        double targetCalories = tdee * (1 + goal.getCalorieAdjustment());

        double proteinGrams = user.getWeight() * goal.getProteinPerKg();
        double proteinCalories = proteinGrams * 4;

        double fatCalories = targetCalories * goal.getFatPercentage();
        double fatGrams = fatCalories / 9;

        double carbCalories = targetCalories - proteinCalories - fatCalories;
        double carbGrams = carbCalories / 4;

        double fibreGrams = (targetCalories / 1000) * 14;

        //Clamp fibre to healthy amount range
        if (fibreGrams < 20) fibreGrams = 20;
        if (fibreGrams > 50) fibreGrams = 50;

        return new CalculatedTargets(
                Math.round(targetCalories),
                Math.round(proteinGrams),
                Math.round(fatGrams),
                Math.round(carbGrams),
                Math.round(fibreGrams),
                Math.round(targetCalories * 7));
    }

    //Calc weekly burn target based on goal
    public double calcBurnTarget(User user, Goal goal) {
        double weightKg = user.getWeight();
        int weeklyWorkouts = user.getWeeklyWorkoutGoalCount();

        double caloriesPerKgPerWorkout;

        switch (goal) {
            case INTENSIVE_WEIGHT_LOSS:
                caloriesPerKgPerWorkout = 12;
                break;
            case MODERATE_WEIGHT_LOSS:
                caloriesPerKgPerWorkout = 10;
                break;
            case MILD_WEIGHT_LOSS:
                caloriesPerKgPerWorkout = 8;
                break;
            case MAINTAIN_WEIGHT:
                caloriesPerKgPerWorkout = 7;
                break;
            case MILD_WEIGHT_GAIN:
                caloriesPerKgPerWorkout = 6;
                break;
            case MODERATE_WEIGHT_GAIN:
                caloriesPerKgPerWorkout = 5;
                break;
            case INTENSIVE_WEIGHT_GAIN:
                caloriesPerKgPerWorkout = 4;
                break;
            default:
                caloriesPerKgPerWorkout = 7;
        }

        double caloriesPerWorkout = weightKg * caloriesPerKgPerWorkout;
        double weeklyTarget = caloriesPerWorkout * weeklyWorkouts;

        return Math.round(weeklyTarget);
    }

    public static class CalculatedTargets {
        public final double dailyCalories;
        public final double dailyProtein;
        public final double dailyFats;
        public final double dailyCarbs;
        public final double dailyFibre;
        public final double weeklyCalories;

        public CalculatedTargets(double dailyCalories, double dailyProtein, double dailyFats, double dailyCarbs, double dailyFibre,
                double weeklyCalories) {
            this.dailyCalories = dailyCalories;
            this.dailyProtein = dailyProtein;
            this.dailyCarbs = dailyCarbs;
            this.dailyFats = dailyFats;
            this.dailyFibre = dailyFibre;
            this.weeklyCalories = weeklyCalories;
        }
    }
}