package com.cmpt276.project.porject.rank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

/**
 * Represents a user's ranking profile in the system.
 * 
 * Class is not explicitly accessed by http, it is only used by other classes
 * through RankService, thus it does not need a controller.
 */
@Entity
@Table(name = "rank_profiles")
public class RankProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Automatically set to 0 if user does not have a rank profile
    @Column(name = "rr", nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    private int rr = 0;

    @Column(name = "food_reward_week_start")
    private LocalDate foodRewardWeekStart;

    @Column(name = "food_logged_days_mask", nullable = false)
    private int foodLoggedDaysMask = 0;

    @Column(name = "weekly_food_goal_awarded", nullable = false)
    private boolean weeklyFoodGoalAwarded = false;

    @Column(name = "weekly_food_streak_bonus_awarded", nullable = false)
    private boolean weeklyFoodStreakBonusAwarded = false;

    @Column(name = "workout_reward_week_start")
    private LocalDate workoutRewardWeekStart;

    @Column(name = "workout_logged_days_mask", nullable = false)
    private int workoutLoggedDaysMask = 0;

    @Column(name = "weekly_workout_goal_awarded", nullable = false)
    private boolean weeklyWorkoutGoalAwarded = false;

    @Column(name = "weekly_workout_streak_bonus_awarded", nullable = false)
    private boolean weeklyWorkoutStreakBonusAwarded = false;

    @Column(name = "last_food_penalty_week_start")
    private LocalDate lastFoodPenaltyWeekStart;

    @Column(name = "last_workout_penalty_week_start")
    private LocalDate lastWorkoutPenaltyWeekStart;

    // Transient field, not stored in the database
    @Transient
    private String rankImageName;

    public RankProfile() {
        this.rr = 0;
    }

    // -- Getters and Setters --
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRr() {
        return rr;
    }

    public void setRr(int rr) {
        this.rr = rr;
    }

    public LocalDate getFoodRewardWeekStart() {
        return foodRewardWeekStart;
    }

    public void setFoodRewardWeekStart(LocalDate foodRewardWeekStart) {
        this.foodRewardWeekStart = foodRewardWeekStart;
    }

    public int getFoodLoggedDaysMask() {
        return foodLoggedDaysMask;
    }

    public void setFoodLoggedDaysMask(int foodLoggedDaysMask) {
        this.foodLoggedDaysMask = foodLoggedDaysMask;
    }

    public boolean isWeeklyFoodGoalAwarded() {
        return weeklyFoodGoalAwarded;
    }

    public void setWeeklyFoodGoalAwarded(boolean weeklyFoodGoalAwarded) {
        this.weeklyFoodGoalAwarded = weeklyFoodGoalAwarded;
    }

    public boolean isWeeklyFoodStreakBonusAwarded() {
        return weeklyFoodStreakBonusAwarded;
    }

    public void setWeeklyFoodStreakBonusAwarded(boolean weeklyFoodStreakBonusAwarded) {
        this.weeklyFoodStreakBonusAwarded = weeklyFoodStreakBonusAwarded;
    }

    public LocalDate getWorkoutRewardWeekStart() {
        return workoutRewardWeekStart;
    }

    public void setWorkoutRewardWeekStart(LocalDate workoutRewardWeekStart) {
        this.workoutRewardWeekStart = workoutRewardWeekStart;
    }

    public int getWorkoutLoggedDaysMask() {
        return workoutLoggedDaysMask;
    }

    public void setWorkoutLoggedDaysMask(int workoutLoggedDaysMask) {
        this.workoutLoggedDaysMask = workoutLoggedDaysMask;
    }

    public boolean isWeeklyWorkoutGoalAwarded() {
        return weeklyWorkoutGoalAwarded;
    }

    public void setWeeklyWorkoutGoalAwarded(boolean weeklyWorkoutGoalAwarded) {
        this.weeklyWorkoutGoalAwarded = weeklyWorkoutGoalAwarded;
    }

    public boolean isWeeklyWorkoutStreakBonusAwarded() {
        return weeklyWorkoutStreakBonusAwarded;
    }

    public void setWeeklyWorkoutStreakBonusAwarded(boolean weeklyWorkoutStreakBonusAwarded) {
        this.weeklyWorkoutStreakBonusAwarded = weeklyWorkoutStreakBonusAwarded;
    }

     public LocalDate getLastFoodPenaltyWeekStart() {
        return lastFoodPenaltyWeekStart;
    }

    public void setLastFoodPenaltyWeekStart(LocalDate lastFoodPenaltyWeekStart) {
        this.lastFoodPenaltyWeekStart = lastFoodPenaltyWeekStart;
    }

    public LocalDate getLastWorkoutPenaltyWeekStart() {
        return lastWorkoutPenaltyWeekStart;
    }

    public void setLastWorkoutPenaltyWeekStart(LocalDate lastWorkoutPenaltyWeekStart) {
        this.lastWorkoutPenaltyWeekStart = lastWorkoutPenaltyWeekStart;
    }

    public String getRankImageName() {
        return rankImageName;
    }

    public void setRankImageName(String rankImageName) {
        this.rankImageName = rankImageName;
    }
}
