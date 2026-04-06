package com.cmpt276.project.porject.nutrition.unit;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.Meal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MealTest {
    private User testUser() {
        return new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
    }

    private Food buildFood(double calories, double protein, double carbs, double fats,
            double fiber, double sugar, double sodium, double potassium, double cholesterol) {
        return new Food("food_test1", 100.0, calories, protein, carbs, fats, fiber, sugar, sodium, potassium,
                cholesterol);
    }

    // Constructor null foods
    @Test
    public void constructor_nullFoods_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Meal(testUser(), "Test Meal", "Lunch", LocalDateTime.now(), null));
    }

    // Constructor empty foods
    @Test
    public void constructor_emptyFoods_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Meal(testUser(), "Test Meal", "Lunch", LocalDateTime.now(), List.of()));
    }

    // Constructor valid foods
    @Test
    public void constructor_validFoods_doesNotThrow() {
        Food food = buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 1.0, 10.0, 100.0, 5.0);
        assertDoesNotThrow(() -> new Meal(testUser(), "Test Meal", "Lunch", LocalDateTime.now(), List.of(food)));
    }

    // addFood
    @Test
    public void addFood_setsBackreference() {
        Meal meal = new Meal();
        Food food = buildFood(100.0, 5.0, 20.0, 2.0, 1.0, 1.0, 5.0, 50.0, 2.0);

        meal.addFood(food);

        assertSame(meal, food.getMeal(), "addFood should set the meal back-reference on the Food");
    }

    // addFood null
    @Test
    public void addFood_null_isNoOp() {
        Meal meal = new Meal();

        assertDoesNotThrow(() -> meal.addFood(null));
        assertEquals(0, meal.getFoods().size());
    }

    // addFood multiple foods
    @Test
    public void addFood_multipleFoods_allAddedToList() {
        Meal meal = new Meal();
        meal.addFood(buildFood(100.0, 5.0, 20.0, 2.0, 1.0, 1.0, 5.0, 50.0, 2.0));
        meal.addFood(buildFood(200.0, 10.0, 30.0, 5.0, 2.0, 2.0, 10.0, 100.0, 5.0));

        assertEquals(2, meal.getFoods().size());
    }

    // setFoods replaces existing foods
    @Test
    public void setFoods_replacesExistingFoods() {
        Meal meal = new Meal();
        meal.addFood(buildFood(100.0, 5.0, 20.0, 2.0, 1.0, 1.0, 5.0, 50.0, 2.0));

        Food newFood = buildFood(300.0, 15.0, 40.0, 8.0, 3.0, 3.0, 15.0, 150.0, 8.0);
        meal.setFoods(List.of(newFood));

        assertEquals(1, meal.getFoods().size());
        assertEquals(300.0, meal.getCalories(), "Should reflect only the new food after setFoods");
    }

    // setFoods null
    @Test
    public void setFoods_null_clearsList() {
        Meal meal = new Meal();
        meal.addFood(buildFood(100.0, 5.0, 20.0, 2.0, 1.0, 1.0, 5.0, 50.0, 2.0));

        meal.setFoods(null);

        assertEquals(0, meal.getFoods().size());
    }

    // getCalories empty foods
    @Test
    public void getCalories_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getCalories());
    }

    // getProtein empty foods
    @Test
    public void getProtein_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getProtein());
    }

    // getCarbs empty foods
    @Test
    public void getCarbs_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getCarbs());
    }

    // getFats empty foods
    @Test
    public void getFats_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getFats());
    }

    // getFiber empty foods
    @Test
    public void getFiber_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getFiber());
    }

    // getSugar empty foods
    @Test
    public void getSugar_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getSugar());
    }

    // getSodium empty foods
    @Test
    public void getSodium_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getSodium());
    }

    // getPotassium empty foods
    @Test
    public void getPotassium_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getPotassium());
    }

    // getCholesterol empty foods
    @Test
    public void getCholesterol_emptyFoods_returnsZero() {
        assertEquals(0.0, new Meal().getCholesterol());
    }

    // getCalories single food
    @Test
    public void getCalories_singleFood_matchesFoodValue() {
        Meal meal = new Meal();
        meal.addFood(buildFood(350.0, 15.0, 45.0, 10.0, 4.0, 6.0, 200.0, 400.0, 25.0));

        assertEquals(350.0, meal.getCalories());
    }

    // allGetters single food
    @Test
    public void allGetters_singleFood_matchAllNineFields() {
        Meal meal = new Meal();
        meal.addFood(buildFood(350.0, 15.0, 45.0, 10.0, 4.0, 6.0, 200.0, 400.0, 25.0));

        assertEquals(350.0, meal.getCalories());
        assertEquals(15.0, meal.getProtein());
        assertEquals(45.0, meal.getCarbs());
        assertEquals(10.0, meal.getFats());
        assertEquals(4.0, meal.getFiber());
        assertEquals(6.0, meal.getSugar());
        assertEquals(200.0, meal.getSodium());
        assertEquals(400.0, meal.getPotassium());
        assertEquals(25.0, meal.getCholesterol());
    }

    // allGetters multiple foods
    @Test
    public void allGetters_multipleFoods_sumsAllNineFields() {
        Meal meal = new Meal();
        meal.addFood(buildFood(200.0, 10.0, 30.0, 5.0, 1.0, 2.0, 100.0, 150.0, 10.0));
        meal.addFood(buildFood(100.0, 5.0, 15.0, 3.0, 2.0, 1.0, 50.0, 250.0, 5.0));

        assertEquals(300.0, meal.getCalories());
        assertEquals(15.0, meal.getProtein());
        assertEquals(45.0, meal.getCarbs());
        assertEquals(8.0, meal.getFats());
        assertEquals(3.0, meal.getFiber());
        assertEquals(3.0, meal.getSugar());
        assertEquals(150.0, meal.getSodium());
        assertEquals(400.0, meal.getPotassium());
        assertEquals(15.0, meal.getCholesterol());
    }
}
