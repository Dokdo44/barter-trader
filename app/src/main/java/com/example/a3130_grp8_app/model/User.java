package com.example.a3130_grp8_app.model;

import java.io.Serializable;

/**
 * Business object for the app-specific user profile stored in Realtime Database.
 */
public class User implements Serializable {
    private String email;
    private String role;

    public User() {
        // Firebase Realtime Database requires a public empty constructor for deserialization.
    }

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
