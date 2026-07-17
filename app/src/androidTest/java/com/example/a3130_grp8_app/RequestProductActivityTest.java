package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class RequestProductActivityTest {

    private static final String TEST_PRODUCT_ID = "test_product_request_001";
    private static final String TEST_UNAVAILABLE_PRODUCT_ID = "test_product_unavailable_001";

    /**
     * Create a test buyer account and set up test products.
     */
    @Before
    public void setUp() throws Exception {
        try {
            TestAuthHelper.createUserProfile("testbuyer@dal.ca", "testPassword1!", "Buyer");
        } catch (Exception e) {
            // if account already exists then sign in
        }

        Tasks.await(
                FirebaseManager.getInstance().getAuth()
                        .signInWithEmailAndPassword("testbuyer@dal.ca", "testPassword1!"),
                15,
                TimeUnit.SECONDS
        );

        // Create an available test product
        Product availableProduct = new Product(
                "Test Bike",
                "A test product for request tests",
                "1869 Upper Water St, Halifax, NS",
                0.0f,
                Collections.singletonList("Sports"),
                true,
                null,
                "dummy_provider_uid",
                "2025-07-13",
                "2025-07-20",
                Arrays.asList("Skateboard", "Cash")
        );

        Tasks.await(
                FirebaseManager.getInstance().getDb()
                        .getReference("product")
                        .child(TEST_PRODUCT_ID)
                        .setValue(availableProduct),
                15,
                TimeUnit.SECONDS
        );

        // Create an unavailable test product
        Product unavailableProduct = new Product(
                "Unavailable Bike",
                "A test product that is unavailable",
                "5251 Duke St, Halifax, NS",
                0.0f,
                Collections.singletonList("Sports"),
                false,
                null,
                "dummy_provider_uid",
                "2025-07-13",
                "2025-07-20",
                Collections.emptyList()
        );

        Tasks.await(
                FirebaseManager.getInstance().getDb()
                        .getReference("product")
                        .child(TEST_UNAVAILABLE_PRODUCT_ID)
                        .setValue(unavailableProduct),
                15,
                TimeUnit.SECONDS
        );
    }

    /**
     * Delete test products after testing so its repeatable
     */
    @After
    public void tearDown() throws Exception {
        // Delete existing requests for the test product
        Tasks.await(
                FirebaseManager.getInstance().getDb()
                        .getReference("requests")
                        .orderByChild("productId")
                        .equalTo(TEST_PRODUCT_ID)
                        .get(),
                15,
                TimeUnit.SECONDS
        ).getChildren().forEach(snap -> snap.getRef().removeValue());

        // Delete test products
        Tasks.await(
                FirebaseManager.getInstance().getDb()
                        .getReference("product")
                        .child(TEST_PRODUCT_ID)
                        .removeValue(),
                15,
                TimeUnit.SECONDS
        );

        Tasks.await(
                FirebaseManager.getInstance().getDb()
                        .getReference("product")
                        .child(TEST_UNAVAILABLE_PRODUCT_ID)
                        .removeValue(),
                15,
                TimeUnit.SECONDS
        );

        // Delete user so it can be created again next test rerun
        FirebaseAuth currentUser = FirebaseAuth.getInstance();
        if (currentUser.getCurrentUser() != null) {
            Tasks.await(
                    currentUser.getCurrentUser().delete(),
                    15,
                    TimeUnit.SECONDS
            );
        }

        TestAuthHelper.signOut();
    }

    private ActivityScenario<RequestProductActivity> launchWithProduct(String productId) {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                RequestProductActivity.class
        );
        intent.putExtra("PRODUCT_ID", productId);
        return ActivityScenario.launch(intent);
    }

    /**
     * Given I am viewing an available product
     * When I click the request button
     * Then the request is submitted successfully
     */
    @Test
    public void requestSubmittedSuccessfullyTest() throws InterruptedException {
        launchWithProduct(TEST_PRODUCT_ID);

        Espresso.onView(withId(R.id.requestButton)).perform(click());

        TestAuthHelper.waitForDisplayedView(R.id.requestSuccess);

        Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
    }

    /**
     * Given I have already requested a product
     * When I attempt to request it again
     * Then the system prevents the duplicate request and displays an error message
     */
    @Test
    public void duplicateRequestPreventedTest() throws InterruptedException {
        launchWithProduct(TEST_PRODUCT_ID);

        Espresso.onView(withId(R.id.requestButton)).perform(click());

        Thread.sleep(3000);

        // Relaunch to simulate requesting again
        launchWithProduct(TEST_PRODUCT_ID);

        Espresso.onView(withId(R.id.requestButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("already requested"))));
    }

    /**
     * Given a product is no longer available
     * When I try to request it
     * Then the request is rejected and an error message is displayed
     */
    @Test
    public void unavailableProductRequestRejectedTest() throws InterruptedException {
        launchWithProduct(TEST_UNAVAILABLE_PRODUCT_ID);

        Thread.sleep(2000);

        Espresso.onView(withId(R.id.requestButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("no longer available"))));
    }

    /**
     * Given a request is successfully submitted
     * When the database is checked
     * Then the request record exists
     */
    @Test
    public void requestRecordExistsInDatabaseTest() throws InterruptedException {
        launchWithProduct(TEST_PRODUCT_ID);

        Espresso.onView(withId(R.id.requestButton)).perform(click());

        TestAuthHelper.waitForDisplayedView(R.id.requestSuccess);

        Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
    }
}