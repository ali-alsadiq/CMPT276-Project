package com.cmpt276.project.porject.auth; // Typo?

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import com.cmpt276.project.porject.rank.RankProfile;

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
    
    // Cals burned targets
    @Column(name = "targets")
    //check user has set targets at all
    private boolean userSetTargets;
    private double weeklyCaloriesBurnedTarget;

    //Nutrition consumtion targets
    private double weeklyCaloriesConsumedTarget;
    private double weeklyProtienTarget;
    private double weeklyCarbsTarget;
    private double weeklyFatsTarget;
    private double weeklyFibreTarget;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_profile_id", referencedColumnName = "id")
    private RankProfile rankProfile;

    public User() {
        this.rankProfile = new RankProfile();
    }

    public User(String firstname, String lastname, String username, String password, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.role = role;
        this.rankProfile = new RankProfile(); // Give a rank profile by default to new users
        this.userSetTargets = false;
    }

    // -- Getters and Setters --
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

    public boolean checkUserSetTargets() {
        return userSetTargets;
    }

    public void setUserSetTargets(boolean val) {
        this.userSetTargets = val;
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

    public boolean isAdmin() {
        return this.role.equals("ADMIN");
    }

    public RankProfile getRankProfile() {
        if (this.rankProfile == null) {
            this.rankProfile = new RankProfile();
        }
        return rankProfile;
    }

    public void setRankProfile(RankProfile rankProfile) {
        this.rankProfile = rankProfile;
    }

    public double getWeeklyCaloriesBurnedTarget() {
        return weeklyCaloriesBurnedTarget;
    }

    public void setWeeklyCaloriesBurnedTarget(double weeklyCaloriesTarget) {
        this.weeklyCaloriesBurnedTarget = weeklyCaloriesTarget;
    }

    public double getWeeklyCaloriesConsumedTarget() {
        return weeklyCaloriesConsumedTarget;
    }

    public void setWeeklyCaloriesConsumedTarget(double weeklyCaloriesTarget) {
        this.weeklyCaloriesConsumedTarget = weeklyCaloriesTarget;
    }

    public double getWeeklyProtienTarget() {
        return weeklyProtienTarget;
    }

    public void setWeeklyProtienTarget(double weeklyProtienTarget) {
        this.weeklyProtienTarget = weeklyProtienTarget;
    }

    public double getWeeklyCarbsTarget() {
        return weeklyCarbsTarget;
    }

    public void setWeeklyCarbsTarget(double weeklyCarbsTarget) {
        this.weeklyCarbsTarget = weeklyCarbsTarget;
    }

    public double getWeeklyFatsTarget() {
        return weeklyFatsTarget;
    }

    public void setWeeklyFatsTarget(double weeklyFatsTarget) {
        this.weeklyFatsTarget = weeklyFatsTarget;
    }

    public double getWeeklyFibreTarget() {
        return weeklyFibreTarget;
    }

    public void setWeeklyFibreTarget(double weeklyFibreTarget) {
        this.weeklyFibreTarget = weeklyFibreTarget;
    }
 
}
