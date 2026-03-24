package com.cmpt276.project.porject.meals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MealRepositoryTest {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Jane", "Doe", "janedoe", "secret123", "USER");
        userRepository.save(user);
    }

    private Meal buildMeal(String name, LocalDateTime date) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setMealName(name);
        meal.setMealType("Lunch");
        meal.setConsumedDate(date);
        return meal;
    }

    // Tests that a meal can be saved and retrieved by ID.
    @Test
    public void testSaveAndFindById() {
        Meal meal = mealRepository.save(buildMeal("Lunch", LocalDateTime.now()));

        Optional<Meal> found = mealRepository.findById(meal.getId());

        assertTrue(found.isPresent());
        assertEquals("Lunch", found.get().getMealName());
    }

    // Tests that findAll returns all saved meals.
    @Test
    public void testFindAllMeals() {
        mealRepository.save(buildMeal("Breakfast", LocalDateTime.now()));
        mealRepository.save(buildMeal("Dinner", LocalDateTime.now()));

        List<Meal> allMeals = mealRepository.findAll();

        assertEquals(2, allMeals.size());
    }

    // Tests that findByUserUidOrderByConsumedDateDesc returns meals for only user
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

    // Tests that findByUserUidAndConsumedDateBetween returns only meals within the
    // given range.
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
}
