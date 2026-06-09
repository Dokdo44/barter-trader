package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityTestRule = new ActivityScenarioRule<>(SignupActivity.class);

    @Test
    public void signUpButtonDisabledWhenFieldsInitiallyEmptyTest() {
        // Type nothing in both fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText(""));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText(""));
        Espresso.closeSoftKeyboard();

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledWhenEmailEmptyTest() {
        //Type nothing in email field, but type a password
        Espresso.onView(withId(R.id.emailInput)).perform(typeText(""));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledWhenPasswordEmptyTest() {
        //Type nothing in email field, but type a password
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText(""));
        Espresso.closeSoftKeyboard();

        // Check that the sign-up button is disabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void signUpButtonDisabledAfterFieldBecomesEmptyTest() {
        // Type something in both the email and password fields
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("validemail@gmail.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("g00dPass*123"));
        Espresso.closeSoftKeyboard();

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

        // Check that the sign-up button is enabled
        Espresso.onView(withId(R.id.signUpButton)).check(matches(isEnabled()));
    }
}
