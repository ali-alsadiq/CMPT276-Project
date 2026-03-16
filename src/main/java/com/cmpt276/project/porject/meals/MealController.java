package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private MealService mealEntryService;

    @Autowired
    private FoodApiService foodApiService;

    /**
     * Displays the add-food page.
     * 
     * @param model   Model to add attributes to.
     * @param request Request to get session from.
     * @return String representing the view to return.
     */
    // @GetMapping("/add-food")
    // public String getAddFoodPage(Model model, HttpServletRequest request) {
    //     HttpSession session = request.getSession();
    //     User user = (User) session.getAttribute("session_user");

    //     if (user == null) {
    //         return "redirect:/login";
    //     }

    //     return "trackers/nutrition/add-food";
    // }

    /**
     * Handles food description input and calculates nutrition results.
     * 
     * @param foodDescription Food description entered by the user.
     * @param model           Model to add attributes to.
     * @param request         Request to get session from.
     * @return String representing the view to return.
     */
    @PostMapping("/add-food")
    public String calculateFoodNutrition(
            @RequestParam String foodDescription,
            Model model,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        String trimmedDescription = foodDescription.trim();
        model.addAttribute("foodDescriptionVal", trimmedDescription);

        if (trimmedDescription.isEmpty()) {
            model.addAttribute("error", "Please enter a food description.");
            return "add-food";
        }

        List<Food> mealFoods = foodApiService.getMealNutrition(trimmedDescription);

        if (mealFoods == null || mealFoods.isEmpty()) {
            model.addAttribute("error", "No foods were found for that description.");
            return "add-food";
        }

        model.addAttribute("mealFoods", mealFoods);

        return "add-food";
    }

    /**
     * Saves a meal entry for the logged-in user.
     * 
     * @param mealType      Type of meal (Breakfast, Lunch, Dinner, Snack).
     * @param consumedDate  Date and time the meal was consumed.
     * @param foodNames     List of food names.
     * @param servSizes     List of serving sizes.
     * @param calories      List of calories.
     * @param protiens      List of protein values.
     * @param carbs         List of carb values.
     * @param fats          List of fat values.
     * @param fibers        List of fiber values.
     * @param sugars        List of sugar values.
     * @param sodiums       List of sodium values.
     * @param potassiums    List of potassium values.
     * @param cholesterols  List of cholesterol values.
     * @param model         Model to add attributes to.
     * @param request       Request to get session from.
     * @return String representing the view to return.
     */
  @PostMapping("/meals/add")
    public String addMeal(
            @RequestParam String mealType,
            @RequestParam String consumedDate,
            @RequestParam(name = "foodName") List<String> foodNames,
            @RequestParam(name = "servSize") List<Double> servSizes,
            @RequestParam(name = "calories") List<Double> calories,
            @RequestParam(name = "protien") List<Double> protiens,
            @RequestParam(name = "carbs") List<Double> carbs,
            @RequestParam(name = "fats") List<Double> fats,
            @RequestParam(name = "fiber") List<Double> fibers,
            @RequestParam(name = "sugar") List<Double> sugars,
            @RequestParam(name = "sodium") List<Double> sodiums,
            @RequestParam(name = "potassium") List<Double> potassiums,
            @RequestParam(name = "cholesterol") List<Double> cholesterols,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("session_user");

        if (user == null) {
            return "redirect:/login";
        }

        LocalDateTime parsedConsumedDate;
        try {
            parsedConsumedDate = LocalDateTime.parse(consumedDate);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Please enter a valid consumed date and time.");
            return "redirect:/add-food";
        }

        try {
            List<Food> foods = new ArrayList<>();

            for (int i = 0; i < foodNames.size(); i++) {

                System.out.println(foodNames.get(i));
                Food food = new Food();
                food.setFoodName(foodNames.get(i));
                food.setServSize(servSizes.get(i));
                food.setCalories(calories.get(i));
                food.setProtien(protiens.get(i));
                food.setCarbs(carbs.get(i));
                food.setFats(fats.get(i));
                food.setFiber(fibers.get(i));
                food.setSugar(sugars.get(i));
                food.setSodium(sodiums.get(i));
                food.setPotassium(potassiums.get(i));
                food.setCholesterol(cholesterols.get(i));

                foods.add(food);
            }

            mealEntryService.addMeal(user, mealType, parsedConsumedDate, foods);

            redirectAttributes.addFlashAttribute("successMessage", "Meal added successfully!");
            return "redirect:/add-food";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add meal.");
            return "redirect:/add-food";
        }
    }
}
