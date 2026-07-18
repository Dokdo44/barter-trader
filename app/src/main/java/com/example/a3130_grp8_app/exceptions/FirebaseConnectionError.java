package com.example.a3130_grp8_app.exceptions;

public class FirebaseConnectionError extends Exception {
    /**
     * Returns the message shown when Firebase cannot provide the expected user connection.
     *
     * @return user-facing Firebase connection error message
     */
    @Override
    public String getMessage() {
        return "Could not connect to firebase";
    }
}
