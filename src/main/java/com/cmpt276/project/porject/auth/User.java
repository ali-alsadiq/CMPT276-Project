package com.cmpt276.project.porject.auth; // Typo?

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    private String username;
    private String password; // Stored as plain text for Iteration 1
    private String role; // USER or ADMIN

    public User() {

    }

    public User(String username, String password, String role) {
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
}
