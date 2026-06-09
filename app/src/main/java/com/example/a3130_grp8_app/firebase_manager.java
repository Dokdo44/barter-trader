package com.example.a3130_grp8_app;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class firebase_manager {
    // Database variables
    private DatabaseReference dbref;
    private static final String FIREBASE_URL = "https://grp8-app-default-rtdb.firebaseio.com/";

    // Function addUser: takes in inputs and adds them as a js
    public static void addUser(String email_id, String password, String role){
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("users");

        String user_id = user_ref.push().getKey();

        User newUser = new User(email_id, password, role);

        user_ref.child(user_id).setValue(newUser);
    }
}