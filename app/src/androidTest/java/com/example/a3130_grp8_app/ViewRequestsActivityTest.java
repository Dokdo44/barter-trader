package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.ViewRequestsActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Request;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

/**
 * Mocked-Firebase acceptance tests for a provider's US12 exchange-offer decisions.
 */
@RunWith(AndroidJUnit4.class)
public class ViewRequestsActivityTest {

    private static final String PRODUCT_ID = "offer-product";
    private static final String EMPTY_PRODUCT_ID = "empty-product";
    private static final String REQUEST_ID = "offer-request";
    private static FirebaseManager mockFirebaseManager;

    /**
     * Installs a mocked Firebase manager with one pending proposal for the provider screen.
     */
    @BeforeClass
    public static void setUpClass() {
        mockFirebaseManager = mock(FirebaseManager.class);

        Request request = new Request(
                REQUEST_ID,
                PRODUCT_ID,
                "receiver@example.test",
                "receiver-uid",
                "Receiver One",
                "Dartmouth, NS",
                Collections.singletonList("Skateboard"),
                "provider-uid"
        );

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener = invocation.getArgument(1);
            listener.onGetRequests(Collections.singletonList(request));
            return null;
        }).when(mockFirebaseManager).getRequests(eq(PRODUCT_ID), any());

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener = invocation.getArgument(1);
            listener.onGetRequests(Collections.emptyList());
            return null;
        }).when(mockFirebaseManager).getRequests(eq(EMPTY_PRODUCT_ID), any());

        when(mockFirebaseManager.updateRequestStatus(anyString(), anyString()))
                .thenReturn(Tasks.forResult(null));

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
     * Clears mock interactions before each provider decision scenario.
     */
    @Before
    public void setUp() {
        clearInvocations(mockFirebaseManager);
    }

    /**
     * Given I open a pending proposal, when I view its details, then I can see the receiver,
     * status, and offered item.
     */
    @Test
    public void providerCanViewProposedItemAndReceiverDetailsTest() {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            Espresso.onView(withText("Receiver One - pending")).perform(click());

            Espresso.onView(withText("Requester Details")).check(matches(isDisplayed()));
            Espresso.onView(withText(containsString("receiver@example.test"))).check(matches(isDisplayed()));
            Espresso.onView(withText(containsString("pending"))).check(matches(isDisplayed()));
            Espresso.onView(withText(containsString("Skateboard"))).check(matches(isDisplayed()));
        }
    }

    /**
     * Given I accept a pending proposal, when I select Accept, then its stored status changes to
     * accepted.
     */
    @Test
    public void providerCanAcceptProposalTest() {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            Espresso.onView(withText("Receiver One - pending")).perform(click());
            Espresso.onView(withText("Accept")).perform(click());
        }

        verify(mockFirebaseManager).updateRequestStatus(REQUEST_ID, "accepted");
    }

    /**
     * Given I reject a pending proposal, when I select Decline, then its stored status changes to
     * declined.
     */
    @Test
    public void providerCanDeclineProposalTest() {
        try (ActivityScenario<ViewRequestsActivity> scenario = launchProviderRequests()) {
            Espresso.onView(withText("Receiver One - pending")).perform(click());
            Espresso.onView(withText("Decline")).perform(click());
        }

        verify(mockFirebaseManager).updateRequestStatus(REQUEST_ID, "declined");
    }

    /**
     * Given a product has no submitted proposals, when the provider opens the request list, then
     * the empty-state message is shown.
     */
    @Test
    public void noOffersMessageDisplayedWhenListEmptyTest() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewRequestsActivity.class);
        intent.putExtra("PRODUCT_ID", EMPTY_PRODUCT_ID);
        try (ActivityScenario<ViewRequestsActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(withText("No offers have been submitted")).check(matches(isDisplayed()));
        }
    }

    /**
     * Opens the provider's request list for the product with the pending proposal.
     *
     * @return the launched provider request-list scenario
     */
    private ActivityScenario<ViewRequestsActivity> launchProviderRequests() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewRequestsActivity.class);
        intent.putExtra("PRODUCT_ID", PRODUCT_ID);
        return ActivityScenario.launch(intent);
    }
}