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
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.RequestProductActivity;
import com.example.a3130_grp8_app.model.Request;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

/**
 * Firebase Emulator acceptance tests for a receiver's US12 exchange proposal flow.
 */
@RunWith(AndroidJUnit4.class)
public class RequestProductActivityTest {

    private ExchangeOfferEmulatorFixture fixture;

    /**
     * Connects Firebase SDK instances before any request activity is launched.
     */
    @BeforeClass
    public static void connectToEmulators() {
        ExchangeOfferEmulatorFixture.connectToEmulators();
    }

    /**
     * Seeds an isolated available product and receiver for each test.
     *
     * @throws Exception when emulator setup fails
     */
    @Before
    public void setUp() throws Exception {
        fixture = new ExchangeOfferEmulatorFixture();
        fixture.setUp();
        fixture.signInReceiver();
    }

    /**
     * Removes the test-only Firebase records created for the current scenario.
     *
     * @throws Exception when emulator cleanup fails
     */
    @After
    public void tearDown() throws Exception {
        fixture.tearDown();
    }

    /**
     * Given I am viewing an available product, when I select a provider-preferred item and send
     * a request, then a pending exchange proposal is stored.
     */
    @Test
    public void selectedOfferIsSubmittedAndStoredAsPendingTest() throws Exception {
        try (ActivityScenario<RequestProductActivity> scenario = launchRequestForm()) {
            completeProposalForm();
            Espresso.onView(withId(R.id.requestButton)).perform(click());
            ExchangeOfferEmulatorFixture.waitForDisplayedView(R.id.requestSuccess);
        }

        Request storedRequest = fixture.getOnlyRequest();
        assertEquals(fixture.getProductId(), storedRequest.getProductId());
        assertEquals(fixture.getReceiverUid(), storedRequest.getReceiverUid());
        assertEquals(Collections.singletonList("Skateboard"), storedRequest.getOfferedItems());
        assertEquals("pending", storedRequest.getStatus());
    }

    /**
     * Given I leave every offer unselected, when I send the request, then I see an error and no
     * exchange proposal is stored.
     */
    @Test
    public void emptyOfferIsRejectedWithoutSavingProposalTest() throws Exception {
        try (ActivityScenario<RequestProductActivity> scenario = launchRequestForm()) {
            ExchangeOfferEmulatorFixture.waitForDisplayedText("Skateboard");
            Espresso.onView(withId(R.id.nameInput)).perform(typeText("Receiver One"), closeSoftKeyboard());
            Espresso.onView(withId(R.id.locationInput)).perform(typeText("Dartmouth, NS"), closeSoftKeyboard());
            Espresso.onView(withId(R.id.requestButton)).perform(click());

            Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(containsString("Select at least one item"))));
        }

        assertEquals(0, fixture.getRequestCount());
    }

    /**
     * Given I already submitted a proposal for a product, when I submit a second proposal, then
     * the application reports a duplicate and keeps only the original proposal.
     */
    @Test
    public void duplicateProposalIsRejectedWithoutCreatingAnotherRequestTest() throws Exception {
        try (ActivityScenario<RequestProductActivity> firstScenario = launchRequestForm()) {
            completeProposalForm();
            Espresso.onView(withId(R.id.requestButton)).perform(click());
            ExchangeOfferEmulatorFixture.waitForDisplayedView(R.id.requestSuccess);
        }

        try (ActivityScenario<RequestProductActivity> secondScenario = launchRequestForm()) {
            completeProposalForm();
            Espresso.onView(withId(R.id.requestButton)).perform(click());

            Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(containsString("already requested"))));
        }

        assertEquals(1, fixture.getRequestCount());
    }

    private ActivityScenario<RequestProductActivity> launchRequestForm() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RequestProductActivity.class);
        intent.putExtra("PRODUCT_ID", fixture.getProductId());
        return ActivityScenario.launch(intent);
    }

    private void completeProposalForm() throws InterruptedException {
        ExchangeOfferEmulatorFixture.waitForDisplayedText("Skateboard");
        Espresso.onView(withId(R.id.nameInput)).perform(typeText("Receiver One"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.locationInput)).perform(typeText("Dartmouth, NS"), closeSoftKeyboard());
        Espresso.onView(withText("Skateboard")).perform(click());
        Espresso.onView(withText("Skateboard")).check(matches(isDisplayed()));
    }
}
