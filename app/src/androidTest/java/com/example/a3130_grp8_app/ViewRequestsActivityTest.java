package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.ViewRequestsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

/**
 * Firebase Emulator acceptance tests for a provider's US12 exchange-offer decisions.
 */
@RunWith(AndroidJUnit4.class)
public class ViewRequestsActivityTest {

    private ExchangeOfferEmulatorFixture fixture;
    private String requestId;

    /**
     * Connects Firebase SDK instances before any provider request screen is launched.
     */
    @BeforeClass
    public static void connectToEmulators() {
        ExchangeOfferEmulatorFixture.connectToEmulators();
    }

    /**
     * Seeds one pending proposal for an available provider product.
     *
     * @throws Exception when emulator setup fails
     */
    @Before
    public void setUp() throws Exception {
        fixture = new ExchangeOfferEmulatorFixture();
        fixture.setUp();
        requestId = fixture.submitProposal(Collections.singletonList("Skateboard"));
        fixture.signInProvider();
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
     * Given I am the provider for a product with a proposal, when I open the proposal, then I
     * can view the receiver details, current status, and offered item.
     */
    @Test
    public void providerCanViewProposedItemAndReceiverDetailsTest() throws Exception {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            ExchangeOfferEmulatorFixture.waitForDisplayedText("Receiver One - pending");
            Espresso.onView(withText("Receiver One - pending")).perform(click());

            Espresso.onView(withText("Requester Details")).check(matches(isDisplayed()));
            Espresso.onView(withText(org.hamcrest.CoreMatchers.containsString("pending")))
                    .check(matches(isDisplayed()));
            Espresso.onView(withText(org.hamcrest.CoreMatchers.containsString("Skateboard")))
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Given I am viewing a pending proposal, when I accept it, then the stored status becomes
     * accepted.
     */
    @Test
    public void providerCanAcceptProposalTest() throws Exception {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            ExchangeOfferEmulatorFixture.waitForDisplayedText("Receiver One - pending");
            Espresso.onView(withText("Receiver One - pending")).perform(click());
            Espresso.onView(withText("Accept")).perform(click());
        }

        fixture.waitForRequestStatus(requestId, "accepted");
    }

    /**
     * Given I am viewing a pending proposal, when I decline it, then the stored status becomes
     * declined.
     */
    @Test
    public void providerCanDeclineProposalTest() throws Exception {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            ExchangeOfferEmulatorFixture.waitForDisplayedText("Receiver One - pending");
            Espresso.onView(withText("Receiver One - pending")).perform(click());
            Espresso.onView(withText("Decline")).perform(click());
        }

        fixture.waitForRequestStatus(requestId, "declined");
    }

    private ActivityScenario<ViewRequestsActivity> launchProviderRequests() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewRequestsActivity.class);
        intent.putExtra("PRODUCT_ID", fixture.getProductId());
        return ActivityScenario.launch(intent);
    }
}
