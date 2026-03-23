package com.cmpt276.project.porject.meals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cmpt276.project.porject.auth.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "meal")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "uid", nullable = false)
    private User user;
    
    private String mealName;
    private String mealType;
    private LocalDateTime consumedDate;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foods = new ArrayList<>();

    public Meal() {
    }

    public Meal(User user, String mealName, String mealType, LocalDateTime consumedDate, List<Food> foods) {
        if (foods == null || foods.isEmpty()) {
            throw new IllegalArgumentException("A meal must contain at least one food.");
        }

        this.user = user;
        this.mealName = mealName;
        this.mealType = mealType;
        this.consumedDate = consumedDate;
        setFoods(foods);
    }

    // -- Getters and Setters --

    /**
     * Meal ID (Primary Key)
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

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
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
        this.foods.clear();

        if (foods == null) {
            return;
        }

        for (Food food : foods) {
            addFood(food);
        }
    }

    public void addFood(Food food) {
        if (food == null) {
            return;
        }

        this.foods.add(food);
        food.setMeal(this);
    }

    // CHANGE: calculate meal calories from all foods
    @Transient
    public double getCalories() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getCalories();
        }
        return total;
    }

    // CHANGE: calculate meal protein from all foods
    @Transient
    public double getProtein() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getProtien();
        }
        return total;
    }

    // CHANGE: calculate meal carbs from all foods
    @Transient
    public double getCarbs() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getCarbs();
        }
        return total;
    }

    // CHANGE: calculate meal fats from all foods
    @Transient
    public double getFats() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getFats();
        }
        return total;
    }

    // CHANGE: optional extra totals if needed later
    @Transient
    public double getFiber() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getFiber();
        }
        return total;
    }

    @Transient
    public double getSugar() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getSugar();
        }
        return total;
    }

    @Transient
    public double getSodium() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getSodium();
        }
        return total;
    }

    @Transient
    public double getPotassium() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getPotassium();
        }
        return total;
    }

    @Transient
    public double getCholesterol() {
        double total = 0.0;
        for (Food food : foods) {
            total += food.getCholesterol();
        }
        return total;
    }
}
