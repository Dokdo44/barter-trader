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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityTestRule =
            new ActivityScenarioRule<>(SignupActivity.class);

    @Test
    public void signUpButtonDisabledWhenFieldsInitiallyEmptyTest() {
        // Type nothing in both fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText(""));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        // Set the spinner to the default value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(0).perform(click());

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledWhenEmailEmptyTest() {
        //Type nothing in email field, but type a password
        Espresso.onView(withId(R.id.emailInput)).perform(typeText(""));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledWhenPasswordEmptyTest() {
        //Type nothing in email field, but type a password
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledWhenRoleSpinnerNotValidTest() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to the default value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(0).perform(click());

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledAfterFieldBecomesEmptyTest() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Delete everything from the email field
        Espresso.onView(withId(R.id.emailInput)).perform(clearText());

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonEnabledWhenFieldsFilledTest() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that the sign-up button is enabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isEnabled()));
    }

    @Test
    public void errorMessageWhenEmailIsInvalidTest() {
        // Type invalid email address and valid password
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("invalid"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that the error snackbar is shown and has an error message
        Espresso.onView(withId(R.id.signUpButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text)).check(
                matches(allOf(isDisplayed(), withText(containsString("Error: ")))));
    }

    @Test
    public void redirectsToRoleSelectionWhenEmailIsValidTest() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();
        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that when the sign-up button is clicked, it redirects to the role selection page
        Espresso.onView(withId(R.id.signUpButton)).perform(click());
        Espresso.onView(withId(R.id.roleSelectionMain)).check(matches(isDisplayed()));
    }

    @Test
    public void redirectsToRoleSpecificDashboardWithUserNameAndRoleOnValidSignUp() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();

        // Set the spinner to a filled value
        Espresso.onView(withId(R.id.roleSpinner)).perform(click());
        Espresso.onData(is(instanceOf(String.class))).atPosition(1).perform(click());

        // Check that when the sign-up button is clicked, it redirects to the role selection page
        Espresso.onView(withId(R.id.signUpButton)).perform(click());
        Espresso.onView(withId(R.id.userText)).check(matches(withText(allOf(
                containsString("validemail@gmail.com"),
                containsString("Buyer")
        ))));
    }
}
