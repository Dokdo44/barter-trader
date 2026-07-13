package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RequestProductActivityTest {

    @Before
    public void setUp() {
        TestAuthHelper.signOut();
    }

    /**
     * Verifies that a request is submitted successfully when the request button is clicked.
     */
    @Test
    public void requestSubmittedSuccessfullyTest() {
        ActivityScenario.launch(RequestProductActivity.class);
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that a duplicate request is prevented.
     */
    @Test
    public void duplicateRequestPreventedTest() {
        ActivityScenario.launch(RequestProductActivity.class);
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("already requested"))));
    }

    /**
     * Verifies that requesting an unavailable product shows an error.
     */
    @Test
    public void unavailableProductRequestRejectedTest() {
        ActivityScenario.launch(RequestProductActivity.class);
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("unavailable"))));
    }

    /**
     * Verifies that a submitted request exists in the database.
     */
    @Test
    public void requestRecordExistsInDatabaseTest() throws InterruptedException{
        ActivityScenario.launch(RequestProductActivity.class);
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        TestAuthHelper.waitForDisplayedView(R.id.requestSuccess);
        Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
    }
}