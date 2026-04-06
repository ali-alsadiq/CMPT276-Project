package com.cmpt276.project.porject.rank;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.MealService;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
public class RewardServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealService mealService;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private RewardService rewardService;

    // =========================================================
    //                       FOOD TESTS
    // =========================================================

    @Test
    public void lmeal_oneDay_dailyRR() {
        User user = createUser("meal_one_log_user");
        user.setWeeklyCaloriesConsumedTarget(9999); // avoid weekly goal reward
        user = userRepository.save(user);

        LocalDateTime mealDate = LocalDate.of(2026, 4, 6).atTime(12, 0); // Monday

        mealService.addMeal(
                user,
                "Lunch",
                "Lunch",
                mealDate,
                List.of(createFood(100.0))
        );

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("1 meal RR = " + rr);

        // 1 daily food log = 5 RR
        assertEquals(5, rr);
    }

    @Test
    public void lmeal_week_noGoal_bonusOnly() {
        User user = createUser("meal_week_no_goal_user");
        user.setWeeklyCaloriesConsumedTarget(9999); // impossible target, no weekly goal reward
        user = userRepository.save(user);

        LocalDate monday = LocalDate.of(2026, 4, 6);

        for (int i = 0; i < 7; i++) {
            LocalDateTime mealDate = monday.plusDays(i).atTime(12, 0);

            mealService.addMeal(
                    user,
                    "Meal " + i,
                    "Lunch",
                    mealDate,
                    List.of(createFood(100.0))
            );
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 meal days without goal RR = " + rr);

        // 7 daily logs = 35 RR
        // 7-day food streak bonus = 15 RR
        // total = 50 RR
        assertEquals(50, rr);
    }

    @Test
    public void meal_week_withGoal_fullRR() {
        User user = createUser("meal_week_goal_user");
        user.setWeeklyCaloriesConsumedTarget(700); // 100 calories x 7 days
        user = userRepository.save(user);

        LocalDate monday = LocalDate.of(2026, 4, 6);

        for (int i = 0; i < 7; i++) {
            LocalDateTime mealDate = monday.plusDays(i).atTime(12, 0);

            mealService.addMeal(
                    user,
                    "Meal " + i,
                    "Lunch",
                    mealDate,
                    List.of(createFood(100.0))
            );
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 meal days with goal RR = " + rr);

        // 7 daily logs = 35 RR
        // weekly food goal = 50 RR
        // 7-day food streak bonus = 15 RR
        // total = 100 RR
        assertEquals(100, rr);
    }

    // =========================================================
    //                      WORKOUT TESTS
    // =========================================================

    @Test
    public void workout_oneDay_dailyRR() {
        User user = createUser("workout_one_log_user");
        user.setWeeklyCaloriesBurnedTarget(9999); // avoid weekly goal reward
        user = userRepository.save(user);

        LocalDateTime workoutDate = LocalDate.of(2026, 4, 6).atTime(18, 0); // Monday

        Workout workout = new Workout("Running", 30, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("1 workout RR = " + rr);

        // 1 daily workout log = 5 RR
        assertEquals(5, rr);
    }

    @Test
    public void workout_week_noGoal_bonusOnly() {
        User user = createUser("workout_week_no_goal_user");
        user.setWeeklyCaloriesBurnedTarget(9999999); // impossible target, no weekly goal reward
        user = userRepository.save(user);

        LocalDate monday = LocalDate.of(2026, 4, 6);

        for (int i = 0; i < 7; i++) {
            LocalDateTime workoutDate = monday.plusDays(i).atTime(18, 0);

            Workout workout = new Workout("Running " + i, 30, 100, workoutDate);
            workout.setUserId(user.getUid());
            workoutRepository.save(workout);
            rewardService.rewardForWorkoutLog(user, workoutDate);
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 workout days without goal RR = " + rr);

        // 7 daily logs = 35 RR
        // 7-day workout streak bonus = 15 RR
        // total = 50 RR
        assertEquals(50, rr);
    }

    @Test
    public void workout_week_withGoal_fullRR() {
        User user = createUser("workout_week_goal_user");
        user.setWeeklyCaloriesBurnedTarget(700); // 100 calories x 7 days
        user = userRepository.save(user);

        LocalDate monday = LocalDate.of(2026, 4, 6);

        for (int i = 0; i < 7; i++) {
            LocalDateTime workoutDate = monday.plusDays(i).atTime(18, 0);

            Workout workout = new Workout("Running " + i, 30, 100, workoutDate);
            workout.setUserId(user.getUid());
            workoutRepository.save(workout);
            rewardService.rewardForWorkoutLog(user, workoutDate);
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 workout days with goal RR = " + rr);

        // 7 daily logs = 35 RR
        // weekly workout goal = 50 RR
        // 7-day workout streak bonus = 15 RR
        // total = 100 RR
        assertEquals(100, rr);
    }

    // =========================================================
    //                 INACTIVITY PENALTY TESTS
    // =========================================================

    @Test
    public void penalty_noMeals_noWorkout_week() {
        User user = createUser("food_penalty_user");
        user = userRepository.save(user);

        // give starting RR so we can see the penalty clearly
        user.getRankProfile().setRr(100);
        userRepository.save(user);

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Food inactivity penalty RR = " + rr);

        // no meals last completed week = -25 RR
        // no workouts last completed week = -25 RR
        // total = -50 RR from 100 -> 50
        assertEquals(50, rr);
    }

    @Test
    public void penalty_noWorkout_week() {
        User user = createUser("workout_penalty_user");
        user = userRepository.save(user);

        user.getRankProfile().setRr(100);
        userRepository.save(user);

        LocalDate lastCompletedWeekStart = LocalDate.now()
                .with(java.time.DayOfWeek.MONDAY)
                .minusWeeks(1);

        // log 1 meal in the completed week so food penalty should NOT apply
        mealService.addMeal(
                user,
                "Lunch",
                "Lunch",
                lastCompletedWeekStart.plusDays(1).atTime(12, 0),
                List.of(createFood(100.0))
        );

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Only workout inactivity penalty RR = " + rr);

        // +5 daily meal log
        // -25 workout inactivity penalty
        // start 100 -> 105 -> 80
        assertEquals(80, rr);
    }

    @Test
    public void penalty_noMeals_week() {
        User user = createUser("food_only_penalty_user");
        user = userRepository.save(user);

        user.getRankProfile().setRr(100);
        userRepository.save(user);

        LocalDate lastCompletedWeekStart = LocalDate.now()
                .with(java.time.DayOfWeek.MONDAY)
                .minusWeeks(1);

        // log 1 workout in the completed week so workout penalty should NOT apply
        LocalDateTime workoutDate = lastCompletedWeekStart.plusDays(2).atTime(18, 0);
        Workout workout = new Workout("Running", 30, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Only food inactivity penalty RR = " + rr);

        // +5 daily workout log
        // -25 food inactivity penalty
        // start 100 -> 105 -> 80
        assertEquals(80, rr);
    }

    @Test
    public void penalty_noMeals() {
        User user = createUser("single_penalty_user");
        user = userRepository.save(user);

        user.getRankProfile().setRr(100);
        userRepository.save(user);

        rewardService.applyMissedWeekPenalties(user);
        rewardService.applyMissedWeekPenalties(user); // should not apply again

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Penalty applied once RR = " + rr);

        // first call:
        // -25 food inactivity
        // -25 workout inactivity
        // 100 -> 50
        // second call: no additional penalty
        assertEquals(50, rr);
    }

    // =========================================================
    //                          HELPERS
    // =========================================================

    private User createUser(String username) {
        return new User("Ali", "Test", username, "pass", "USER");
    }

    private Food createFood(double calories) {
        return new Food(
                "Food",
                1.0,
                calories,
                5.0,
                20.0,
                1.0,
                1.0,
                0.0,
                0.0,
                0.0,
                0.0
        );
    }
}
