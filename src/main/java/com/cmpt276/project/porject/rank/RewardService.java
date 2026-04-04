package com.cmpt276.project.porject.rank;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.auth.UserRepository;
import com.cmpt276.project.porject.meals.Food;
import com.cmpt276.project.porject.meals.Meal;
import com.cmpt276.project.porject.meals.MealRepository;
import com.cmpt276.project.porject.trackers.workouts.Workout;
import com.cmpt276.project.porject.trackers.workouts.WorkoutRepository;

@Service
public class RewardService {

    @Autowired
    private RankService rankService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private static final int DAILY_LOG_RR = 5;
    private static final int WEEKLY_GOAL_RR = 50;
    private static final int PERFECT_WEEK_BONUS_RR = 15;

    private static final int ALL_DAYS_LOGGED_MASK = 0b1111111;

    // =========================================================
    //                      FOOD REWARDS
    // =========================================================
    @Transactional
    public void rewardForFoodLog(User user, LocalDateTime consumedDateTime) {
        if (user == null || consumedDateTime == null) {
            return;
        }

        RankProfile rankProfile = user.getRankProfile();
        LocalDate loggedDate = consumedDateTime.toLocalDate();
        LocalDate weekStart = getWeekStart(loggedDate);

        resetFoodTrackingForNewWeek(rankProfile, weekStart);
        awardFoodDailyLog(user, rankProfile, loggedDate);
        awardFoodWeeklyGoal(user, rankProfile, weekStart);
        awardFoodPerfectWeekBonus(user, rankProfile);

        userRepository.save(user);
    }

    private void resetFoodTrackingForNewWeek(RankProfile rankProfile, LocalDate weekStart) {
        if (rankProfile.getFoodRewardWeekStart() == null
                || !rankProfile.getFoodRewardWeekStart().equals(weekStart)) {
            rankProfile.setFoodRewardWeekStart(weekStart);
            rankProfile.setFoodLoggedDaysMask(0);
            rankProfile.setWeeklyFoodGoalAwarded(false);
            rankProfile.setWeeklyFoodStreakBonusAwarded(false);
        }
    }

    private void awardFoodDailyLog(User user, RankProfile rankProfile, LocalDate loggedDate) {
        int dayBit = getDayBit(loggedDate);
        boolean alreadyRewarded = (rankProfile.getFoodLoggedDaysMask() & dayBit) != 0;

        if (!alreadyRewarded) {
            rankService.increaseRR(user, DAILY_LOG_RR);
            rankProfile.setFoodLoggedDaysMask(rankProfile.getFoodLoggedDaysMask() | dayBit);
        }
    }

    private void awardFoodWeeklyGoal(User user, RankProfile rankProfile, LocalDate weekStart) {
        if (rankProfile.isWeeklyFoodGoalAwarded()) {
            return;
        }

        double weeklyGoal = user.getWeeklyCaloriesConsumedTarget();
        if (weeklyGoal <= 0) {
            return;
        }

        double weeklyCalories = getFoodCaloriesForWeek(user.getUid(), weekStart);

        if (weeklyCalories >= weeklyGoal) {
            rankService.increaseRR(user, WEEKLY_GOAL_RR);
            rankProfile.setWeeklyFoodGoalAwarded(true);
        }
    }

    private void awardFoodPerfectWeekBonus(User user, RankProfile rankProfile) {
        if (rankProfile.isWeeklyFoodStreakBonusAwarded()) {
            return;
        }

        if (rankProfile.getFoodLoggedDaysMask() == ALL_DAYS_LOGGED_MASK) {
            rankService.increaseRR(user, PERFECT_WEEK_BONUS_RR);
            rankProfile.setWeeklyFoodStreakBonusAwarded(true);
        }
    }

    private double getFoodCaloriesForWeek(int uid, LocalDate weekStart) {
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(7).atStartOfDay();

        List<Meal> meals = mealRepository.findByUserUidAndConsumedDateBetween(uid, start, end);

        double totalCalories = 0.0;
        for (Meal meal : meals) {
            for (Food food : meal.getFoods()) {
                totalCalories += food.getCalories();
            }
        }

        return totalCalories;
    }

    // =========================================================
    //                      WORKOUT REWARDS
    // =========================================================
    @Transactional
    public void rewardForWorkoutLog(User user, LocalDateTime workoutDateTime) {
        if (user == null || workoutDateTime == null) {
            return;
        }

        RankProfile rankProfile = user.getRankProfile();
        LocalDate loggedDate = workoutDateTime.toLocalDate();
        LocalDate weekStart = getWeekStart(loggedDate);

        resetWorkoutTrackingForNewWeek(rankProfile, weekStart);
        awardWorkoutDailyLog(user, rankProfile, loggedDate);
        awardWorkoutWeeklyGoal(user, rankProfile, weekStart);
        awardWorkoutPerfectWeekBonus(user, rankProfile);

        userRepository.save(user);
    }

    private void resetWorkoutTrackingForNewWeek(RankProfile rankProfile, LocalDate weekStart) {
        if (rankProfile.getWorkoutRewardWeekStart() == null
                || !rankProfile.getWorkoutRewardWeekStart().equals(weekStart)) {
            rankProfile.setWorkoutRewardWeekStart(weekStart);
            rankProfile.setWorkoutLoggedDaysMask(0);
            rankProfile.setWeeklyWorkoutGoalAwarded(false);
            rankProfile.setWeeklyWorkoutStreakBonusAwarded(false);
        }
    }

    private void awardWorkoutDailyLog(User user, RankProfile rankProfile, LocalDate loggedDate) {
        int dayBit = getDayBit(loggedDate);
        boolean alreadyRewarded = (rankProfile.getWorkoutLoggedDaysMask() & dayBit) != 0;

        if (!alreadyRewarded) {
            rankService.increaseRR(user, DAILY_LOG_RR);
            rankProfile.setWorkoutLoggedDaysMask(rankProfile.getWorkoutLoggedDaysMask() | dayBit);
        }
    }

    private void awardWorkoutWeeklyGoal(User user, RankProfile rankProfile, LocalDate weekStart) {
        if (rankProfile.isWeeklyWorkoutGoalAwarded()) {
            return;
        }

        double weeklyGoal = user.getWeeklyCaloriesBurnedTarget();
        if (weeklyGoal <= 0) {
            return;
        }

        int weeklyCalories = getWorkoutCaloriesForWeek(user.getUid(), weekStart);

        if (weeklyCalories >= weeklyGoal) {
            rankService.increaseRR(user, WEEKLY_GOAL_RR);
            rankProfile.setWeeklyWorkoutGoalAwarded(true);
        }
    }

    private void awardWorkoutPerfectWeekBonus(User user, RankProfile rankProfile) {
        if (rankProfile.isWeeklyWorkoutStreakBonusAwarded()) {
            return;
        }

        if (rankProfile.getWorkoutLoggedDaysMask() == ALL_DAYS_LOGGED_MASK) {
            rankService.increaseRR(user, PERFECT_WEEK_BONUS_RR);
            rankProfile.setWeeklyWorkoutStreakBonusAwarded(true);
        }
    }

    private int getWorkoutCaloriesForWeek(int uid, LocalDate weekStart) {
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(7).atStartOfDay();

        List<Workout> workouts = workoutRepository.findByUserIdAndWorkoutDateBetween(uid, start, end);

        int totalCalories = 0;
        for (Workout workout : workouts) {
            totalCalories += workout.getCalsBurned();
        }

        return totalCalories;
    }

    // =========================================================
    //                     SHARED HELPERS
    // =========================================================

    private LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private int getDayBit(LocalDate date) {
        int dayIndex = date.getDayOfWeek().getValue() - 1; // Monday=0 ... Sunday=6
        return 1 << dayIndex;
    }
}
