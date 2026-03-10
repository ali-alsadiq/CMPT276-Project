package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cmpt276.project.porject.auth.User;
import com.cmpt276.project.porject.trackers.nutrition.Food;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a meal logged by a user.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - Each meal belongs to one user.
 * - Each meal contains a list of Food objects.
 * - The consumedDate represents the date and time the meal occurred.
 * - Nutrition totals are calculated by summing all foods in the meal.
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

    private String mealType;
    private LocalDateTime consumedDate;

    @OneToMany(mappedBy = "mealEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foods = new ArrayList<>();

    public MealEntry() {

    }

    public MealEntry(User user, String mealType, LocalDateTime consumedDate) {
        this.user = user;
        this.mealType = mealType;
        this.consumedDate = consumedDate;
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

    /**
     * Gets the type of meal.
     * 
     * @return Meal type such as Breakfast, Lunch, Dinner, or Snack
     */
    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    /**
     * Gets the date and time the meal was consumed.
     * 
     * @return Consumed date and time
     */
    public LocalDateTime getConsumedDate() {
        return consumedDate;
    }

    public void setConsumedDate(LocalDateTime consumedDate) {
        this.consumedDate = consumedDate;
    }

    /**
     * Gets the foods that belong to this meal.
     * 
     * 
     * @return List of food items in the meal
     */
    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}
