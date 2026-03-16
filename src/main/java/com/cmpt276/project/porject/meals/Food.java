package com.cmpt276.project.porject.meals;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Object of food items and their nutrition
 */
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Meal that this food belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "meal_entry_id", nullable = false)
    private Meal mealEntry;

    @Column(nullable = false)
    private String foodName;

    @Column(name = "calories")
    private double calories;

    @Column(name = "servSize")
    private double servSize;

    @Column(name = "protien")
    private double protien;

    @Column(name = "carbs")
    private double carbs;

    @Column(name = "fats")
    private double fats;

    @Column(name = "fiber")
    private double fiber;

    @Column(name = "sugar")
    private double sugar;

    @Column(name = "sodium")
    private double sodium;

    @Column(name = "potassium")
    private double potassium;

    @Column(name = "cholesterol")
    private double cholesterol;

    public Food() {
    }

    public Food(String foodName, double servSize, double calories, double protien,
            double carbs, double fats, double fiber, double sugar,
            double sodium, double potassium, double cholesterol) {
        this.foodName = foodName;
        this.servSize = servSize;
        this.calories = calories;
        this.protien = protien;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
        this.sugar = sugar;
        this.sodium = sodium;
        this.potassium = potassium;
        this.cholesterol = cholesterol;
    }

    public int getId() {
        return id;
    }

    public Meal getMealEntry() {
        return mealEntry;
    }

    public void setMealEntry(Meal mealEntry) {
        this.mealEntry = mealEntry;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getServSize() {
        return servSize;
    }

    public void setServSize(double servSize) {
        this.servSize = servSize;
    }

    public double getProtien() {
        return protien;
    }

    public void setProtien(double protien) {
        this.protien = protien;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }
}
