package com.example.a3130_grp8_app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Centralizes Firebase Auth and Realtime Database calls so Activities do not hold business logic.
 */
public class FirebaseManager {

    // Singleton
    private static FirebaseManager instance;

    private static final String USERS_PATH = "users";
    private static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    private FirebaseAuth auth;
    private FirebaseDatabase db;

    protected FirebaseManager() {}

    public static FirebaseManager getInstance() {
        if(instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public static void install(FirebaseManager firebaseManager) {
        if(instance != null) {
            throw new IllegalStateException("FirebaseManager singleton already initialized");
        }
        instance = firebaseManager;
    }

    /**
     * Returns the currently signed-in Firebase Auth user.
     *
     * @return current Firebase user, or null when no user is signed in
     */
    public FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public FirebaseDatabase getDb() {
        if(db == null) {
            db = FirebaseDatabase.getInstance();
        }
        return db;
    }

    public FirebaseAuth getAuth() {
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public void setDb(FirebaseDatabase db) {
        this.db = db;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    /**
     * Creates an Auth account and stores the app profile under the new user's uid.
     *
     * @param email email address for Firebase Auth and the app profile
     * @param password password for Firebase Auth account creation
     * @param role selected app role to store in Realtime Database
     * @return task that completes after both Auth creation and profile storage finish
     */
    public Task<Void> createUser(String email, String password, String role) {
        return getAuth()
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

    /**
     * Signs in with Firebase Auth using email/password credentials.
     *
     * @param email email address entered on the login screen
     * @param password password entered on the login screen
     * @return Firebase Auth sign-in task
     */
    public Task<AuthResult> signIn(String email, String password) {
        return getAuth().signInWithEmailAndPassword(email, password);
    }

    /**
     * Loads the current user's app profile from Realtime Database.
     *
     * @return task that resolves to the current user's profile
     * @throws FirebaseConnectionError when no Auth user is signed in
     */
    public Task<User> getCurrentUserProfile() throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        // Dashboard data comes from /users/{uid}, keeping role separate from Auth credentials.
        return getUserReference(firebaseUser.getUid()).get()
                .continueWith(task -> task.getResult().getValue(User.class));
    }

    /**
     * Sends a Firebase Auth password reset email.
     *
     * @param email account email that should receive the reset message
     * @return Firebase Auth password reset task
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return getAuth().sendPasswordResetEmail(email);
    }

    /**
     * Clears the current Firebase Auth session.
     */
    public void signOut() {
        getAuth().signOut();
    }

    private DatabaseReference getUserReference(String uid) {
        return getDb().getReference(USERS_PATH).child(uid);
    }
    
    public static abstract class ProductChildEventListener implements ChildEventListener {
        @Override
        public final void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Product product = snapshot.getValue(Product.class);
            onGetProduct(product);
        }

        @Override
        public final void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public final void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public final void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public final void onCancelled(@NonNull DatabaseError error) {

        }

        public abstract void onGetProduct(Product product);
    }

    public void addProductChildEventListener
            (String orderBy, ProductChildEventListener productChildEventListener) {
        DatabaseReference ref = getDb().getReference("product");
        ref.orderByChild(orderBy)
                .addChildEventListener(productChildEventListener);
    }
}
