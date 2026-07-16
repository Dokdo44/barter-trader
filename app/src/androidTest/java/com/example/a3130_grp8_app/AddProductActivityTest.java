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

@RunWith(AndroidJUnit4.class)
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
    public void createListingButtonDisabledWhenTitleMissing() {
        fillRequiredFieldsExcept("title");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonDisabledWhenDescriptionMissing() {
        fillRequiredFieldsExcept("description");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonDisabledWhenLocationMissing() {
        fillRequiredFieldsExcept("location");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonDisabledWhenDateMissing() {
        fillRequiredFieldsExcept("date");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonDisabledWhenMarketValueMissing() {
        fillRequiredFieldsExcept("marketValue");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonDisabledWhenCategoryNotSelected() {
        fillRequiredFieldsExcept("category");
        Espresso.onView(withId(R.id.createListing)).check(matches(isNotEnabled()));
    }

    @Test
    public void createListingButtonEnabledWhenAllRequiredFieldsFilled() {
        fillRequiredFieldsExcept(null);
        Espresso.onView(withId(R.id.createListing)).check(matches(isEnabled()));
    }

    private void fillRequiredFieldsExcept(String skipField) {
        if (!"title".equals(skipField)) {
            typeInto(R.id.InputTitle, "Test listing");
        }
        if (!"description".equals(skipField)) {
            typeInto(R.id.InputDescription, "Test description");
        }
        if (!"location".equals(skipField)) {
            typeInto(R.id.InputLocation, "Test address");
        }
        if (!"date".equals(skipField)) {
            typeInto(R.id.InputDate, "06/07/2026");
        }
        if (!"marketValue".equals(skipField)) {
            typeInto(R.id.InputMarketValue, "20");
        }
        if (!"category".equals(skipField)) {
            selectCategory("Decoration");
        }
    }

    private void typeInto(int viewId, String text) {
        Espresso.onView(withId(viewId)).perform(typeText(text));
        Espresso.closeSoftKeyboard();
    }
    private void selectCategory(String categoryName) {
        Espresso.onView(withId(R.id.CategorySpinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is(categoryName))).perform(click());
    }
}
