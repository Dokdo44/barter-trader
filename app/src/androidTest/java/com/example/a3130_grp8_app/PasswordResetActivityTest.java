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

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PasswordResetActivityTest {

    @Rule
    public ActivityScenarioRule<PasswordResetActivity> activityTestRule =
            new ActivityScenarioRule<>(PasswordResetActivity.class);

    @Test
    public void resetButtonDisabledWhenEmailInitiallyEmptyTest() {

        //No email was typed
        Espresso.onView(withId(R.id.resetButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void resetButtonDisabledWhenEmailInvalidTest() {

        //when email entered is invalid
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("group8invalidemail"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.resetButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void resetButtonEnabledWhenEmailValidTest() {

        //when valid email is entered reset button should be enabled
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("group8validemail@gmail.com"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.resetButton)).check(matches(isEnabled()));
    }

    @Test
    public void resetButtonDisabledAfterEmailClearedTest() {

        //valid email is entered but is deleted
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("group8validemail@gmail.com"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.emailInput)).perform(clearText());

        Espresso.onView(withId(R.id.resetButton)).check(matches(isNotEnabled()));
    }

    @Test
    public void canRequestResetWithValidEmailTest() throws InterruptedException {

        //to check if a password request is sent to a valid email and shows success snackbar message
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("group8testacc@gmail.com"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.resetButton)).perform(click());

        //time to let firebase network call return
        Thread.sleep(3000);

        //to check if request actually succeeded comparing it to the success message
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(containsString("Check your spam")))));

    }

    @Test
    public void snackbarAppearsWhenResetButtonIsPressedTest() throws InterruptedException {

        //to check if a password request is sent to a non-registered email failure snackbar message
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("group8validemail@gmail.com"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.resetButton)).perform(click());

        //time to let firebase network call return
        Thread.sleep(3000);

        //to check if snackbar appears
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()));

    }

    @Test
    public void backToLoginGoesToLoginPageTest() {

        //to check if pressing back to log in takes us back to the login page
        Espresso.onView(withId(R.id.backToLoginButton)).perform(click());

        Espresso.onView(withId(R.id.loginMain)).check(matches(isDisplayed()));
    }
}
