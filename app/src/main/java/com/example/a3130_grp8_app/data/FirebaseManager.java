package com.example.a3130_grp8_app.data;

import com.example.a3130_grp8_app.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Centralizes Firebase Auth and Realtime Database calls so Activities do not hold business logic.
 */
public class FirebaseManager {

    private static final String USERS_PATH = "users";
    private static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Task<Void> createUser(String email, String password, String role) {
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

                    // Store app profile data under the Auth uid after account creation succeeds.
                    return getUserReference(firebaseUser.getUid()).setValue(new User(email, role));
                });
    }

    public static Task<AuthResult> signIn(String email, String password) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
    }

    public static Task<User> getCurrentUserProfile() throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        // Dashboard data comes from /users/{uid}, keeping role separate from Auth credentials.
        return getUserReference(firebaseUser.getUid()).get()
                .continueWith(task -> task.getResult().getValue(User.class));
    }

    public static Task<Void> sendPasswordResetEmail(String email) {
        return FirebaseAuth.getInstance().sendPasswordResetEmail(email);
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private static DatabaseReference getUserReference(String uid) {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(USERS_PATH).child(uid);
    }
}
