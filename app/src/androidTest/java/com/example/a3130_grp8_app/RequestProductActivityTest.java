package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.RequestProductActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RequestProductActivityTest {

    Intent intent;

    @Before
    public void setUp() throws FirebaseConnectionError {
        TestAuthHelper.signOut();

        FirebaseManager mockFirebaseManager = mock(FirebaseManager.class);

        Product validProduct = new Product(
                "testProduct",
                "testProduct",
                "test",
                10.0f,
                new ArrayList<String>(List.of("test")),
                true,
                "test",
                "test",
                "test",
                "test",
                new ArrayList<String>(List.of("test"))
        );

        Product unavailableProduct = new Product(
                "testProduct",
                "testProduct",
                "test",
                10.0f,
                new ArrayList<String>(List.of("test")),
                false,
                "test",
                "test",
                "test",
                "test",
                new ArrayList<String>(List.of("test"))
        );

        when(mockFirebaseManager.getProduct("1")).thenReturn(
                Tasks.forResult(validProduct));

        when(mockFirebaseManager.getProduct("2")).thenReturn(
                Tasks.forResult(validProduct));

        when(mockFirebaseManager.getProduct("3")).thenReturn(
                Tasks.forResult(unavailableProduct)
        );

        when(mockFirebaseManager.submitRequest(eq("1"),
                eq("user1"),
                eq("Fake Street"),
                any())).thenReturn(Tasks.forResult(null));

        when(mockFirebaseManager.submitRequest(eq("2"),
                eq("user1"),
                eq("Fake Street"),
                any())).thenReturn(Tasks.forException(
                        new Exception("You have already requested this product")));

        when(mockFirebaseManager.submitRequest(eq("3"),
                eq("user1"),
                eq("Fake Street"),
                any())).thenReturn(Tasks.forException(
                        new Exception("Product is no longer available")));

        FirebaseManager.install(mockFirebaseManager);

        intent = new Intent(ApplicationProvider.getApplicationContext(), RequestProductActivity.class);
    }

    /**
     * Verifies that a request is submitted successfully when the request button is clicked.
     */
    @Test
    public void requestSubmittedSuccessfullyTest() {
        intent.putExtra("PRODUCT_ID", "1");
        ActivityScenario.launch(intent);
        Espresso.onView(withId(R.id.nameInput)).perform(typeText("user1"));
        Espresso.onView(withId(R.id.locationInput)).perform(typeText("Fake Street"));
        Espresso.onView(withText("test")).perform(click());

        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(R.id.requestSuccess)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that a duplicate request is prevented.
     */
    @Test
    public void duplicateRequestPreventedTest() {
        intent.putExtra("PRODUCT_ID", "2");
        ActivityScenario.launch(intent);
        Espresso.onView(withId(R.id.nameInput)).perform(typeText("user1"));
        Espresso.onView(withId(R.id.locationInput)).perform(typeText("Fake Street"));
        Espresso.onView(withText("test")).perform(click());

        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("already requested"))));
    }

    /**
     * Verifies that requesting an unavailable product shows an error.
     */
    @Test
    public void unavailableProductRequestRejectedTest() {
        intent.putExtra("PRODUCT_ID", "3");
        ActivityScenario.launch(intent);
        Espresso.onView(withId(R.id.nameInput)).perform(typeText("user1"));
        Espresso.onView(withId(R.id.locationInput)).perform(typeText("Fake Street"));
        Espresso.onView(withText("test")).perform(click());

        Espresso.onView(withId(R.id.requestButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(containsString("no longer available"))));
    }
}