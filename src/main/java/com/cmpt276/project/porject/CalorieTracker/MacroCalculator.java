package com.cmpt276.project.porject.CalorieTracker;

import org.springframework.stereotype.Component;

/**
 * Computes macro targets based on a user's daily calorie goal.
 * 
 * Formulas:
 * - Protein: 30% of calories (4 kcal/g)
 * - Carbs: 40% of calories (4 kcal/g)
 * - Fats: 30% of calories (9 kcal/g)
 * - Fiber: 14g per 1000 calories
 */
@Component
public class MacroCalculator {
    // Ratio Constants
    private static final double PROTEIN_RATIO = 0.30;
    private static final double CARBS_RATIO = 0.40;
    private static final double FATS_RATIO = 0.30;

    // Density Constants
    private static final double KCAL_PER_GRAM_PROTEIN = 4.0;
    private static final double KCAL_PER_GRAM_CARBS = 4.0;
    private static final double KCAL_PER_GRAM_FAT = 9.0;

    // Fibre Constants
    private static final double FIBER_GRAMS_PER_1000_KCAL = 14.0;
    private static final double FIBER_BASELINE_KCAL = 1000.0;

    public int calculateTargetProtein(int caloriesDailyGoal) {
        return (int) Math.round((caloriesDailyGoal * PROTEIN_RATIO) / KCAL_PER_GRAM_PROTEIN);
    }

    public int calculateTargetCarbs(int caloriesDailyGoal) {
        return (int) Math.round((caloriesDailyGoal * CARBS_RATIO) / KCAL_PER_GRAM_CARBS);
    }

    public int calculateTargetFats(int caloriesDailyGoal) {
        return (int) Math.round((caloriesDailyGoal * FATS_RATIO) / KCAL_PER_GRAM_FAT);
    }

    public int calculateTargetFiber(int caloriesDailyGoal) {
        return (int) Math.round((caloriesDailyGoal / FIBER_BASELINE_KCAL) * FIBER_GRAMS_PER_1000_KCAL);
    }
}
