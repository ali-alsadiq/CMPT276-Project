package com.cmpt276.project.porject.trackers.nutrition;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Object of food/meal to track nutrition of
 */
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id; 

    @Column(nullable = false)
    private String foodName;

    @Column(name = "user_id")  
    private int userId;

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

    //should let users add things they ate beforehand
    @Column(name = "consumed_date")
    private LocalDateTime consumedAt;  
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Food(String name, double calories, double servSize, double protien, double carbs, double fats, double fiber, double sugar, double sodium, double potassium, double cholesterol, LocalDateTime consumedAt) {
        this.foodName = name;
        this.calories = calories;
        this.fats = fats;
        this.carbs = carbs;
        this.consumedAt = consumedAt;
        this.createdAt = LocalDateTime.now();

    }

	public int getId() {
		return id;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public LocalDateTime getConsumedAt() {
		return consumedAt;
	}

	public void setConsumedAt(LocalDateTime consumedAt) {
		this.consumedAt = consumedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
    
}
