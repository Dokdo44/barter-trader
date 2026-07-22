package com.example.a3130_grp8_app;


import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.AddPreferredProductActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AddPreferredProductActivityTest {

    private static FirebaseManager mockManager;

    @Rule
    public ActivityScenarioRule<AddPreferredProductActivity> activityTestRule =
            new ActivityScenarioRule<>(AddPreferredProductActivity.class);

    @BeforeClass
    public static void setUpClass() {
        mockManager = mock(FirebaseManager.class);
        when(mockManager.addPreferredProduct("1", "IPad", List.of("Technology")))
                .thenReturn(Tasks.forResult(null));

        when(mockManager.addPreferredProduct("1", "Plant", List.of("Decoration")))
                .thenReturn(Tasks.forException(new Exception("Mock exception")));

        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("1");

        when(mockManager.getCurrentUser()).thenReturn(mockUser);
    }

    @Before
    public void setUp() {
        FirebaseManager.install(mockManager);
    }

    @Test
    public void addPreferredProductSuccess() {
        Espresso.onView(withId(R.id.addPreferredProductQueryEditText)).perform(typeText("IPad"));
        Espresso.onView(withId(R.id.addPreferredProductCategoriesEditText))
                .perform(typeText("Technology"));
        Espresso.onView(withId(R.id.addPreferredProductButton)).perform(click());

        // Displays Snackbar with text
        Espresso.onView(withText("Successfully added preferred product")).check(matches(isDisplayed()));
    }

    @Test
    public void addPreferredProductFailure() {
        Espresso.onView(withId(R.id.addPreferredProductQueryEditText)).perform(typeText("Plant"));
        Espresso.onView(withId(R.id.addPreferredProductCategoriesEditText))
                .perform(typeText("Decoration"));
        Espresso.onView(withId(R.id.addPreferredProductButton)).perform(click());

        // Displays error Snackbar with text
        Espresso.onView(withText("Could not add preferred product to Firebase: Mock exception"))
                .check(matches(isDisplayed()));
    }
}
