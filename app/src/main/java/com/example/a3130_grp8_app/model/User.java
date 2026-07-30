package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Business object for the app-specific user profile stored in Realtime Database.
 */
public class User implements Serializable {
    private String id;
    private String email;
    private String role;
    private String notificationToken;
    private Map<String, Boolean> messageInbox;
    private Map<String, String> preferredProductIds;
    private Map<String, String> preferredCategoryNames;

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

    public void setMessageInbox(Map<String, Boolean> messageInbox) {
        this.messageInbox = messageInbox;
    }

    public Map<String, Boolean> getMessageInbox() {
        return messageInbox;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public List<String> getPreferredProductIds() {
        return preferredProductIds;
    }

    public void setPreferredProductIds(Map<String, String> preferredProductIds) {
        this.preferredProductIds = preferredProductIds;
    }

    public Map<String, String> getPreferredCategoryNames() {
        return preferredCategoryNames;
    }

    public void setPreferredCategoryNames(Map<String, String> preferredCategoryNames) {
        this.preferredCategoryNames = preferredCategoryNames;
    }
}
