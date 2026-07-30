package com.example.a3130_grp8_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.ProductChatActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class ProductChatActivityTest {

    Intent intent;
    static Chat chat1;
    static Chat chat2;
    static Chat chat3;
    static Message message1;
    static Message message2;
    static Message message3;

    @BeforeClass
    public static void setUpClass() throws FirebaseConnectionError {
        FirebaseManager mockFirebaseManager = mock(FirebaseManager.class);

        User receiver = new User("fake@email.com", "buyer");
        User provider = new User("fake2@email.com", "seller");
        Product product = mock(Product.class);
        chat1 = new Chat(provider.getEmail(),
                receiver.getEmail(),
                product.getName(),
                product.getId(),
                new HashMap<>(),
                "1",
                provider.getId(),
                receiver.getId());
        chat2 = new Chat(provider.getEmail(),
                receiver.getEmail(),
                product.getName(),
                product.getId(),
                new HashMap<>(),
                "2",
                provider.getId(),
                receiver.getId());
        chat3 = new Chat(provider.getEmail(),
                receiver.getEmail(),
                product.getName(),
                product.getId(),
                new HashMap<>(),
                "3",
                provider.getId(),
                receiver.getId());

        when(mockFirebaseManager.getCurrentUserProfile()).thenReturn(Tasks.forResult(receiver));

        when(mockFirebaseManager.addMessageToChat(eq(chat1.getId()), any(), any(), any()))
                .thenReturn(Tasks.forResult(null));

        when(mockFirebaseManager.addMessageToChat(eq(chat3.getId()), any(), any(), any()))
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
        intent.putExtra("CHAT", chat1);
        ActivityScenario.launch(intent);

        onView(withId(R.id.messageInput)).perform(typeText("Hi, is this still available?"));
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()));
    }
    /**
     * AC1: Receiver cannot send an empty message.
     * Given I am viewing a product
     * When I leave the message empty and click send
     * Then the message is not sent and an error is displayed
     */
    @Test
    public void emptyMessageNotSentTest() {
        intent.putExtra("CHAT", chat2);
        ActivityScenario.launch(intent);

        onView(withId(R.id.sendButton)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to send message: Message cannot be empty")));
    }
    /**
     * AC1, AC3: Handle message send failure gracefully.
     * Given I am viewing a product
     * When I send a message but the Firebase call fails
     * Then an error message is displayed
     */
    @Test
    public void messageSendFailedTest() {
        intent.putExtra("CHAT", chat3);
        ActivityScenario.launch(intent);

        onView(withId(R.id.messageInput)).perform(typeText("Hello!"));
        onView(withId(R.id.sendButton)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to send message: Failed to send message")));
    }
}
