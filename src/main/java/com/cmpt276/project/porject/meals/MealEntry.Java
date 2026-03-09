package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;

import com.cmpt276.project.porject.auth.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a meal entry logged by a user.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - Each meal belongs to one user.
 * - Calories can be aggregated by day, week, or month
 * using the eatenAt field.
 */
@Entity
@Table(name = "meal_entries")
public class MealEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    private String foodName;
    private String mealType;
    private int calories;
    private LocalDateTime eatenAt;

    public MealEntry() {

    }

    public MealEntry(User user, String foodName, String mealType, int calories, LocalDateTime eatenAt) {
        this.user = user;
        this.foodName = foodName;
        this.mealType = mealType;
        this.calories = calories;
        this.eatenAt = eatenAt;
    }

    // -- Getters and Setters --

    /**
     * Meal Entry ID (Primary Key)
     * 
     * @return Unique database ID for this meal entry.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * User who logged this meal.
     * 
     * @return User who owns this meal entry.
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public LocalDateTime getEatenAt() {
        return eatenAt;
    }

    public void setEatenAt(LocalDateTime eatenAt) {
        this.eatenAt = eatenAt;
    }
}
