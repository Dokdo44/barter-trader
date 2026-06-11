package com.example.a3130_grp8_app;

public class User {
    // Profile fields only; passwords were removed because Firebase Auth stores credentials.
    public String email;
    public String role;

    public User() {
        // Required by Firebase Realtime Database.
    }

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail(){
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
