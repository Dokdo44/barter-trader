package com.example.a3130_grp8_app;

import android.provider.ContactsContract;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseManager {
    // Database variables
    private DatabaseReference dbref;
    private static final String FIREBASE_URL = "https://grp8-app-default-rtdb.firebaseio.com/";

    // Function addUser: Takes in inputs and adds them as a json string in the database
    public static void addUser(String email_id, String password, String role) throws FirebaseConnectionError {
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("users");

        String userId = user_ref.push().getKey();

        if(userId == null) {
            throw new FirebaseConnectionError();
        }

        User newUser = new User(email_id, password, role);

        user_ref.child(userId).setValue(newUser);
    }

    // Function getUser: compared the email_id and gives you a Task<User> object that business logic must attach addOnSuccessListener to inorder to get the value of
    public static Task<User> getUser(String email_id){
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference("users");

        Query query = user_ref.orderByChild("email_id").equalTo(email_id).limitToFirst(1);

        return query.get().continueWith(task -> {
            DataSnapshot result = task.getResult();

            for (DataSnapshot userSnapchat : result.getChildren()) {
                return userSnapchat.getValue(User.class);
            }
            return null;
        });
    }
}