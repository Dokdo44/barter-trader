package com.example.a3130_grp8_app.model;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * Business object for the app-specific user profile stored in Realtime Database.
 */
public class User implements Serializable {
    private String id;
    private String email;
    private String role;

    /**
     * Creates an empty profile for Firebase Realtime Database deserialization.
     */
    public User() {
        // Firebase Realtime Database requires a public empty constructor for deserialization.
    }

    /**
     * Creates an app profile with the email and role stored outside Firebase Auth credentials.
     *
     * @param email profile email address
     * @param role selected app role
     */
    public User(String email, String role, String id) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    /**
     * Returns the profile email address.
     *
     * @return profile email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the app role for dashboard selection.
     *
     * @return selected app role
     */
    public String getRole() {
        return role;
    }

    /**
     * Updates the profile email address.
     *
     * @param email profile email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the app role for dashboard selection.
     *
     * @param role selected app role
     */
    public void setRole(String role) {
        this.role = role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
