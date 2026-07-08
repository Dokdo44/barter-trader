package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LocationActivityTest {

    @Rule
    public ActivityScenarioRule<ProductListActivity> activityTestRule =
            new ActivityScenarioRule<>(ProductListActivity.class);

    @Before
    public void setUp() throws Exception {
        // Sign in as a buyer so the product list and map are accessible
        TestAuthHelper.signOut();
        TestAuthHelper.createUserProfile("testbuyer@dal.ca", "testPassword1!", "Buyer");
    }

    /**
     * Given I have searched for used products when I click
     * the map view button then I should see the map view displayed.
     */
    @Test
    public void mapViewButtonShowsMapTest() throws InterruptedException {
        Espresso.onView(withId(R.id.mapViewButton)).perform(click());
        TestAuthHelper.waitForDisplayedView(R.id.mapFragment);
    }

    /**
     * Given there are no products matching my search or filters when I open the map view
     * then I should see a message saying no products were found.
     */
    @Test
    public void noProductsMessageShownWhenSearchMatchesNothingTest() throws InterruptedException {
        Espresso.onView(withId(R.id.searchInput)).perform(typeText("noresults"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.mapViewButton)).perform(click());
        TestAuthHelper.waitForDisplayedView(R.id.noProductsMessage);
        Espresso.onView(withId(R.id.noProductsMessage)).check(matches(isDisplayed()));
    }

}
