package com.cmpt276.project.porject.meals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.trackers.nutrition.Food;

/**
 * Service for handling meal-related business logic.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - Use this class to add meals and calculate nutrition totals.
 * - Keep nutrition calculations here instead of in the controller.
 * - Weekly totals always run from Monday to Sunday.
 */
@Service
public class MealEntryService {
    @Autowired
    private MealEntryRepository mealEntryRepository;

    /**
     * Adds a new meal for a user.
     * 
     * @param user         User logging the meal.
     * @param mealType     Type of meal (Breakfast, Lunch, Dinner, Snack).
     * @param consumedDate Date and time the meal was consumed.
     * @param foods        List of foods in the meal.
     */
    public void addMeal(User user, String mealType, LocalDateTime consumedDate, List<Food> foods) {
        MealEntry mealEntry = new MealEntry(user, mealType, consumedDate);

        for (Food food : foods) {
            food.setMealEntry(mealEntry);
        }

        mealEntry.setFoods(foods);
        mealEntryRepository.save(mealEntry);
    }

    /**
     * Gets all meals for a user from newest to oldest.
     * 
     * @param uid User ID
     * @return List of meal entries
     */
    public List<MealEntry> getUserMeals(int uid) {
        return mealEntryRepository.findByUserUidOrderByConsumedDateDesc(uid);
    }

    /**
     * Calculates nutrition totals for a single meal.
     * 
     * @param mealEntry Meal entry
     * @return Map containing nutrition totals for the meal
     */
    public Map<String, Double> getMealTotals(MealEntry mealEntry) {
        Map<String, Double> totals = createEmptyTotals();

        for (Food food : mealEntry.getFoods()) {
            addFoodToTotals(totals, food);
        }

        return totals;
    }

    /**
     * Calculates nutrition totals for all meals in a list.
     * 
     * @param meals List of meals
     * @return Map containing combined nutrition totals
     */
    public Map<String, Double> getTotalsForMeals(List<MealEntry> meals) {
        Map<String, Double> totals = createEmptyTotals();

        for (MealEntry meal : meals) {
            for (Food food : meal.getFoods()) {
                addFoodToTotals(totals, food);
            }
        }

        return totals;
    }

    /**
     * Calculates nutrition totals consumed today.
     * 
     * @param uid User ID
     * @return Map containing nutrition totals consumed today
     */
    public Map<String, Double> getTodayTotals(int uid) {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndConsumedDateBetween(uid, start, end);

        return getTotalsForMeals(meals);
    }

    /**
     * Calculates nutrition totals consumed this week.
     * 
     * Week always runs from Monday to Sunday.
     * 
     * @param uid User ID
     * @return Map containing nutrition totals consumed this week
     */
    public Map<String, Double> getWeeklyTotals(int uid) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = monday.plusDays(7).atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndConsumedDateBetween(uid, start, end);

        return getTotalsForMeals(meals);
    }

    /**
     * Calculates nutrition totals consumed this month.
     * 
     * @param uid User ID
     * @return Map containing nutrition totals consumed this month
     */
    public Map<String, Double> getMonthlyTotals(int uid) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);

        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        LocalDateTime end = firstDayOfNextMonth.atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndConsumedDateBetween(uid, start, end);

        return getTotalsForMeals(meals);
    }

    /**
     * Creates an empty nutrition totals map.
     * 
     * @return Map with all nutrition values initialized to 0
     */
    private Map<String, Double> createEmptyTotals() {
        Map<String, Double> totals = new HashMap<>();

        totals.put("calories", 0.0);
        totals.put("protein", 0.0);
        totals.put("carbs", 0.0);
        totals.put("fats", 0.0);
        totals.put("fiber", 0.0);
        totals.put("sugar", 0.0);
        totals.put("sodium", 0.0);
        totals.put("potassium", 0.0);
        totals.put("cholesterol", 0.0);

        return totals;
    }

    /**
     * Adds one food item's nutrition values to the totals map.
     * 
     * @param totals Nutrition totals map
     * @param food   Food item
     */
    private void addFoodToTotals(Map<String, Double> totals, Food food) {
        totals.put("calories", totals.get("calories") + food.getCalories());
        totals.put("protein", totals.get("protein") + food.getProtien());
        totals.put("carbs", totals.get("carbs") + food.getCarbs());
        totals.put("fats", totals.get("fats") + food.getFats());
        totals.put("fiber", totals.get("fiber") + food.getFiber());
        totals.put("sugar", totals.get("sugar") + food.getSugar());
        totals.put("sodium", totals.get("sodium") + food.getSodium());
        totals.put("potassium", totals.get("potassium") + food.getPotassium());
        totals.put("cholesterol", totals.get("cholesterol") + food.getCholesterol());
    }
}
