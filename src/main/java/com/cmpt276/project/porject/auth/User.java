package com.cmpt276.project.porject.auth; // Typo?

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

import jakarta.persistence.Column;

/**
 * Represents a user in the system.
 * 
 * FOR BACKEND IMPLEMENTATION:
 * - When creating entities that belong to users, use 'uid'
 * from this class as a foreign key.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    private String firstname;
    private String lastname;

    @Column(unique = true)
    private String username;
    private String password; // Stored as plain text for Iteration 1
    private String role; // USER or ADMIN

    private String sex;
    private LocalDate dateOfBirth;
    private double height;
    private double weight;
    private int caloriesDailyGoal;

    public User() {

    }

    public User(String firstname, String lastname, String username, String password, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // -- Getters and Setters --

    /**
     * User ID (Primary Key)
     * 
     * @return Unique database ID for this user.
     *         - Use this to link to other tables!
     */
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

   public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCaloriesDailyGoal() {
        return caloriesDailyGoal;
    }

    public void setCaloriesDailyGoal(int caloriesDailyGoal) {
        this.caloriesDailyGoal = caloriesDailyGoal;
    }
}
