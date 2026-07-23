package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Request;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Creates isolated Firebase Emulator data for US12 exchange-offer acceptance tests.
 */
final class ExchangeOfferEmulatorFixture {

    private static final String PASSWORD = "TestPassword1!";
    private static final long TIMEOUT_SECONDS = 15;
    private static boolean emulatorsConfigured;

    private final String suffix = UUID.randomUUID().toString();
    private final String productId = "us12-product-" + suffix;
    private TestUser provider;
    private TestUser receiver;

    /**
     * Connects the shared Firebase SDK instances to the local Auth and Database emulators.
     */
    static synchronized void connectToEmulators() {
        if (emulatorsConfigured) {
            return;
        }

        FirebaseManager manager = FirebaseManager.getInstance();
        manager.getAuth().useEmulator("10.0.2.2", BuildConfig.EMULATOR_AUTH_PORT);
        manager.getDb().useEmulator("10.0.2.2", BuildConfig.EMULATOR_DATABASE_PORT);
        emulatorsConfigured = true;
    }

    /**
     * Creates the provider, receiver, and available product used by one acceptance test.
     *
     * @throws Exception when emulator setup cannot complete
     */
    void setUp() throws Exception {
        provider = createUser("provider", "Provider");
        receiver = createUser("receiver", "Receiver");

        Product product = new Product(
                "US12 Test Bike",
                "A product used only by exchange-offer tests.",
                "Halifax, NS",
                10.0f,
                Collections.singletonList("Sports"),
                true,
                "",
                provider.uid,
                "2026-07-23",
                "2026-07-30",
                List.of("Skateboard", "Desk lamp")
        );
        product.setId(productId);

        await(database().getReference("product").child(productId).setValue(product));
    }

    /**
     * Signs in the test receiver so the request form can submit a proposal.
     *
     * @throws Exception when emulator authentication fails
     */
    void signInReceiver() throws Exception {
        signIn(receiver);
    }

    /**
     * Signs in the test provider before the provider request screen is opened.
     *
     * @throws Exception when emulator authentication fails
     */
    void signInProvider() throws Exception {
        signIn(provider);
    }

    /**
     * Saves a pending proposal through the production Firebase manager.
     *
     * @param offeredItems the items selected by the receiver
     * @return the Firebase key for the stored request
     * @throws Exception when the proposal cannot be saved
     */
    String submitProposal(List<String> offeredItems) throws Exception {
        signInReceiver();
        await(FirebaseManager.getInstance().submitRequest(
                productId,
                "Receiver One",
                "Dartmouth, NS",
                offeredItems
        ));
        return getOnlyRequest().getRequestId();
    }

    /**
     * Returns the only request saved for this fixture's product.
     *
     * @return the stored request with its Firebase key attached
     * @throws Exception when the request cannot be read
     */
    Request getOnlyRequest() throws Exception {
        DataSnapshot snapshot = await(database().getReference("requests")
                .orderByChild("productId")
                .equalTo(productId)
                .get());

        List<Request> requests = new ArrayList<>();
        for (DataSnapshot child : snapshot.getChildren()) {
            Request request = child.getValue(Request.class);
            if (request != null) {
                request.setRequestId(child.getKey());
                requests.add(request);
            }
        }

        if (requests.size() != 1) {
            throw new AssertionError("Expected one request for " + productId + " but found " + requests.size());
        }
        return requests.get(0);
    }

    /**
     * Returns the number of proposals currently saved for this fixture's product.
     *
     * @return the proposal count
     * @throws Exception when the request collection cannot be read
     */
    int getRequestCount() throws Exception {
        DataSnapshot snapshot = await(database().getReference("requests")
                .orderByChild("productId")
                .equalTo(productId)
                .get());
        int count = 0;
        for (DataSnapshot ignored : snapshot.getChildren()) {
            count++;
        }
        return count;
    }

    /**
     * Waits for a provider decision to reach the local database.
     *
     * @param requestId the request to inspect
     * @param expectedStatus the expected stored status
     * @throws Exception when the status is not updated before the timeout
     */
    void waitForRequestStatus(String requestId, String expectedStatus) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
        while (System.nanoTime() < deadline) {
            String status = await(database().getReference("requests")
                    .child(requestId)
                    .child("status")
                    .get())
                    .getValue(String.class);
            if (expectedStatus.equals(status)) {
                return;
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Request " + requestId + " did not reach status " + expectedStatus);
    }

    /**
     * Removes only the Auth and Realtime Database records created by this fixture.
     *
     * @throws Exception when emulator cleanup fails
     */
    void tearDown() throws Exception {
        DataSnapshot requestSnapshot = await(database().getReference("requests")
                .orderByChild("productId")
                .equalTo(productId)
                .get());
        List<com.google.android.gms.tasks.Task<Void>> deletionTasks = new ArrayList<>();
        for (DataSnapshot request : requestSnapshot.getChildren()) {
            deletionTasks.add(request.getRef().removeValue());
        }
        await(Tasks.whenAll(deletionTasks));
        await(database().getReference("product").child(productId).removeValue());

        deleteUserProfile(receiver);
        deleteUserProfile(provider);
        FirebaseManager.getInstance().signOut();
    }

    /**
     * Returns the product ID to pass to the request Activities.
     *
     * @return the fixture product ID
     */
    String getProductId() {
        return productId;
    }

    /**
     * Returns the receiver used by this fixture.
     *
     * @return the test receiver
     */
    TestUser getReceiver() {
        return receiver;
    }

    /**
     * Returns the receiver UID expected on a persisted proposal.
     *
     * @return the receiver UID
     */
    String getReceiverUid() {
        return receiver.uid;
    }

    /**
     * Waits for a Firebase-populated view to become visible without a fixed delay.
     *
     * @param viewId the expected view ID
     * @throws InterruptedException when the wait is interrupted
     */
    static void waitForDisplayedView(int viewId) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
        while (System.nanoTime() < deadline) {
            try {
                Espresso.onView(withId(viewId)).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError ignored) {
                Thread.sleep(100);
            }
        }
        Espresso.onView(withId(viewId)).check(matches(isDisplayed()));
    }

    /**
     * Waits for a Firebase-populated text view to become visible without a fixed delay.
     *
     * @param text the exact text expected in the view
     * @throws InterruptedException when the wait is interrupted
     */
    static void waitForDisplayedText(String text) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);
        while (System.nanoTime() < deadline) {
            try {
                Espresso.onView(withText(text)).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError ignored) {
                Thread.sleep(100);
            }
        }
        Espresso.onView(withText(text)).check(matches(isDisplayed()));
    }

    private TestUser createUser(String rolePrefix, String role) throws Exception {
        String email = rolePrefix + "-us12-" + suffix + "@example.test";
        FirebaseAuth auth = FirebaseManager.getInstance().getAuth();
        FirebaseUser firebaseUser = await(auth.createUserWithEmailAndPassword(email, PASSWORD)).getUser();
        if (firebaseUser == null) {
            throw new AssertionError("Firebase Auth did not create " + rolePrefix);
        }

        await(database().getReference("users").child(firebaseUser.getUid()).setValue(new User(email, role)));
        auth.signOut();
        return new TestUser(email, firebaseUser.getUid());
    }

    private void signIn(TestUser user) throws Exception {
        await(FirebaseManager.getInstance().getAuth()
                .signInWithEmailAndPassword(user.email, PASSWORD));
    }

    private void deleteUserProfile(TestUser user) throws Exception {
        if (user == null) {
            return;
        }

        await(database().getReference("users").child(user.uid).removeValue());
        signIn(user);
        FirebaseUser currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            await(currentUser.delete());
        }
    }

    private FirebaseDatabase database() {
        return FirebaseManager.getInstance().getDb();
    }

    private static <T> T await(com.google.android.gms.tasks.Task<T> task) throws Exception {
        return Tasks.await(task, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Holds the credentials needed to switch between fixture users.
     */
    static final class TestUser {
        private final String email;
        private final String uid;

        private TestUser(String email, String uid) {
            this.email = email;
            this.uid = uid;
        }
    }
}
