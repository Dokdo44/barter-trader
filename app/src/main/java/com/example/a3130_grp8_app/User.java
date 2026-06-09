package com.example.a3130_grp8_app;

public class User {
    public String email_id;
    public String password;
    public String role;

    public User() {
        //Placeholder
    }

    public User(String email_id, String password, String role) {
        this.email_id = email_id;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getEmail_id(){
        return email_id;
    }

    public String getPassword(){
        return password;
    }

    public String getRole(){
        return role;
    }

    // Setters
    public void setEmail_id(String email_id){
        this.email_id = email_id;
    }

    public void setPassword(String password){       // We might need these later on
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
