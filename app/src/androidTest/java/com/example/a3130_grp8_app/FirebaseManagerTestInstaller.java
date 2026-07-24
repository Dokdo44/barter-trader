package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.data.FirebaseManager;

import java.lang.reflect.Field;

/**
 * Installs isolated FirebaseManager doubles for Android tests that share the application's
 * singleton-backed Firebase API.
 */
public final class FirebaseManagerTestInstaller {

    private FirebaseManagerTestInstaller() {
    }

    /**
     * Replaces any prior singleton so this test class can use its own FirebaseManager double.
     *
     * @param firebaseManager mock or fake manager used by the current test class
     */
    public static void install(FirebaseManager firebaseManager) {
        reset();
        FirebaseManager.install(firebaseManager);
    }

    /**
     * Clears the singleton after a test class so it does not affect unrelated Android tests.
     */
    public static void reset() {
        try {
            Field instanceField = FirebaseManager.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not reset FirebaseManager for test isolation", exception);
        }
    }
}
