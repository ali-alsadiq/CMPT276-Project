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


    
}
