package com.cmpt276.project.porject.CalorieTracker;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.Meal;
import com.cmpt276.project.porject.meals.MealRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class CalorieTrackerController {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MacroCalculator macroCalculator;

    @Autowired
    private com.cmpt276.project.porject.meals.MealService mealService;

    @Autowired
    private com.cmpt276.project.porject.meals.FoodApiService foodApiService;

    /**
     * Populates the calorie tracker page with the user's meals and macro targets
     * 
     * - Validating the user session
     * - Fetch all meals logged from midnight to midnight of the current day
     * - Aggregate total consumed calories, protein, carbs, fats, and fiber
     * - Calculate dynamic targets and completion percentages based on user goals
     * - Populate the Thymeleaf model with the calculated data
     */
    @GetMapping("/calorie-tracker")
    public String getCalorieTracker(Model model, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("session_user");

            if (user == null)
                return "redirect:/login";

            // Fetch todays meals
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

            List<Meal> meals = mealRepository.findByUserUidAndConsumedDateBetween(user.getUid(), startOfDay, endOfDay);

            // Sum up totals using MealService
            Map<String, Double> totals = mealService.getTotalsForMeals(meals);

            List<MealSummary> mealSummaries = new java.util.ArrayList<>();

            for (Meal meal : meals) {
                MealSummary summary = summarizeMeal(meal);
                if (summary != null) {
                    mealSummaries.add(summary);
                }
            }

            double totalCalories = totals.getOrDefault("calories", 0.0);
            double totalProtein = totals.getOrDefault("protein", 0.0);
            double totalCarbs = totals.getOrDefault("carbs", 0.0);
            double totalFats = totals.getOrDefault("fats", 0.0);
            double totalFiber = totals.getOrDefault("fiber", 0.0);

            // Targets
            int dailyGoalCals = user.getCaloriesDailyGoal();
            if (dailyGoalCals <= 0) {
                dailyGoalCals = 2000;
            }

            int targetProtein = macroCalculator.calculateTargetProtein(dailyGoalCals);
            int targetCarbs = macroCalculator.calculateTargetCarbs(dailyGoalCals);
            int targetFats = macroCalculator.calculateTargetFats(dailyGoalCals);
            int targetFiber = macroCalculator.calculateTargetFiber(dailyGoalCals);

            // Percentages
            int totalPercent = calcPercent(totalCalories, dailyGoalCals);
            int proteinPercent = calcPercent(totalProtein, targetProtein);
            int carbsPercent = calcPercent(totalCarbs, targetCarbs);
            int fatsPercent = calcPercent(totalFats, targetFats);
            int fiberPercent = calcPercent(totalFiber, targetFiber);

            model.addAttribute("mealSummaries", mealSummaries);
            model.addAttribute("totalCalories", Math.round(totalCalories));
            model.addAttribute("totalProtein", Math.round(totalProtein));
            model.addAttribute("totalCarbs", Math.round(totalCarbs));
            model.addAttribute("totalFats", Math.round(totalFats));
            model.addAttribute("totalFiber", Math.round(totalFiber));

            model.addAttribute("dailyGoalCals", dailyGoalCals);
            model.addAttribute("targetProtein", targetProtein);
            model.addAttribute("targetCarbs", targetCarbs);
            model.addAttribute("targetFats", targetFats);
            model.addAttribute("targetFiber", targetFiber);

            model.addAttribute("totalPercent", totalPercent);
            model.addAttribute("proteinPercent", proteinPercent);
            model.addAttribute("carbsPercent", carbsPercent);
            model.addAttribute("fatsPercent", fatsPercent);
            model.addAttribute("fiberPercent", fiberPercent);

            return "calorieTracker";
        }

        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard";
        }
    }

    /* Logs a meal to the database based on the provided description. */
    @PostMapping("/calorie-tracker/log")
    public String logMeal(@RequestParam(required = false) String description, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        if (description != null && !description.trim().isEmpty()) {
            List<Food> foods = foodApiService.getMealNutrition(description);

            if (foods != null && !foods.isEmpty()) {
                mealService.addMeal(user, "Chat Log", LocalDateTime.now(), foods);
            }
        }

        return "redirect:/calorie-tracker";
    }

    /* Helper: Calculates the percentage of the target that has been met */
    private int calcPercent(double total, int target) {
        return target > 0 ? (int) Math.round((total / target) * 100) : 0;
    }

    /* Helper: Creates a summary of a meal */
    public record MealSummary(int id, String name, int calories, int protein, int carbs, int fats) {

    }

    /* Helper: Summarizes a meal */
    private MealSummary summarizeMeal(Meal meal) {
        List<Food> foods = meal.getFoods();

        if (foods == null || foods.isEmpty()) {
            return null; // Skip empty meals
        }

        // Generate the display name
        String name = foods.get(0).getFoodName();
        if (foods.size() > 1) {
            name += " + " + (foods.size() - 1) + " more";
        }

        double cals = 0, pro = 0, carbs = 0, fats = 0;
        for (Food food : foods) {
            cals += food.getCalories();
            pro += food.getProtien(); // Typo in food model
            carbs += food.getCarbs();
            fats += food.getFats();
        }

        // Return the packaged data
        return new MealSummary(
                meal.getId(),
                name,
                (int) Math.round(cals),
                (int) Math.round(pro),
                (int) Math.round(carbs),
                (int) Math.round(fats));
    }
}
