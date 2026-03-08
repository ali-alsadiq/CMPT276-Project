package com.cmpt276.project.porject.trackers.workouts;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Object of a single user workout
 * 
 */
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int id;  
    
    @Column(nullable = false)
    private String workoutName;
    
    @Column(nullable = false)
    private int duration;
    
    @Column(name = "calories_burned", nullable = false)
    private int calsBurned;
    
    @Column(name = "user_id")  
    private int userId;
    
    //to allow adding previous workouts
    @Column(name = "workout_date")
    private LocalDateTime workoutDate;  
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Workout() {}  
    
    public Workout(String name, int duration, int calories, LocalDateTime workoutDate) {
        this.workoutName = name;
        this.duration = duration;
        this.calsBurned = calories;
        this.workoutDate = workoutDate;
        this.createdAt = LocalDateTime.now();
    }
    
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getWorkoutName() {
        return workoutName;
    }
    
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getCalsBurned() {
        return calsBurned;
    }
    
    public void setCalsBurned(int calsBurned) {
        this.calsBurned = calsBurned;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getWorkoutDate() {
        return workoutDate;
    }
    
    public void setWorkoutDate(LocalDateTime when) {
        this.workoutDate = when;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}


