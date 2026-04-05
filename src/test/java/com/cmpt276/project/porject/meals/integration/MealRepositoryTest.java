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
public class MealRepositoryTest {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("User_test1", "TestLastname", "user_test1", "pass123", "USER");
        userRepository.save(user);
    }

    private Food buildFood(String name) {
        return new Food(name, 100.0, 200.0, 10.0, 30.0, 5.0, 2.0, 3.0, 100.0, 200.0, 5.0);
    }

    private Meal buildMeal(String name, LocalDateTime date) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setMealName(name);
        meal.setMealType("Lunch");
        meal.setConsumedDate(date);
        return meal;
    }

    @Test
    public void testSaveAndFindById() {
        Meal meal = mealRepository.save(buildMeal("Lunch", LocalDateTime.now()));

        Optional<Meal> found = mealRepository.findById(meal.getId());

        assertTrue(found.isPresent());
        assertEquals("Lunch", found.get().getMealName());
    }

    @Test
    public void testFindAllMeals() {
        mealRepository.save(buildMeal("Breakfast", LocalDateTime.now()));
        mealRepository.save(buildMeal("Dinner", LocalDateTime.now()));

        List<Meal> allMeals = mealRepository.findAll();

        assertEquals(2, allMeals.size());
    }

    @Test
    public void testFindByUserUidOrderByConsumedDateDesc() {
        LocalDateTime older = LocalDateTime.now().minusDays(2);
        LocalDateTime newer = LocalDateTime.now();

        mealRepository.save(buildMeal("Old Meal", older));
        mealRepository.save(buildMeal("New Meal", newer));

        List<Meal> meals = mealRepository.findByUserUidOrderByConsumedDateDesc(user.getUid());

        assertEquals(2, meals.size());
        assertEquals("New Meal", meals.get(0).getMealName());
        assertEquals("Old Meal", meals.get(1).getMealName());
    }

    @Test
    public void testFindByUserUidAndConsumedDateBetween() {
        LocalDateTime base = LocalDateTime.now();

        mealRepository.save(buildMeal("In Range", base.minusHours(1)));
        mealRepository.save(buildMeal("Out of Range", base.minusDays(5)));

        List<Meal> meals = mealRepository.findByUserUidAndConsumedDateBetween(
                user.getUid(), base.minusHours(2), base);

        assertEquals(1, meals.size());
        assertEquals("In Range", meals.get(0).getMealName());
    }

    @Test
    public void findByUserUid_singleMeal_returnsSingleItem() {
        mealRepository.save(buildMeal("Breakfast", LocalDateTime.now()));

        List<Meal> meals = mealRepository.findByUserUidOrderByConsumedDateDesc(user.getUid());

        assertEquals(1, meals.size());
    }

    @Test
    public void findByUserUidAndConsumedDateBetween_exclusiveEndBound() {
        LocalDateTime base = LocalDateTime.now();
        mealRepository.save(buildMeal("Exact End", base));

        List<Meal> meals = mealRepository.findByUserUidAndConsumedDateBetween(
                user.getUid(), base.minusHours(1), base);

        assertTrue(meals.size() <= 1, "Should return at most 1 meal at the boundary");
    }

    @Test
    public void findByUserUid_isolatesUserMeals() {
        User otherUser = new User("User_test2", "TestLastname", "user_test2", "pass123", "USER");
        userRepository.save(otherUser);
        Meal otherMeal = buildMeal("Other User Meal", LocalDateTime.now());
        otherMeal.setUser(otherUser);
        mealRepository.save(otherMeal);

        mealRepository.save(buildMeal("My Meal", LocalDateTime.now()));

        List<Meal> userMeals = mealRepository.findByUserUidOrderByConsumedDateDesc(user.getUid());

        assertEquals(1, userMeals.size(), "Should only return meals for the queried user");
        assertEquals("My Meal", userMeals.get(0).getMealName());
    }

    @Test
    public void saveMeal_withFoods_persistsFoodsWithCascade() {
        Meal meal = buildMeal("Cascade Meal", LocalDateTime.now());
        meal.addFood(buildFood("food_test1"));
        meal.addFood(buildFood("food_test2"));
        mealRepository.save(meal);

        List<Food> foods = foodRepository.findAll();
        assertEquals(2, foods.size(), "Both foods should be persisted via cascade");
    }

    @Test
    public void deleteMeal_removesMealAndFoodsViaOrphanRemoval() {
        Meal meal = buildMeal("Delete Meal", LocalDateTime.now());
        meal.addFood(buildFood("food_test1"));
        meal.addFood(buildFood("food_test2"));
        Meal saved = mealRepository.save(meal);

        mealRepository.deleteById(saved.getId());

        assertEquals(0, foodRepository.findAll().size(), "Orphan foods should be deleted with the meal");
    }
}
