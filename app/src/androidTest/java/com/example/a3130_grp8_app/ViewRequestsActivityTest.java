package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewRequestsActivityTest {

    @Rule
    public ActivityScenarioRule<ViewRequestsActivity> activityTestRule =
            new ActivityScenarioRule<>(ViewRequestsActivity.class);

    /**
     * Verifies that all requests submitted for a product are displayed.
     */
    @Test
    public void allRequestsDisplayedTest() {
        // Given a provider is logged in
        // And the product has requests
        // When the provider opens the product listing
        // Then all submitted requests should be displayed
    }

    /**
     * Verifies that each request displays the requester's name and request details.
     */
    @Test
    public void requesterNameAndDetailsDisplayedTest() {
        // Given requests exist
        // When viewing the request list
        // Then each request should display the requester's name
        // And request details
    }

    /**
     * A fail-safe to verify in the case of no requests being present
     */
    @Test
    public void noRequestsMessageDisplayedTest() {
        // Given there are no requests
        // When viewing the request list
        // Then "No requests have been submitted" should be displayed
    }

    /**
     * Verifies that clicking a request opens the requester's full trade details.
     */
    @Test
    public void clickingRequestOpensRequesterDetailsTest() {
        // Given requests are displayed
        // When a request is clicked
        // Then the requester details screen should be shown
    }
}
