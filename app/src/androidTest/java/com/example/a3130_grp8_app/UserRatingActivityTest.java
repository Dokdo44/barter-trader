package com.example.a3130_grp8_app;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.a3130_grp8_app.activities.UserRatingActivity;

@RunWith(AndroidJUnit4.class)
public class UserRatingActivityTest {

    private Intent intent;

    @Before
    public void setUp() {
        intent = new Intent(
                ApplicationProvider.getApplicationContext(), UserRatingActivity.class
        );
    }

    /**
     * Verifies that a receiver can submit a rating after an exchange.
     */
    @Test
    public void receiverCanRateProviderTest() {
        ActivityScenario.launch(intent);
        onView(withId(R.id.ratingBar)).perform(click());

        onView(withId(R.id.submitRatingButton)).perform(click());

        onView(withText("Rating submitted")).check(matches(isDisplayed()));
    }

    /**
     * Verifies that submitted ratings are displayed on the provider profile.
     */
    @Test
    public void ratingsDisplayedOnProviderProfileTest() {
        ActivityScenario.launch(intent);

        onView(withId(R.id.providerRatingRecyclerView)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the average rating is displayed.
     */
    @Test
    public void averageRatingDisplayedTest() {
        ActivityScenario.launch(intent);

        onView(withId(R.id.averageRatingText)).check(matches(isDisplayed()));
    }
}