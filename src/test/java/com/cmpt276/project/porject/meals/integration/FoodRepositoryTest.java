package com.cmpt276.project.porject.meals.integration;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.FoodRepository;
import com.cmpt276.project.porject.meals.Meal;
import com.cmpt276.project.porject.meals.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FoodRepositoryTest {
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    private Meal meal;

    @BeforeEach
    public void setUp() {
        User user = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
        userRepository.save(user);

        meal = new Meal();

        meal.setUser(user);
        meal.setMealName("Lunch");
        meal.setMealType("Lunch");
        meal.setConsumedDate(LocalDateTime.now());

        mealRepository.save(meal);
    }

    private Food buildFood(String name, double calories) {
        Food food = new Food(name, 100.0, calories, 10.0, 20.0, 5.0, 3.0, 2.0, 150.0, 300.0, 10.0);
        food.setMeal(meal);

        return food;
    }

    // testSaveAndFindById
    @Test
    public void testSaveAndFindById() {
        Food food = foodRepository.save(buildFood("food_test1", 165.0));

        Optional<Food> found = foodRepository.findById(food.getId());

        assertTrue(found.isPresent());
        assertEquals("food_test1", found.get().getFoodName());
        assertEquals(165.0, found.get().getCalories());
    }

    // testFindAllFoods
    @Test
    public void testFindAllFoods() {
        foodRepository.save(buildFood("food_test1", 95.0));
        foodRepository.save(buildFood("food_test2", 105.0));

        List<Food> allFoods = foodRepository.findAll();

        assertEquals(2, allFoods.size());
    }

    // testUpdateFood
    @Test
    public void testUpdateFood() {
        Food food = foodRepository.save(buildFood("food_test1", 307.0));

        food.setCalories(350.0);
        foodRepository.save(food);

        Food updated = foodRepository.findById(food.getId()).orElseThrow();

        assertEquals(350.0, updated.getCalories());
    }

    // testDeleteFood
    @Test
    public void testDeleteFood() {
        Food food = foodRepository.save(buildFood("food_test1", 208.0));
        int id = food.getId();

        foodRepository.deleteById(id);

        assertFalse(foodRepository.findById(id).isPresent());
    }
}
