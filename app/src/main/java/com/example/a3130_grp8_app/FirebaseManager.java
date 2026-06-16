package com.example.a3130_grp8_app;

import android.provider.ContactsContract;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseManager {

    private static final String USERS_PATH = "users";
    // The checked-in google-services.json does not provide a database URL, so database
    // calls use the project URL explicitly instead of relying on default initialization.
    private static final String FIREBASE_URL = "https://grp8-app-default-rtdb.firebaseio.com/";

    public static Task<Void> createUser(String email, String password, String role) {
        // Firebase Auth owns credentials now; the Realtime Database stores profile data only.
        return FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    AuthResult authResult = task.getResult();
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        throw new FirebaseConnectionError();
                    }

                    // Store profile data by Auth UID so duplicate emails are not created with push IDs.
                    return getUserReference(firebaseUser.getUid()).setValue(new User(email, role));
                });
    }

    private static DatabaseReference getUserReference(String uid) {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(USERS_PATH).child(uid);
    }
}
