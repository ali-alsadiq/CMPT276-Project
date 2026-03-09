package com.cmpt276.project.porject.meals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmpt276.project.porject.auth.User;

/**
 * Service for handling meal-related business logic.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - Use this class to add meals and calculate calorie totals.
 * - Keep meal calculations here instead of in the controller.
 */
@Service
public class MealService {
    @Autowired
    private MealEntryRepository mealEntryRepository;

    /**
     * Adds a new meal for a user.
     * 
     * @param user     User logging the meal.
     * @param foodName Name of the food.
     * @param mealType Type of meal (Breakfast, Lunch, Dinner, Snack).
     * @param calories Calories in the meal.
     * @param eatenAt  Date and time the meal was eaten.
     */
    public void addMeal(User user, String foodName, String mealType, int calories, LocalDateTime eatenAt) {
        MealEntry mealEntry = new MealEntry(user, foodName, mealType, calories, eatenAt);
        mealEntryRepository.save(mealEntry);
    }

    /**
     * Gets all meals for a user from newest to oldest.
     * 
     * @param uid User ID
     * @return List of meal entries
     */
    public List<MealEntry> getUserMeals(int uid) {
        return mealEntryRepository.findByUserUidOrderByEatenAtDesc(uid);
    }

    /**
     * Calculates calories consumed today.
     * 
     * @param uid User ID
     * @return Total calories consumed today
     */
    public int getTodayCalories(int uid) {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndEatenAtBetween(uid, start, end);

        int total = 0;
        for (MealEntry meal : meals) {
            total += meal.getCalories();
        }

        return total;
    }

    /**
     * Calculates calories consumed this week.
     * 
     * Week always runs from Monday to Sunday.
     * 
     * @param uid User ID
     * @return Total calories consumed this week
     */
    public int getWeeklyCalories(int uid) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = monday.plusDays(7).atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndEatenAtBetween(uid, start, end);

        int total = 0;
        for (MealEntry meal : meals) {
            total += meal.getCalories();
        }

        return total;
    }

    /**
     * Calculates calories consumed this month.
     * 
     * @param uid User ID
     * @return Total calories consumed this month
     */
    public int getMonthlyCalories(int uid) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);

        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        LocalDateTime end = firstDayOfNextMonth.atStartOfDay();

        List<MealEntry> meals = mealEntryRepository.findByUserUidAndEatenAtBetween(uid, start, end);

        int total = 0;
        for (MealEntry meal : meals) {
            total += meal.getCalories();
        }

        return total;
    }
}
