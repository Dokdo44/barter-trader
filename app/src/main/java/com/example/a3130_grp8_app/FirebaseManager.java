package com.example.a3130_grp8_app;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    // Database variables
    private DatabaseReference dbref;
    private static final String FIREBASE_URL = "https://grp8-app-default-rtdb.firebaseio.com/";

    // Function addUser: takes in inputs and adds them as a js
    public static void addUser(String email_id, String password, String role) throws FirebaseConnectionError {
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("users");

        String userId = user_ref.push().getKey();

        if(userId == null) {
            throw new FirebaseConnectionError();
        }

        User newUser = new User(email_id, password, role);

        user_ref.child(userId).setValue(newUser);
    }
}