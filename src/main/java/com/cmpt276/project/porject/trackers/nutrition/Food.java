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
    private int calories;

    @Column(name = "protien")
    private int protien;

    @Column(name = "fats")
    private int fats;

    @Column(name = "carbs")
    private int carbs;

    //should let users add things they ate beforehand
    @Column(name = "consumed_date")
    private LocalDateTime consumedAt;  
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Food(String name, int calories, int protien, int fats, int carbs, LocalDateTime consumedAt) {
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

	public void setId(int id) {
		this.id = id;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public int getProtien() {
		return protien;
	}

	public void setProtien(int protien) {
		this.protien = protien;
	}

	public int getFats() {
		return fats;
	}

	public void setFats(int fats) {
		this.fats = fats;
	}

	public int getCarbs() {
		return carbs;
	}

	public void setCarbs(int carbs) {
		this.carbs = carbs;
	}

	public LocalDateTime consumedAt() {
		return consumedAt;
	}

	public void consumedAt(LocalDateTime when) {
		this.consumedAt = when;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
    
}
