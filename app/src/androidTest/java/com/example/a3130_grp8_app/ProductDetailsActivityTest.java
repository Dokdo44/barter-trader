package com.example.a3130_grp8_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ProductDetailsActivityTest {

    private static final String PRODUCT_ID = "product-details-test-product";
    private static final String PRODUCT_DETAILS_ACTIVITY =
            "com.example.a3130_grp8_app.activities.ProductDetailsActivity";

    @After
    public void clearFirebaseManager() {
        FirebaseManager.install(null);
    }

    /**
     * Verifies that opening a selected product shows every detail required by the user story.
     *
     * @throws Exception when the detail screen cannot be launched or loaded
     */
    @Test
    public void selectedProductShowsFullDetailsTest() throws Exception {
        openProductDetails();

        waitForText("Vintage Lamp");
        onView(withText("Vintage Lamp")).check(matches(isDisplayed()));
        onView(withText("Brass table lamp in working condition"))
                .check(matches(isDisplayed()));
        onView(withText("Location: Halifax, NS")).check(matches(isDisplayed()));
        onView(withText("Available: 2026-07-30")).check(matches(isDisplayed()));
        onView(withText("Approximate Market Value: $45.00")).check(matches(isDisplayed()));
    }

    /**
     * Verifies that receivers can start the request flow from the product details screen.
     *
     * @throws Exception when the detail screen cannot be launched or loaded
     */
    @Test
    public void sendOfferButtonIsAvailableFromProductDetailsTest() throws Exception {
        openProductDetails();

        waitForText("Request Item");
        onView(withText("Request Item")).check(matches(isDisplayed()));
    }

    private void openProductDetails() {
        Intent intent = new Intent();
        intent.setClassName(BuildConfig.APPLICATION_ID, PRODUCT_DETAILS_ACTIVITY);

        Product product = new Product(
                "Vintage Lamp",
                "Brass table lamp in working condition",
                "Halifax, NS",
                45.0f,
                new ArrayList<String>(),
                true,
                "https://example.com/vintage-lamp.jpg",
                "placeholder",
                "2026-07-30",
                "2026-07-30",
                new ArrayList<String>()
        );

        product.setId("1");

        intent.putExtra("PRODUCT", product);
        ActivityScenario.launch(intent);
    }

    private void waitForText(String text) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            try {
                onView(withText(text)).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError error) {
                Thread.sleep(250);
            }
        }

        onView(withText(text)).check(matches(isDisplayed()));
    }

    private FirebaseManager getMockManager() {
        FirebaseManager ret = mock(FirebaseManager.class);

        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("1");

        when(ret.getCurrentUser()).thenReturn(mockUser);

        return ret;
    }

    @Test
    public void testAddPreferredProductSuccess() {
        FirebaseManager mockManager = getMockManager();
        when(mockManager.addPreferredProductForUser("1", "1"))
                .thenReturn(Tasks.forResult(null));

        FirebaseManager.install(mockManager);
        openProductDetails();

        onView(withId(R.id.addToPreferredProductsButton)).perform(click());

        onView(withText("Successfully added products to preferred products"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddPreferredProductFailure() {
        FirebaseManager mockManager = getMockManager();
        when(mockManager.addPreferredProductForUser("1", "1"))
                .thenReturn(Tasks.forException(new Exception("Mock exception")));

        FirebaseManager.install(mockManager);
        openProductDetails();

        doAnswer(invocation -> null)
                .when(mockManager).addChildEventListenerForUserPreferredProductIds(eq("1"), any());

        onView(withId(R.id.addToPreferredProductsButton)).perform(click());

        onView(withText("Could not add product to preferred products: Mock exception"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAddPreferredProductAlreadyAdded() {
        FirebaseManager mockManager = getMockManager();
        DataSnapshot snapshotMock = mock(DataSnapshot.class);
        when(snapshotMock.getValue(String.class)).thenReturn("1");

        doAnswer(invocation -> {
            ChildEventListener childEventListener = invocation.getArgument(1);
            childEventListener.onChildAdded(snapshotMock, null);
            return null;
        }).when(mockManager).addChildEventListenerForUserPreferredProductIds(eq("1"), any());

        FirebaseManager.install(mockManager);
        openProductDetails();

        onView(withId(R.id.addToPreferredProductsButton)).perform(click());

        // Check that snackbar prevents multiple submissions
        onView(withText("You have already added this product as a preferred product"))
                .check(matches(isDisplayed()));
    }
}
