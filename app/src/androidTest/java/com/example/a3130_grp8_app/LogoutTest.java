package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNull;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.android.gms.tasks.Tasks;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LogoutTest {

    private static final String Test_Password = "Test1234!";

    /**
     * Creates a fresh Firebase user and signs them in so a test can start logged in
     * @throws Exception
     */
    private void createAndSignInTestUser() throws Exception {
        String email = TestAuthHelper.uniqueEmail("logout");
        TestAuthHelper.createUserProfile(email, Test_Password, "Buyer");
        Tasks.await(FirebaseManager.getInstance().signIn(email, Test_Password), 15, TimeUnit.SECONDS);
    }

    /**
     * Clicking logout while signed in logs the user out and clears the Firebase Auth session
     * @throws Exception
     */

    @Test
    public void clickingLogoutSignsUserOut() throws Exception {
        createAndSignInTestUser();

        ActivityScenario.launch(RoleSpecificDashboardActivity.class);
        Espresso.onView(withId(R.id.logoutButton)).perform(click());

        TestAuthHelper.waitForDisplayedView(R.id.loginMain);
        assertNull(FirebaseManager.getInstance().getCurrentUser());
    }

    /**
     * Logging out redirects to login screen
     * @throws Exception
     */
    @Test
    public void loggingOutRedirectsToLoginTest() throws Exception {
        createAndSignInTestUser();

        ActivityScenario.launch(RoleSpecificDashboardActivity.class);
        Espresso.onView(withId(R.id.logoutButton)).perform(click());

        TestAuthHelper.waitForDisplayedView(R.id.loginMain);
        Espresso.onView(withId(R.id.loginMain)).check(matches(isDisplayed()));
    }

    /**
     * After logging out the dashboard isn't accessible
     * @throws Exception
     */
    @Test
    public void afterLogoutDashboardIsNotAccessibleTest() throws Exception {
        createAndSignInTestUser();

        ActivityScenario.launch(RoleSpecificDashboardActivity.class);
        Espresso.onView(withId(R.id.logoutButton)).perform(click());
        TestAuthHelper.waitForDisplayedView(R.id.loginMain);

        ActivityScenario.launch(RoleSpecificDashboardActivity.class);
        TestAuthHelper.waitForDisplayedView(R.id.loginMain);
        Espresso.onView(withId(R.id.loginMain)).check(matches(isDisplayed()));
    }

    //for acceptance test 4 i.e. Verifies that an unauthenticated user cannot stay on the protected dashboard, a test already exists in RoleSpecificDashboardActivityTest.java
}
