package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.ViewRequestsActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Request;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ViewRequestsActivityTest {

    Intent intent;

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager = Mockito.mock(FirebaseManager.class);

        List<String> offeredItems1 = List.of("Book: Clean Code", "Gift Card $10");
        Request request1 = new Request(
                "req-001",
                "prod-123",
                "interestedbuyer1@gmail.com",
                "uid-buyer1",
                "Alice Johnson",
                "Toronto, ON",
                offeredItems1
        );

        List<String> offeredItems2 = List.of("Vintage Vinyl Record", "Handmade Candle");
        Request request2 = new Request(
                "req-002",
                "prod-456",
                "interestedbuyer2@gmail.com",
                "uid-buyer2",
                "Marcus Lee",
                "Vancouver, BC",
                offeredItems2
        );

        ArrayList<Request> testRequests = new ArrayList<>(List.of(request1, request2));

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener requestValueEventListener =
                    invocation.getArgument(1);
            requestValueEventListener.onGetRequests(testRequests);
            return null;
        }).when(mockManager).getRequests(eq("1"), any());

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener requestValueEventListener =
                    invocation.getArgument(1);
            requestValueEventListener.onGetRequests(new ArrayList<>(List.of(request1)));
            return null;
        }).when(mockManager).getRequests(eq("2"), any());

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener requestValueEventListener =
                    invocation.getArgument(1);
            requestValueEventListener.onGetRequests(new ArrayList<>());
            return null;
        }).when(mockManager).getRequests(eq("0"), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        intent = new Intent(ApplicationProvider.getApplicationContext(), ViewRequestsActivity.class);

    }

    /**
     * Verifies that all requests submitted for a product are displayed.
     */
    @Test
    public void allRequestsDisplayedTest() {
        intent.putExtra("PRODUCT_ID", "1");
        ActivityScenario.launch(intent);
        // All submitted requests should be displayed
        Espresso.onView(withText(containsString("Alice Johnson")))
                .check(matches(isDisplayed()));
        Espresso.onView(withText(containsString("Marcus Lee")))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that each request displays the requester's name and request details.
     */
    @Test
    public void requesterNameAndDetailsDisplayedTest() {
        intent.putExtra("PRODUCT_ID", "2");
        ActivityScenario.launch(intent);
        // The requester's email and request status should be displayed
        Espresso.onView(withText(containsString("Alice Johnson")))
                .check(matches(isDisplayed()));

        Espresso.onView(withText(containsString("pending")))
                .check(matches(isDisplayed()));
    }

    /**
     * A fail-safe to verify in the case of no requests being present
     */
    @Test
    public void noRequestsMessageDisplayedTest() {
        intent.putExtra("PRODUCT_ID", "0");
        ActivityScenario.launch(intent);
        // If no offers are present then "No offers have been submitted" should be displayed
        Espresso.onView(withText("No offers have been submitted"))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that clicking a request opens the requester's full trade details.
     */
    @Test
    public void clickingRequestOpensRequesterDetailsTest() {
        intent.putExtra("PRODUCT_ID", "1");
        ActivityScenario.launch(intent);
        // Given requests are displayed and we click an offer
        Espresso.onView(withText(containsString("Alice Johnson")))
                .perform(click());
        // Then the requester details screen should be shown
        Espresso.onView(withText(containsString("interestedbuyer1@gmail.com")))
                .check(matches(isDisplayed()));
        Espresso.onView(withText(containsString("pending")))
                .check(matches(isDisplayed()));
    }
}
