package com.cmpt276.project.porject.meals.unit;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.Meal;
import com.cmpt276.project.porject.meals.MealRepository;
import com.cmpt276.project.porject.meals.MealService;
import com.cmpt276.project.porject.rank.RewardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealServiceTest {

    @Mock
    private MealRepository mealEntryRepository;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private MealService mealService;

    private Food buildFood(double calories, double protein, double carbs, double fats,
            double fiber, double sugar, double sodium, double potassium, double cholesterol) {
        return new Food("food_test1", 100.0, calories, protein, carbs, fats, fiber, sugar, sodium, potassium,
                cholesterol);
    }

    // addMeal
    @Test
    public void addMeal_savesAndRewards() {
        User user = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
        Food food = buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        mealService.addMeal(user, "Lunch", "Lunch", LocalDateTime.now(), List.of(food));

        verify(mealEntryRepository, times(1)).save(any(Meal.class));
        verify(rewardService, times(1)).rewardForLoggingMeal(user);
    }

    // getMealTotals w/ no foods
    @Test
    public void getMealTotals_emptyFoods_returnsAllZeroes() {
        Meal meal = new Meal();

        Map<String, Double> totals = mealService.getMealTotals(meal);

        assertEquals(0.0, totals.get("calories"));
        assertEquals(0.0, totals.get("protein"));
        assertEquals(0.0, totals.get("carbs"));
        assertEquals(0.0, totals.get("fats"));
        assertEquals(0.0, totals.get("fiber"));
        assertEquals(0.0, totals.get("sugar"));
        assertEquals(0.0, totals.get("sodium"));
        assertEquals(0.0, totals.get("potassium"));
        assertEquals(0.0, totals.get("cholesterol"));
    }

    // getMealTotals w/ one food
    @Test
    public void getMealTotals_singleFood_matchesFoodValues() {
        Meal meal = new Meal();

        Food food = buildFood(300.0, 15.0, 40.0, 8.0, 3.0, 5.0, 200.0, 400.0, 20.0);

        meal.addFood(food);

        Map<String, Double> totals = mealService.getMealTotals(meal);

        assertEquals(300.0, totals.get("calories"));
        assertEquals(15.0, totals.get("protein"));
        assertEquals(40.0, totals.get("carbs"));
        assertEquals(8.0, totals.get("fats"));
        assertEquals(3.0, totals.get("fiber"));
        assertEquals(5.0, totals.get("sugar"));
        assertEquals(200.0, totals.get("sodium"));
        assertEquals(400.0, totals.get("potassium"));
        assertEquals(20.0, totals.get("cholesterol"));
    }

    // getMealTotals w/ multiple foods
    @Test
    public void getMealTotals_multipleFoods_sumsAllNineFields() {
        Meal meal = new Meal();

        Food food1 = buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);
        Food food2 = buildFood(100.0, 5.0, 15.0, 2.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        meal.addFood(food1);
        meal.addFood(food2);

        Map<String, Double> totals = mealService.getMealTotals(meal);

        assertEquals(300.0, totals.get("calories"));
        assertEquals(15.0, totals.get("protein"));
        assertEquals(45.0, totals.get("carbs"));
        assertEquals(7.0, totals.get("fats"));
        assertEquals(2.0, totals.get("fiber"));
        assertEquals(2.0, totals.get("sugar"));
        assertEquals(20.0, totals.get("sodium"));
        assertEquals(200.0, totals.get("potassium"));
        assertEquals(10.0, totals.get("cholesterol"));
    }

    // getTotalsForMeals w/ multiple meals
    @Test
    public void getTotalsForMeals_multipleMeals_aggregatesCorrectly() {
        Food food1 = buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);
        Food food2 = buildFood(100.0, 5.0, 15.0, 2.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        Meal meal1 = new Meal();
        meal1.addFood(food1);

        Meal meal2 = new Meal();
        meal2.addFood(food2);

        Map<String, Double> totals = mealService.getTotalsForMeals(List.of(meal1, meal2));

        assertEquals(300.0, totals.get("calories"));
        assertEquals(15.0, totals.get("protein"));
        assertEquals(45.0, totals.get("carbs"));
        assertEquals(7.0, totals.get("fats"));
        assertEquals(2.0, totals.get("fiber"));
        assertEquals(2.0, totals.get("sugar"));
        assertEquals(20.0, totals.get("sodium"));
        assertEquals(200.0, totals.get("potassium"));
        assertEquals(10.0, totals.get("cholesterol"));
    }

    // getTotalsForMeals w/ no meals
    @Test
    public void getTotalsForMeals_emptyMealList_returnsAllZeroes() {
        Map<String, Double> totals = mealService.getTotalsForMeals(List.of());

        assertEquals(0.0, totals.get("calories"));
        assertEquals(0.0, totals.get("protein"));
        assertEquals(0.0, totals.get("carbs"));
        assertEquals(0.0, totals.get("fats"));
    }

    // getTotalsForMeals w/ deleted food
    @Test
    public void getTotalsForMeals_mealWithOneFoodDeleted_doesNotCountDeletedFood() {
        Food food1 = buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);
        Food food2 = buildFood(100.0, 5.0, 15.0, 2.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        Meal meal = new Meal();
        List<Food> mutableFoods = new ArrayList<>();

        mutableFoods.add(food1);
        mutableFoods.add(food2);

        meal.setFoods(mutableFoods);
        meal.getFoods().remove(food2);

        Map<String, Double> totals = mealService.getTotalsForMeals(List.of(meal));

        assertEquals(200.0, totals.get("calories"), "Only food1 should count after food2 is removed");
    }

    // Date range queries
    @Test
    public void getTodayTotals_callsRepositoryWithCorrectRange() {
        when(mealEntryRepository.findByUserUidAndConsumedDateBetween(anyInt(), any(), any()))
                .thenReturn(List.of());

        mealService.getTodayTotals(1);

        verify(mealEntryRepository, times(1))
                .findByUserUidAndConsumedDateBetween(eq(1), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void getWeeklyTotals_callsRepositoryWithRange() {
        when(mealEntryRepository.findByUserUidAndConsumedDateBetween(anyInt(), any(), any()))
                .thenReturn(List.of());

        mealService.getWeeklyTotals(1);

        verify(mealEntryRepository, times(1))
                .findByUserUidAndConsumedDateBetween(eq(1), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void getMonthlyTotals_callsRepositoryWithRange() {
        when(mealEntryRepository.findByUserUidAndConsumedDateBetween(anyInt(), any(), any()))
                .thenReturn(List.of());

        mealService.getMonthlyTotals(1);

        verify(mealEntryRepository, times(1))
                .findByUserUidAndConsumedDateBetween(eq(1), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    // getUserMeals
    @Test
    public void getUserMeals_returnsListFromRepository() {
        Meal meal = new Meal();
        when(mealEntryRepository.findByUserUidOrderByConsumedDateDesc(1)).thenReturn(List.of(meal));

        List<Meal> result = mealService.getUserMeals(1);

        assertEquals(1, result.size());
        verify(mealEntryRepository, times(1)).findByUserUidOrderByConsumedDateDesc(1);
    }
}
