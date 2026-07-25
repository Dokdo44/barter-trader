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

import com.example.a3130_grp8_app.activities.ProductChatActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProductChatActivityTest {

    Intent intent;
    static Chat chat;
    static Message message1;
    static Message message2;
    static Message message3;

    @BeforeClass
    public static void setUpClass() throws FirebaseConnectionError {
        FirebaseManager mockFirebaseManager = mock(FirebaseManager.class);

        chat = mock(Chat.class);
        message1 = new Message(new User("fake@email.com", "buyer", "1"),
                "2026-07-25 18:15:30", "Hi, is this still available?", "1");
        message2 = new Message(new User("fake@email.com", "buyer", "1"),
                "2026-07-25 18:15:30", "", "2");
        message3 = new Message(new User("fake@email.com", "buyer", "1"),
                "2026-07-25 18:15:30", "will fail!", "3");

        when(mockFirebaseManager.addMessageToChat(chat, message1))
                .thenReturn(Tasks.forResult(null));

        when(mockFirebaseManager.addMessageToChat(chat, message2))
                .thenReturn(Tasks.forException(new Exception("Message cannot be empty")));

        when(mockFirebaseManager.addMessageToChat(chat, message3))
                .thenReturn(Tasks.forException(new Exception("Failed to send message")));

        FirebaseManager.install(mockFirebaseManager);
    }

    @Before
    public void setUp() {
        TestAuthHelper.signOut();
        intent = new Intent(ApplicationProvider.getApplicationContext(), ProductChatActivity.class);
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
    /**
     * AC1: Receiver cannot send an empty message.
     * Given I am viewing a product
     * When I leave the message empty and click send
     * Then the message is not sent and an error is displayed
     */
    @Test
    public void emptyMessageNotSentTest() {
        intent.putExtra("PRODUCT_ID", "1");
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.sendButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()));
    }
    /**
     * AC1, AC3: Handle message send failure gracefully.
     * Given I am viewing a product
     * When I send a message but the Firebase call fails
     * Then an error message is displayed
     */
    @Test
    public void messageSendFailedTest() {
        intent.putExtra("PRODUCT_ID", "2");
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.messageInput)).perform(typeText("Hello!"));
        Espresso.onView(withId(R.id.sendButton)).perform(click());
        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()));
    }
}
