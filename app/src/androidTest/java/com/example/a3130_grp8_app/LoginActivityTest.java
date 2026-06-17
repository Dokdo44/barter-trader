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

    // Given I open the login screen
    // When I have not typed anything
    // Then the login button should be disabled
    @Test
    public void loginButtonDisabledWhenFieldsInitiallyEmptyTest() {
        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    // Given I open the login screen
    // When I type only an email
    // Then the login button should be disabled
    @Test
    public void loginButtonDisabledWhenOnlyEmailFilledTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.com"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    // Given I open the login screen
    // When I type only a password
    // Then the login button should be disabled
    @Test
    public void loginButtonDisabledWhenOnlyPasswordFilledTest() {
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    // Given I open the login screen
    // When I fill both fields then clear the email
    // Then the login button should be disabled again
    @Test
    public void loginButtonDisabledAfterEmailClearedTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.com"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.emailInput)).perform(clearText());

        Espresso.onView(withId(R.id.loginButton)).check(matches(isNotEnabled()));
    }

    // Given I open the login screen
    // When I fill both email and password
    // Then the login button should be enabled
    @Test
    public void loginButtonEnabledWhenBothFieldsFilledTest() {
        Espresso.onView(withId(R.id.emailInput)).perform(typeText("test@dal.ca"));
        Espresso.onView(withId(R.id.passwordInput)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.loginButton)).check(matches(isEnabled()));
    }

    // Given I enter my login details when I have not signed up
    // Then I should be prompted that the credentials are invalid
    /**
     * Make sure that notanaccount@dal.ca does not exist
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

    // Given I enter an incorrect password when I am trying to login
    // Then I should be prompted an incorrect password/username
    // And my fields should be cleared
    /**
     * Make sure that testuser@dal.ca exists in firebase
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

    // Given I am logged in when I submit my login details
    // Then I should be redirected to the app
    /**
     * Make sure that testuser@dal.ca exists in firebase
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

    // Given I am a buyer when I log in
    // then I should only be able to see the buyer dashboard
    /**
     * Make sure that testuser@dal.ca exists in firebase and is a buyer
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

    // Given I am a seller when I log in
    // then I should only be able to see the seller dashboard
    /**
     * Make sure that testuserseller@dal.ca exists in firebase and is a seller
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

