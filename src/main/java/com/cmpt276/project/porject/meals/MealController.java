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

        model.addAttribute("totalPercent", 67);
        model.addAttribute("totalSpent", 1340);
        model.addAttribute("totalGoal", 2000);

        model.addAttribute("proteinPercent", 82);
        model.addAttribute("proteinSpent", 123);
        model.addAttribute("proteinGoal", 150);

        model.addAttribute("carbsPercent", 74);
        model.addAttribute("carbsSpent", 185);
        model.addAttribute("carbsGoal", 250);

        model.addAttribute("macrosPercent", 91);
        model.addAttribute("fatsSpent", 64);
        model.addAttribute("fatsGoal", 70);

    }

    /**
     * Displays the add-food page.
     *
     */
    @GetMapping("/calorieTracker")
    public String getAddFoodPage(Model model, HttpServletRequest request) {
        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        populateCalorieTrackerData(model, user);
        return "calorieTracker";
    }

    /**
     * Handles food description input and calculates nutrition results.
     * 
     */
    @PostMapping("/calorieTracker/search")
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
            return "redirect:/calorieTracker";
        }

        try {
            List<String> queryParts = new ArrayList<>();

            for (String foodName : foodOrder) {
                String paramKey = "requestedServSizes[" + foodName + "]";
                String servingSizeValue = allParams.get(paramKey);

                if (servingSizeValue == null || servingSizeValue.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "Missing serving size for food: " + foodName);
                    return "redirect:/calorieTracker";
                }

                double servingSize = Double.parseDouble(servingSizeValue);

                if (servingSize <= 0) {
                    redirectAttributes.addFlashAttribute("error", "Serving size must be greater than 0 for food: " + foodName);
                    return "redirect:/calorieTracker";
                }

                queryParts.add(servingSize + " grams " + foodName);
            }

            String adjustedQuery = String.join(", ", queryParts);
            System.out.println("adjustedQuery = " + adjustedQuery);

            List<Food> foods = foodApiService.getMealNutrition(adjustedQuery);

            if (foods == null || foods.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No foods were returned from the updated serving sizes.");
                return "redirect:/calorieTracker";
            }

            mealService.addMeal(user, mealName, mealType, parsedConsumedDate, foods);
            redirectAttributes.addFlashAttribute("successMessage", "Meal added successfully!");
            return "redirect:/calorieTracker";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add meal.");
            return "redirect:/calorieTracker";
        }
    }
}
