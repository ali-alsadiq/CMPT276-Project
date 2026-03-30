package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cmpt276.project.porject.auth.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for handling meal entry creation.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - Displays the add-food page.
 * - Calculates nutrition from a food description using FoodApiService.
 * - Saves a MealEntry containing multiple Food objects.
 * - Ties each saved meal to the currently logged-in user.
 * 
 * To check if a user is logged in, use:
 * User user = (User) request.getSession().getAttribute("session_user");
 */
@Controller
public class MealController {

    @Autowired
    private MealService mealService;

    @Autowired
    private FoodApiService foodApiService;

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("session_user");
    }

    private void populateCalorieTrackerData(Model model, User user) {
        List<Meal> meals = mealService.getUserMeals(user.getUid());
        Map<String, Double> todayTotals = mealService.getTodayTotals(user.getUid());

        model.addAttribute("meals", meals);
        model.addAttribute("todayTotals", todayTotals);

        double totalSpent = todayTotals.getOrDefault("calories", 0.0);
        double proteinSpent = todayTotals.getOrDefault("protein", 0.0);
        double carbsSpent = todayTotals.getOrDefault("carbs", 0.0);
        double fatsSpent = todayTotals.getOrDefault("fats", 0.0);
        double fibreSpent = todayTotals.getOrDefault("fiber", 0.0);

        // Placehodler values
        double totalGoal = user.getWeeklyCaloriesConsumedTarget() > 0 ? user.getWeeklyCaloriesConsumedTarget() : 2000.0;
        double proteinGoal = user.getWeeklyProtienTarget() > 0 ? user.getWeeklyProtienTarget() : 2000.0;
        double carbsGoal = user.getWeeklyCarbsTarget() > 0 ? user.getWeeklyCarbsTarget() : 2000.0;
        double fatsGoal = user.getWeeklyFatsTarget() > 0 ? user.getWeeklyFatsTarget() : 2000.0;
        double fibreGoal = user.getWeeklyFibreTarget() > 0 ? user.getWeeklyFibreTarget() : 2000.0;

        int totalPercent = calculatePercentage(totalSpent, totalGoal);
        int proteinPercent = calculatePercentage(proteinSpent, proteinGoal);
        int carbsPercent = calculatePercentage(carbsSpent, carbsGoal);
        int fatsPercent = calculatePercentage(fatsSpent, fatsGoal);
        int fibrePercent = calculatePercentage(fibreSpent, fibreGoal);

        model.addAttribute("totalPercent", totalPercent);
        model.addAttribute("totalSpent", (int) totalSpent);
        model.addAttribute("totalGoal", (int) totalGoal);

        model.addAttribute("proteinPercent", proteinPercent);
        model.addAttribute("proteinSpent", (int) proteinSpent);
        model.addAttribute("proteinGoal", (int) proteinGoal);

        model.addAttribute("carbsPercent", carbsPercent);
        model.addAttribute("carbsSpent", (int) carbsSpent);
        model.addAttribute("carbsGoal", (int) carbsGoal);

        model.addAttribute("fatsPercent", fatsPercent);
        model.addAttribute("fatsSpent", (int) fatsSpent);
        model.addAttribute("fatsGoal", (int) fatsGoal);

        model.addAttribute("fibrePercent", fibrePercent);
        model.addAttribute("fibreSpent", (int) fibreSpent);
        model.addAttribute("fibreGoal", (int) fibreGoal);

    }

    /**
     * Displays the add-food page.
     *
     */
    @GetMapping("/calorie-tracker")
    public String getAddFoodPage(Model model, HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        populateCalorieTrackerData(model, user);
        return "calorie-tracker/calorie-tracker";
    }

    /**
     * Handles food description input and calculates nutrition results.
     * 
     */
    @PostMapping("/calorie-tracker/search")
    @ResponseBody
    public List<Food> searchMealFromCalorieTracker(
            @RequestParam String foodDescription,
            HttpServletRequest request) {

        User user = getCurrentUser(request);
        if (user == null) {
            return List.of();
        }

        String trimmedDescription = foodDescription.trim();
        if (trimmedDescription.isEmpty()) {
            return List.of();
        }

        List<Food> mealFoods = foodApiService.getMealNutrition(trimmedDescription);
        System.out.println(mealFoods.toString());
        return mealFoods != null ? mealFoods : List.of();
    }

    /**
     * Saves a meal entry for the logged-in user.
     * 
     */
    @PostMapping("/meals/add")
    public String addMeal(
            @RequestParam String mealType,
            @RequestParam String consumedDate,
            @RequestParam(required = false, defaultValue = "") String mealName,
            @RequestParam(name = "foodOrder") List<String> foodOrder,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        LocalDateTime parsedConsumedDate;
        try {
            parsedConsumedDate = LocalDateTime.parse(consumedDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Please enter a valid consumed date and time.");
            return "redirect:/calorie-tracker";
        }

        try {
            List<String> queryParts = new ArrayList<>();

            for (String foodName : foodOrder) {
                String paramKey = "requestedServSizes[" + foodName + "]";
                String servingSizeValue = allParams.get(paramKey);

                if (servingSizeValue == null || servingSizeValue.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "Missing serving size for food: " + foodName);
                    return "redirect:/calorie-tracker";
                }

                double servingSize = Double.parseDouble(servingSizeValue);

                if (servingSize <= 0) {
                    redirectAttributes.addFlashAttribute("error",
                            "Serving size must be greater than 0 for food: " + foodName);
                    return "redirect:/calorie-tracker";
                }

                // Fix 10x multiplier bug: CalorieNinja evaluates '100.0 grams' as '1000 grams'
                // by accidentally stripping the decimal point
                String formattedServingSize;
                if (servingSize == Math.floor(servingSize) && !Double.isInfinite(servingSize)) {
                    formattedServingSize = String.valueOf((long) servingSize);
                } else {
                    formattedServingSize = String.valueOf(servingSize);
                }

                queryParts.add(formattedServingSize + " grams " + foodName);
            }

            String adjustedQuery = String.join(", ", queryParts);
            System.out.println("adjustedQuery = " + adjustedQuery);

            List<Food> foods = foodApiService.getMealNutrition(adjustedQuery);

            if (foods == null || foods.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No foods were returned from the updated serving sizes.");
                return "redirect:/calorie-tracker";
            }

            mealService.addMeal(user, mealName, mealType, parsedConsumedDate, foods);
            redirectAttributes.addFlashAttribute("successMessage", "Meal added successfully!");
            return "redirect:/calorie-tracker";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add meal.");
            return "redirect:/calorie-tracker";
        }
    }

    private int calculatePercentage(double spent, double goal) {
        if (goal <= 0) {
            return 0;
        }

        return (int) Math.min(100, Math.round((spent / goal) * 100));
    }
}
