package com.example.a3130_grp8_app.data;

import com.example.a3130_grp8_app.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.ProductListing;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
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
    private static final String PRODUCT_PATH = "product";
    private static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    private static FirebaseAuth auth;
    private static FirebaseDatabase db;

    /**
     * Returns the currently signed-in Firebase Auth user.
     *
     * @return current Firebase user, or null when no user is signed in
     */
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static FirebaseDatabase getDb() {
        if(db == null) {
            db = FirebaseDatabase.getInstance();
        }
        return db;
    }

    public static FirebaseAuth getAuth() {
        if(auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static void setDb(FirebaseDatabase db) {
        FirebaseManager.db = db;
    }

    public static void setAuth(FirebaseAuth auth) {
        FirebaseManager.auth = auth;
    }

    /**
     * Creates an Auth account and stores the app profile under the new user's uid.
     *
     * @param email email address for Firebase Auth and the app profile
     * @param password password for Firebase Auth account creation
     * @param role selected app role to store in Realtime Database
     * @return task that completes after both Auth creation and profile storage finish
     */
    public static Task<Void> createUser(String email, String password, String role) {
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
    public static Task<AuthResult> signIn(String email, String password) {
        return getAuth().signInWithEmailAndPassword(email, password);
    }

    /**
     * Loads the current user's app profile from Realtime Database.
     *
     * @return task that resolves to the current user's profile
     * @throws FirebaseConnectionError when no Auth user is signed in
     */
    public static Task<User> getCurrentUserProfile() throws FirebaseConnectionError {
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
    public static Task<Void> sendPasswordResetEmail(String email) {
        return getAuth().sendPasswordResetEmail(email);
    }

    /**
     * Saves a provider listing, including its optional Base64 photo, in Realtime Database.
     *
     * @param listing listing form data without the final Firebase id and provider uid
     * @return task that completes after the database save finishes
     * @throws FirebaseConnectionError when no Auth user is signed in
     */
    public static Task<Void> createProductListing(ProductListing listing) throws FirebaseConnectionError {
        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            throw new FirebaseConnectionError();
        }

        DatabaseReference listingReference = getListingsReference().push();
        String listingId = listingReference.getKey();
        if (listingId == null) {
            throw new FirebaseConnectionError();
        }

        listing.id = listingId;
        listing.providerUid = firebaseUser.getUid();

        return listingReference.setValue(listing);
    }

    /**
     * Clears the current Firebase Auth session.
     */
    public static void signOut() {
        getAuth().signOut();
    }

    private static DatabaseReference getUserReference(String uid) {
        return getDb().getReference(USERS_PATH).child(uid);
    }

    private static DatabaseReference getListingsReference() {
        return getDb().getReference(PRODUCT_PATH);
    }
}
