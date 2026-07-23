package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.ContactProviderActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ContactProviderActivityTest {

    Intent intent;

    @BeforeClass
    public static void setUpClass() throws FirebaseConnectionError {
        FirebaseManager mockFirebaseManager = mock(FirebaseManager.class);

        when(mockFirebaseManager.sendMessage(eq("1"), eq("Hi, is this still available?")))
                .thenReturn(Tasks.forResult(null));

        when(mockFirebaseManager.sendMessage(eq("1"), eq("")))
                .thenReturn(Tasks.forException(new Exception("Message cannot be empty")));

        when(mockFirebaseManager.sendMessage(eq("2"), any()))
                .thenReturn(Tasks.forException(new Exception("Failed to send message")));

        FirebaseManager.install(mockFirebaseManager);
    }

    @Before
    public void setUp() {
        TestAuthHelper.signOut();
        intent = new Intent(ApplicationProvider.getApplicationContext(), ContactProviderActivity.class);
    }
    /**
     * AC1: Receiver can send a message to the provider from the product page.
     * Given I am viewing a product
     * When I type a message and click send
     * Then the message is sent successfully
     */
    @Test
    public void messageSentSuccessfullyTest() {
        intent.putExtra("PRODUCT_ID", "1");
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.messageInput)).perform(typeText("Hi, is this still available?"));
        Espresso.onView(withId(R.id.sendButton)).perform(click());
        Espresso.onView(withId(R.id.messageSentText)).check(matches(isDisplayed()));
    }
}
