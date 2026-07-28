package com.example.a3130_grp8_app;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import com.example.a3130_grp8_app.activities.UserRatingActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Rating;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class UserRatingActivityTest {

    private Intent intent;

    @BeforeClass
    public static void setUpClass() {

        FirebaseManager mockManager = Mockito.mock(FirebaseManager.class);

        ArrayList<Rating> ratings = new ArrayList<>();

        ratings.add(new Rating(
                "receiver1",
                "provider1",
                5,
                "Excellent!"
        ));

        ratings.add(new Rating(
                "receiver2",
                "provider1",
                4,
                "Great experience."
        ));

        // Provider with ratings
        doAnswer(invocation -> {
            FirebaseManager.RatingValueEventListener listener = invocation.getArgument(1);

            listener.onGetRatings(ratings);
            return null;
        }).when(mockManager).getRatings(eq("provider1"), any());

        // Provider with no ratings
        doAnswer(invocation -> {
            FirebaseManager.RatingValueEventListener listener =
                    invocation.getArgument(1);

            listener.onGetRatings(new ArrayList<>());
            return null;
        }).when(mockManager).getRatings(eq("provider2"), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        intent = new Intent(
                ApplicationProvider.getApplicationContext(), UserRatingActivity.class
        );
    }

    @Test
    public void ratingsDisplayedOnProviderProfileTest() {
        intent.putExtra("PROVIDER_ID", "provider1");

        ActivityScenario.launch(intent);

        onView(withText("Excellent!")).check(matches(isDisplayed()));

        onView(withText("Great experience.")).check(matches(isDisplayed()));
    }

    @Test
    public void averageRatingDisplayedTest() {
        intent.putExtra("PROVIDER_ID", "provider1");

        ActivityScenario.launch(intent);

        // (5 + 4) / 2 = 4.5
        onView(withText("4.5"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void noRatingsDisplayedTest() {
        intent.putExtra("PROVIDER_ID", "provider2");

        ActivityScenario.launch(intent);

        onView(withText("No ratings yet")).check(matches(isDisplayed()));
    }
}