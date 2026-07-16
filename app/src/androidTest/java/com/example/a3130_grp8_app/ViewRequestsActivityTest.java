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
        // All submitted requests should be displayed
        Espresso.onView(withText("intrestedbuyer1@gmail.com"))
                .check(matches(isDisplayed()));
        Espresso.onView(withText("intrestedbuyer2@gmail.com"))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that each request displays the requester's name and request details.
     */
    @Test
    public void requesterEmailDisplayedTest() {
        // The requester's email should be displayed
        Espresso.onView(withText("intrestedbuyer1@gmail.com"))
                .check(matches(isDisplayed()));
    }

    /**
     * A fail-safe to verify in the case of no requests being present
     */
    @Test
    public void noRequestsMessageDisplayedTest() {
        // If no offers are present then "No offers have been submitted" should be displayed
        Espresso.onView(withText("No offers have been submitted"))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that clicking a request opens the requester's full trade details.
     */
    @Test
    public void clickingRequestDisplaysRequesterInformationTest() {
        // Given requests are displayed and we click an offer
        Espresso.onView(withText("intrestedbuyer1@gmail.com"))
                .perform(click());

        // Then the requester information should be displayed
        Espresso.onView(withText("intrestedbuyer1@gmail.com"))
                .check(matches(isDisplayed()));
    }
}