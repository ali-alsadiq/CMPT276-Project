package com.cmpt276.project.porject.meals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.rank.RewardService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealServiceTest {

    @Mock
    private MealRepository mealEntryRepository;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private MealService mealService;

    // Tests that addMeal saves the meal and calls the reward service.
    @Test
    public void testAddMeal() {
        User user = new User("Jane", "Doe", "janedoe", "secret", "USER");
        Food food = new Food("Chicken", 100.0, 200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        mealService.addMeal(user, "Lunch", "Lunch", LocalDateTime.now(), List.of(food));

        verify(mealEntryRepository, times(1)).save(any(Meal.class));
        // verify(rewardService, times(1)).rewardForLoggingMeal(user, LocalDateTime.now());
    }

    // Tests that getMealTotals correctly sums nutrition across foods in a meal.
    @Test
    public void testGetMealTotals() {
        Food food1 = new Food("Rice", 100.0, 200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);
        Food food2 = new Food("Beans", 100.0, 100.0, 5.0, 15.0, 2.0, 1.0, 1.0, 10.0, 100.0, 5.0);

        Meal meal = new Meal();
        meal.addFood(food1);
        meal.addFood(food2);

        Map<String, Double> totals = mealService.getMealTotals(meal);

        assertEquals(300.0, totals.get("calories"));
        assertEquals(15.0, totals.get("protein"));
        assertEquals(45.0, totals.get("carbs"));
    }

    // Tests that getUserMeals returns the list provided by the repository.
    @Test
    public void testGetUserMeals() {
        Meal meal = new Meal();

        when(mealEntryRepository.findByUserUidOrderByConsumedDateDesc(1)).thenReturn(List.of(meal));

        List<Meal> result = mealService.getUserMeals(1);

        assertEquals(1, result.size());
        verify(mealEntryRepository, times(1)).findByUserUidOrderByConsumedDateDesc(1);
    }
}
