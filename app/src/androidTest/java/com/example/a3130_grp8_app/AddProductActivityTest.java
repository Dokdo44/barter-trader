package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.clearText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.EspressoException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class AddProductActivityTest {

    @Rule
    public ActivityScenarioRule<AddProductActivity> activityTestRule =
            new ActivityScenarioRule<>(AddProductActivity.class);

    @Test
    public void createListingButtonDisableWhenFieldsAreEmpty()
    {
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void CreateListingButtonDisabledWhenOnlyTitleFilled()
    {
        Espresso.onView(withId(R.id.InputTitle)).perform(typeText("Test listing"));

        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void CreateListingButtonDisabledWhenOnlyCategorySelected()
    {
        Espresso.onView(withId(R.id.CategorySpinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("decoration"))).perform(click());

        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));

    }
}
