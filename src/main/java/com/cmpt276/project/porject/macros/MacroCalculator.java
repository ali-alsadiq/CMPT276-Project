package com.cmpt276.project.porject.macros;

import org.springframework.stereotype.Service;

@Service
public class MacroCalculator {
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
}
