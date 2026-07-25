package com.example.a3130_grp8_app;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;

import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Shared Firebase and Espresso helpers for repeatable instrumented auth tests.
 */
public final class TestAuthHelper {

    private static final String FIREBASE_URL = "https://group8-19144-default-rtdb.firebaseio.com/";

    private TestAuthHelper() {}

    /**
     * Builds a unique email address so repeated test runs do not reuse Firebase Auth accounts.
     *
     * @param prefix readable prefix that identifies the test purpose
     * @return unique test email address
     */
    public static String uniqueEmail(String prefix) {
        return String.format(Locale.US, "%s-%s@gmail.com", prefix, UUID.randomUUID());
    }

    /**
     * Creates a Firebase Auth user and matching Realtime Database profile for login tests.
     *
     * @param email test user email
     * @param password test user password
     * @param role app role to store in the profile
     * @throws Exception when Firebase setup fails
     */
    public static void createUserProfile(String email, String password, String role) throws Exception {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        FirebaseUser firebaseUser = Tasks.await(
                auth.createUserWithEmailAndPassword(email, password),
                15,
                TimeUnit.SECONDS
        ).getUser();

        if (firebaseUser == null) {
            throw new IllegalStateException("Firebase did not return a test user");
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference("users")
                .child(firebaseUser.getUid());

        Tasks.await(
                userRef.setValue(new User(email, role, userRef.getKey())),
                15,
                TimeUnit.SECONDS
        );

        auth.signOut();
    }

    /**
     * Clears any existing Firebase Auth session before a test starts.
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Waits until a view appears, avoiding fixed sleeps around Firebase-backed navigation.
     *
     * @param viewId Android view id to wait for
     * @throws InterruptedException when the wait is interrupted
     */
    public static void waitForDisplayedView(int viewId) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            try {
                Espresso.onView(withId(viewId)).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError error) {
                Thread.sleep(250);
            }
        }

        Espresso.onView(withId(viewId)).check(matches(isDisplayed()));
    }
}
