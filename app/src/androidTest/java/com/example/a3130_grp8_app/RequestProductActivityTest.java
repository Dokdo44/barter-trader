package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.RequestProductActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mocked-Firebase acceptance tests for a receiver's US12 exchange proposal flow.
 *
 * <p>These tests deliberately avoid Firebase Emulator dependencies. The mocked manager verifies
 * the activity's request contract while the Request model test verifies default pending state.</p>
 */
@RunWith(AndroidJUnit4.class)
public class RequestProductActivityTest {

    private static final String VALID_PRODUCT_ID = "valid-product";
    private static final String DUPLICATE_PRODUCT_ID = "duplicate-product";
    private static FirebaseManager mockFirebaseManager;

    /**
     * Installs a mocked Firebase manager and defines product/request results for the test suite.
     */
    @BeforeClass
    public static void setUpClass() throws FirebaseConnectionError {
        mockFirebaseManager = mock(FirebaseManager.class);

        Product product = new Product(
                VALID_PRODUCT_ID,
                "US12 Test Bike",
                "Available for exchange",
                100.0f,
                new ArrayList<>(),
                true,
                "Halifax, NS",
                "2026-07-24",
                "provider-uid",
                "provider@example.test",
                new ArrayList<>(List.of("Skateboard", "Desk lamp"))
        );

        when(mockFirebaseManager.getProduct(anyString())).thenReturn(Tasks.forResult(product));
        when(mockFirebaseManager.submitRequest(
                anyString(), anyString(), anyString(), anyString(), anyList())).thenReturn(Tasks.forResult(null));
        when(mockFirebaseManager.submitRequest(
                eq(DUPLICATE_PRODUCT_ID), anyString(), anyString(), anyString(), anyList())).thenReturn(
                Tasks.forException(new Exception("You have already requested this product"))
        );

        FirebaseManagerTestInstaller.install(mockFirebaseManager);
    }

    /**
     * Clears the mocked singleton so it does not affect later Android test classes.
     */
    @AfterClass
    public static void tearDownClass() {
        FirebaseManagerTestInstaller.reset();
    }

    /**
     * Clears previous mock interactions so each scenario verifies only its own submission.
     */
    @Before
    public void setUp() {
        clearInvocations(mockFirebaseManager);
    }

    /**
     * Given I select a provider-preferred item, when I submit a request, then the selected item
     * is sent to the request service and the request completes successfully.
     */
    @Test
    public void selectedOfferIsSubmittedAndStoredAsPendingTest() throws FirebaseConnectionError {
        try (ActivityScenario<RequestProductActivity> scenario = launchRequestForm(VALID_PRODUCT_ID)) {
            completeProposalForm();
            Espresso.onView(withId(R.id.requestButton)).perform(click());
            Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
        }

        ArgumentCaptor<List<String>> offeredItems = ArgumentCaptor.forClass(List.class);
        verify(mockFirebaseManager).submitRequest(
                eq(VALID_PRODUCT_ID),
                anyString(),
                eq("Receiver One"),
                eq("Dartmouth, NS"),
                offeredItems.capture()
        );
        assertEquals(Collections.singletonList("Skateboard"), offeredItems.getValue());
    }

    /**
     * Given I leave every offer unselected, when I submit the request, then an error is shown and
     * no request is sent to Firebase.
     */
    @Test
    public void emptyOfferIsRejectedWithoutSavingProposalTest() throws FirebaseConnectionError {
        try (ActivityScenario<RequestProductActivity> scenario = launchRequestForm(VALID_PRODUCT_ID)) {
            Espresso.onView(withId(R.id.nameInput)).perform(typeText("Receiver One"), closeSoftKeyboard());
            Espresso.onView(withId(R.id.locationInput)).perform(typeText("Dartmouth, NS"), closeSoftKeyboard());
            Espresso.onView(withId(R.id.requestButton)).perform(click());

            Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(containsString("Select at least one item"))));
        }

        verify(mockFirebaseManager, never()).submitRequest(
                eq(VALID_PRODUCT_ID), anyString(), anyString(), anyString(), anyList());
    }

    /**
     * Given Firebase reports an existing request, when I submit another proposal, then the
     * duplicate error is shown and the form does not report success.
     */
    @Test
    public void duplicateProposalIsRejectedWithoutCreatingAnotherRequestTest()
            throws FirebaseConnectionError {
        try (ActivityScenario<RequestProductActivity> scenario = launchRequestForm(DUPLICATE_PRODUCT_ID)) {
            completeProposalForm();
            Espresso.onView(withId(R.id.requestButton)).perform(click());

            Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(containsString("already requested"))));
        }

        verify(mockFirebaseManager).submitRequest(
                eq(DUPLICATE_PRODUCT_ID), anyString(), eq("Receiver One"), eq("Dartmouth, NS"), anyList());
    }

    /**
     * Opens the request form for the supplied product identifier.
     *
     * @param productId product receiving the exchange proposal
     * @return the launched request form scenario
     */
    private ActivityScenario<RequestProductActivity> launchRequestForm(String productId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RequestProductActivity.class);
        intent.putExtra("PRODUCT_ID", productId);
        return ActivityScenario.launch(intent);
    }

    /**
     * Enters valid receiver details and selects the first provider-preferred exchange item.
     */
    private void completeProposalForm() {
        Espresso.onView(withId(R.id.nameInput)).perform(typeText("Receiver One"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.locationInput)).perform(typeText("Dartmouth, NS"), closeSoftKeyboard());
        Espresso.onView(withText("Skateboard")).perform(click());
    }
}