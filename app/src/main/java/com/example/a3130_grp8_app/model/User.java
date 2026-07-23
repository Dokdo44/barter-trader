package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Business object for the app-specific user profile stored in Realtime Database.
 */
public class User implements Serializable {
    private String email;
    private String role;

    private ArrayList<String> preferredCategories;
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
    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }
    public User(String email, String role, ArrayList<String> preferredCategories) {
        this.email = email;
        this.role = role;
        this.preferredCategories = preferredCategories;
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
    public ArrayList<String> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(ArrayList<String> preferredCategories)
    {
        this.preferredCategories = preferredCategories;
    }
}

