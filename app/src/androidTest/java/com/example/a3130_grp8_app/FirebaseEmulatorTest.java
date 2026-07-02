package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.a3130_grp8_app.util.FirebaseEmulator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class FirebaseEmulatorTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getContext();

    @Rule
    public ActivityScenarioRule<SignupActivity> activityTestRule =
            new ActivityScenarioRule<>(SignupActivity.class);

    public static class User {
        public String name;
        public String email;
        public int age;

        public User(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public User() {
        }
    }

    @Test
    public void dataUploadedToEmulatorSuccessfullyTest() throws Exception {
        FirebaseDatabase db = FirebaseEmulator.getDatabase();
        FirebaseEmulator.sendBulkDataToDatabase("FirebaseEmulatorTest/database_test_data.json");

        DatabaseReference ref = db.getReference("users");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DataSnapshot> snapshotRef = new AtomicReference<>();
        AtomicReference<DatabaseError> errorRef = new AtomicReference<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotRef.set(snapshot);
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorRef.set(error);
                latch.countDown();
            }
        });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue("Listener timed out", completed);
        assertNull("Listener was cancelled: " + (errorRef.get() != null ? errorRef.get().getMessage() : ""),
                errorRef.get());

        DataSnapshot snapshot = snapshotRef.get();
        assertNotNull("Snapshot was null", snapshot);

        boolean foundAny = false;
        for (DataSnapshot child : snapshot.getChildren()) {
            User user = child.getValue(User.class);
            assertNotNull(user);
            assertTrue(user.name.equals("Bob") || user.name.equals("Alice"));
            foundAny = true;
        }
        assertTrue("No children found in snapshot", foundAny);
    }



    @Test
    public void usersUploadedToEmulatorSuccessfullyTest() throws Exception {
        FirebaseAuth auth = FirebaseEmulator.getAuth();
        FirebaseEmulator.sendBulkDataToAuth("FirebaseEmulatorTest/auth_test_data.json");

        CountDownLatch latch = new CountDownLatch(1);

        auth.signInWithEmailAndPassword("bob@example.com", "password123")
                .addOnSuccessListener(authResult -> {
                    latch.countDown();
                }).addOnFailureListener(exception -> {
                    try {
                        throw exception;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed);
        assertNotNull(auth.getCurrentUser());
        assertEquals("Bob", auth.getCurrentUser().getDisplayName());
    }

    @Test
    public void authResetsSuccessfullyTest() throws Exception {
        FirebaseAuth auth = FirebaseEmulator.getAuth();
        FirebaseEmulator.sendBulkDataToAuth("FirebaseEmulatorTest/auth_test_data.json");

        CountDownLatch signInLatch = new CountDownLatch(1);

        auth.signInWithEmailAndPassword("bob@example.com", "password123")
                .addOnSuccessListener(authResult -> {
                    signInLatch.countDown();
                }).addOnFailureListener(exception -> {
                    try {
                        throw exception;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        boolean completed = signInLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed);
        assertNotNull(auth.getCurrentUser());
        assertEquals("Bob", auth.getCurrentUser().getDisplayName());

        FirebaseEmulator.resetAuth();

        CountDownLatch failSignInLatch = new CountDownLatch(1);
        AtomicReference<Boolean> failedRef = new AtomicReference<>();

        auth.signInWithEmailAndPassword("bob@example.com", "password123")
                .addOnSuccessListener(authResult -> {
                    failedRef.set(false);
                    failSignInLatch.countDown();
                }).addOnFailureListener(exception -> {
                    failedRef.set(true);
                    failSignInLatch.countDown();
                });

        completed = failSignInLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed);
        assertEquals(true, failedRef.get());
    }

    @Test
    public void dbResetsSuccessfullyTest() throws Exception {
        FirebaseDatabase db = FirebaseEmulator.getDatabase();
        FirebaseEmulator.sendBulkDataToDatabase("FirebaseEmulatorTest/database_test_data.json");

        DatabaseReference ref = db.getReference("users");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DataSnapshot> snapshotRef = new AtomicReference<>();
        AtomicReference<DatabaseError> errorRef = new AtomicReference<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotRef.set(snapshot);
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorRef.set(error);
                latch.countDown();
            }
        });

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue("Listener timed out", completed);
        assertNull("Listener was cancelled: " + (errorRef.get() != null ? errorRef.get().getMessage() : ""),
                errorRef.get());

        DataSnapshot snapshot = snapshotRef.get();
        assertNotNull("Snapshot was null", snapshot);

        boolean foundAny = false;
        for (DataSnapshot child : snapshot.getChildren()) {
            User user = child.getValue(User.class);
            assertNotNull(user);
            assertTrue(user.name.equals("Bob") || user.name.equals("Alice"));
            foundAny = true;
        }
        assertTrue("No children found in snapshot", foundAny);

        FirebaseEmulator.resetDatabase();

        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicReference<DataSnapshot> snapshotRef2 = new AtomicReference<>();
        AtomicReference<DatabaseError> errorRef2 = new AtomicReference<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotRef2.set(snapshot);
                latch2.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorRef2.set(error);
                latch2.countDown();
            }
        });

        completed = latch2.await(10, TimeUnit.SECONDS);
        assertTrue("Listener timed out", completed);
        assertNull("Listener was cancelled: " + (errorRef2.get() != null ? errorRef2.get().getMessage() : ""),
                errorRef2.get());

        DataSnapshot snapshot2 = snapshotRef2.get();
        assertNotNull("Snapshot was null", snapshot2);

        foundAny = false;
        for (DataSnapshot child : snapshot2.getChildren()) {
            User user = child.getValue(User.class);
            assertNotNull(user);
            assertTrue(user.name.equals("Bob") || user.name.equals("Alice"));
            foundAny = true;
        }
        assertFalse(foundAny);
    }
}
