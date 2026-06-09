package com.example.a3130_grp8_app;

public class FirebaseConnectionError extends Exception {
    @Override
    public String getMessage() {
        return "Could not connect to firebase";
    }
}
