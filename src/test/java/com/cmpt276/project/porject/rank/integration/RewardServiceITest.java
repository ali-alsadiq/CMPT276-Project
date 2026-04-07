package com.cmpt276.project.porject.rank.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import com.cmpt276.project.porject.rank.RewardService;
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
public class RewardServiceITest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealService mealService;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private RewardService rewardService;

    // Daily food log reward
    @Test
    public void meal_oneDay_dailyRR() {
        User user = createUser("meal_one_log_user");
        user.setWeeklyCaloriesConsumedTarget(9999);
        user = userRepository.save(user);

        LocalDateTime mealDate = LocalDate.of(2026, 4, 6).atTime(12, 0); // Monday

        mealService.addMeal(
                user,
                "Lunch",
                "Lunch",
                mealDate,
                List.of(createFood(100.0)));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("1 meal RR = " + rr);

        assertEquals(5, rr);
    }

    // Weekly food log reward
    @Test
    public void meal_week_noGoal_bonusOnly() {
        User user = createUser("meal_week_no_goal_user");
        user.setWeeklyCaloriesConsumedTarget(9999);
        user = userRepository.save(user);

        LocalDate monday = LocalDate.of(2026, 4, 6);

        for (int i = 0; i < 7; i++) {
            LocalDateTime mealDate = monday.plusDays(i).atTime(12, 0);

            mealService.addMeal(
                    user,
                    "Meal " + i,
                    "Lunch",
                    mealDate,
                    List.of(createFood(100.0)));
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 meal days without goal RR = " + rr);

        assertEquals(50, rr);
    }

    // Weekly food log reward with goal
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
                    List.of(createFood(100.0)));
        }

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("7 meal days with goal RR = " + rr);

        assertEquals(100, rr);
    }

    // Daily workout log reward
    @Test
    public void workout_oneDay_dailyRR() {
        User user = createUser("workout_one_log_user");
        user.setWeeklyCaloriesBurnedTarget(9999);
        user = userRepository.save(user);

        LocalDateTime workoutDate = LocalDate.of(2026, 4, 6).atTime(18, 0); // Monday

        Workout workout = new Workout("Running", 30, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("1 workout RR = " + rr);

        assertEquals(5, rr);
    }

    // Weekly workout log reward
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

        assertEquals(50, rr);
    }

    // Weekly workout log reward with goal
    @Test
    public void workout_week_withGoal_fullRR() {
        User user = createUser("workout_week_goal_user");
        user.setWeeklyCaloriesBurnedTarget(700);
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

        assertEquals(100, rr);
    }

    // Inactive meal log for a week
    @Test
    public void penalty_noMeals_noWorkout_week() {
        User user = createUser("food_penalty_user");
        user = userRepository.save(user);

        user.getRankProfile().setRr(100);
        userRepository.save(user);

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Food inactivity penalty RR = " + rr);

        assertEquals(50, rr);
    }

    // Inactive meal log for a week
    @Test
    public void penalty_noMeals_week() {
        User user = createUser("food_only_penalty_user");
        user = userRepository.save(user);

        user.getRankProfile().setRr(100);
        userRepository.save(user);

        LocalDate lastCompletedWeekStart = LocalDate.now()
                .with(java.time.DayOfWeek.MONDAY)
                .minusWeeks(1);

        LocalDateTime workoutDate = lastCompletedWeekStart.plusDays(2).atTime(18, 0);
        Workout workout = new Workout("Running", 30, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Only food inactivity penalty RR = " + rr);

        assertEquals(80, rr);
    }

    // test no penalty if already applied
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
        assertEquals(50, rr);
    }

    // logging meal on the same day twice only gives one daily RR reward
    @Test
    public void meal_sameDayTwice_onlyOneDailyRR() {
        User user = createUser("meal_dup_day_user");
        user.setWeeklyCaloriesConsumedTarget(9999);
        user = userRepository.save(user);

        LocalDateTime mealDate = LocalDate.of(2026, 4, 6).atTime(8, 0); // Monday

        mealService.addMeal(user, "Breakfast", "Breakfast", mealDate,
                List.of(createFood(100.0)));
        mealService.addMeal(user, "Lunch", "Lunch", mealDate.withHour(13),
                List.of(createFood(100.0)));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Same-day duplicate meal RR = " + rr);

        assertEquals(5, rr);
    }

    // logging workout on the same day twice only gives one daily RR reward
    @Test
    public void workout_sameDayTwice_onlyOneDailyRR() {
        User user = createUser("workout_dup_day_user");
        user.setWeeklyCaloriesBurnedTarget(9999999);
        user = userRepository.save(user);

        LocalDateTime workoutDate = LocalDate.of(2026, 4, 6).atTime(8, 0); // Monday

        Workout w1 = new Workout("Morning Run", 30, 100, workoutDate);
        w1.setUserId(user.getUid());
        workoutRepository.save(w1);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        // Second workout same day
        Workout w2 = new Workout("Evening Run", 30, 150, workoutDate.withHour(18));
        w2.setUserId(user.getUid());
        workoutRepository.save(w2);
        rewardService.rewardForWorkoutLog(user, workoutDate.withHour(18));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Same-day duplicate workout RR = " + rr);

        assertEquals(5, rr);
    }

    // tracking resets and a new daily RR is awarded next week
    @Test
    public void meal_acrossTwoWeeks_dailyRRAwardedEachWeek() {
        User user = createUser("meal_two_week_user");
        user.setWeeklyCaloriesConsumedTarget(9999);
        user = userRepository.save(user);

        // Week 1: Monday April 6
        LocalDateTime week1Day = LocalDate.of(2026, 4, 6).atTime(12, 0);
        mealService.addMeal(user, "Week1 Meal", "Lunch", week1Day,
                List.of(createFood(100.0)));

        // Week 2: Monday April 13
        LocalDateTime week2Day = LocalDate.of(2026, 4, 13).atTime(12, 0);
        mealService.addMeal(user, "Week2 Meal", "Lunch", week2Day,
                List.of(createFood(100.0)));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Two-week meal RR = " + rr);

        assertEquals(10, rr);
    }

    // tracking resets and a new daily RR is awarded next week
    @Test
    public void workout_acrossTwoWeeks_dailyRRAwardedEachWeek() {
        User user = createUser("workout_two_week_user");
        user.setWeeklyCaloriesBurnedTarget(9999999);
        user = userRepository.save(user);

        // Week 1: Monday April 6
        LocalDateTime week1Day = LocalDate.of(2026, 4, 6).atTime(18, 0);
        Workout w1 = new Workout("Week1 Run", 30, 100, week1Day);
        w1.setUserId(user.getUid());
        workoutRepository.save(w1);
        rewardService.rewardForWorkoutLog(user, week1Day);

        // Week 2: Monday April 13
        LocalDateTime week2Day = LocalDate.of(2026, 4, 13).atTime(18, 0);
        Workout w2 = new Workout("Week2 Run", 30, 100, week2Day);
        w2.setUserId(user.getUid());
        workoutRepository.save(w2);
        rewardService.rewardForWorkoutLog(user, week2Day);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Two-week workout RR = " + rr);

        assertEquals(10, rr);
    }

    // Partial goal: calories below target gives no weekly goal bonus
    @Test
    public void meal_partialGoal_noWeeklyGoalRR() {
        User user = createUser("meal_partial_goal_user");
        user.setWeeklyCaloriesConsumedTarget(1000);
        user = userRepository.save(user);

        // Log only 1 meal with 100 calories (far below 1000 target)
        LocalDateTime mealDate = LocalDate.of(2026, 4, 6).atTime(12, 0);
        mealService.addMeal(user, "Small Meal", "Lunch", mealDate,
                List.of(createFood(100.0)));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Partial food goal RR = " + rr);

        assertEquals(5, rr);
    }

    // calories below target gives no weekly goal bonus
    @Test
    public void workout_partialGoal_noWeeklyGoalRR() {
        User user = createUser("workout_partial_goal_user");
        user.setWeeklyCaloriesBurnedTarget(500);
        user = userRepository.save(user);

        // Log only 1 workout with 100 calories (below 500 target)
        LocalDateTime workoutDate = LocalDate.of(2026, 4, 6).atTime(18, 0);
        Workout workout = new Workout("Light Jog", 20, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Partial workout goal RR = " + rr);

        assertEquals(5, rr);
    }

    // meals weekly goal should be skipped entirely with 0 target
    @Test
    public void meal_zeroCaloriesTarget_noWeeklyGoalRR() {
        User user = createUser("meal_zero_target_user");
        user.setWeeklyCaloriesConsumedTarget(0);
        user = userRepository.save(user);

        LocalDateTime mealDate = LocalDate.of(2026, 4, 6).atTime(12, 0);
        mealService.addMeal(user, "Meal", "Lunch", mealDate,
                List.of(createFood(100.0)));

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Zero target food RR = " + rr);

        assertEquals(5, rr);
    }

    // workout weekly goal should be skipped entirely with 0 target
    @Test
    public void workout_zeroCaloriesTarget_noWeeklyGoalRR() {
        User user = createUser("workout_zero_target_user");
        user.setWeeklyCaloriesBurnedTarget(0);
        user = userRepository.save(user);

        LocalDateTime workoutDate = LocalDate.of(2026, 4, 6).atTime(18, 0);
        Workout workout = new Workout("Run", 30, 100, workoutDate);
        workout.setUserId(user.getUid());
        workoutRepository.save(workout);
        rewardService.rewardForWorkoutLog(user, workoutDate);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("Zero target workout RR = " + rr);

        assertEquals(5, rr);
    }

    // penalty cannot push RR below 0

    @Test
    public void penalty_rrAlreadyZero_staysAtZero() {
        User user = createUser("rr_floor_user");
        user = userRepository.save(user);

        rewardService.applyMissedWeekPenalties(user);

        User updatedUser = userRepository.findByUid(user.getUid());
        int rr = updatedUser.getRankProfile().getRr();

        System.out.println("RR floor at zero penalty RR = " + rr);

        assertEquals(0, rr);
    }

    @Test
    public void rewardForFoodLog_nullUser_doesNotThrow() {
        assertDoesNotThrow(() -> rewardService.rewardForFoodLog(null,
                LocalDate.of(2026, 4, 6).atTime(12, 0)));
    }

    @Test
    public void rewardForFoodLog_nullDate_doesNotThrow() {
        User user = createUser("food_null_date_user");
        user = userRepository.save(user);
        final User finalUser = user;
        assertDoesNotThrow(() -> rewardService.rewardForFoodLog(finalUser, null));
    }

    @Test
    public void rewardForWorkoutLog_nullUser_doesNotThrow() {
        assertDoesNotThrow(() -> rewardService.rewardForWorkoutLog(null,
                LocalDate.of(2026, 4, 6).atTime(18, 0)));
    }

    @Test
    public void rewardForWorkoutLog_nullDate_doesNotThrow() {
        User user = createUser("workout_null_date_user");
        user = userRepository.save(user);
        final User finalUser = user;
        assertDoesNotThrow(() -> rewardService.rewardForWorkoutLog(finalUser, null));
    }

    @Test
    public void applyMissedWeekPenalties_nullUser_doesNotThrow() {
        assertDoesNotThrow(() -> rewardService.applyMissedWeekPenalties(null));
    }

    // Helper
    private User createUser(String username) {
        return new User("User", "Test", username, "pass", "USER");
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
                0.0);
    }
}
