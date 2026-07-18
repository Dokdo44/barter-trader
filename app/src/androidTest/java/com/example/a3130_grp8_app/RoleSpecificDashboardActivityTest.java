package com.example.a3130_grp8_app;


import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.RoleSpecificDashboardActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoleSpecificDashboardActivityTest {

    /**
     * Verifies that an unauthenticated user cannot stay on the protected dashboard.
     */
    @Test
    public void loggedOutUserIsRedirectedToLoginTest() {
        TestAuthHelper.signOut();

        ActivityScenario.launch(RoleSpecificDashboardActivity.class);

        Espresso.onView(withId(R.id.loginMain)).check(matches(isDisplayed()));
    }
}
