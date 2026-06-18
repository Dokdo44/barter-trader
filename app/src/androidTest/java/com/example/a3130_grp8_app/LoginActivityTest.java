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

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityTestRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Verifies that the login button starts disabled when both credential fields are empty.
     */
    @Test
    public void loginButtonDisabledWhenFieldsInitiallyEmptyTest() {
        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    /**
     * Verifies that entering only an email does not enable login.
     */
    @Test
    public void loginButtonDisabledWhenOnlyEmailFilledTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.com"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    /**
     * Verifies that entering only a password does not enable login.
     */
    @Test
    public void loginButtonDisabledWhenOnlyPasswordFilledTest() {
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    /**
     * Verifies that clearing the email disables login after the form was complete.
     */
    @Test
    public void loginButtonDisabledAfterEmailClearedTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.emailInput)).perform(clearText());

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    /**
     * Verifies that entering both email and password enables login.
     */
    @Test
    public void loginButtonEnabledWhenBothFieldsFilledTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isEnabled()));
    }

    /**
     * Verifies that a missing account shows an invalid credentials message.
     *
     * @throws InterruptedException when the test wait is interrupted
     */
    @Test
    public void errorShownWhenUserDoesNotExistTest() throws InterruptedException {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("notanaccount@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("somePassword1!"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(containsString("Invalid")))));
    }

    /**
     * Verifies that an incorrect password shows an invalid credentials message.
     *
     * @throws InterruptedException when the test wait is interrupted
     */
    @Test
    public void errorShownForIncorrectPasswordTest() throws InterruptedException {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("testbuyer@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("wrongPassword1!"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(allOf(isDisplayed(), withText(containsString("Invalid")))));
    }

    /**
     * Verifies that valid buyer credentials open the dashboard.
     *
     * @throws InterruptedException when the test wait is interrupted
     */
    @Test
    public void successfulLoginRedirectsToDashboardTest() throws InterruptedException {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("testbuyer@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("testPassword1!"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(R.id.roleSelectionMain))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that buyer credentials display the buyer dashboard role.
     *
     * @throws InterruptedException when the test wait is interrupted
     */
    @Test
    public void dashboardShowsOnlyBuyerRoleTest() throws InterruptedException {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("testbuyer@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("testPassword1!"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(R.id.dashboardRole))
                .check(matches(withText(containsString("Buyer"))));
    }

    /**
     * Verifies that seller credentials display the seller dashboard role.
     *
     * @throws InterruptedException when the test wait is interrupted
     */
    @Test
    public void dashboardShowsSellerRoleTest() throws InterruptedException {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("testuserseller@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("sellerPassword1!"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(3000);

        Espresso.onView(withId(R.id.dashboardRole))
                .check(matches(withText(containsString("Seller"))));
    }
}
