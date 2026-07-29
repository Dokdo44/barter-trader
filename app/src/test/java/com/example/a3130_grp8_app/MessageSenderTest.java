package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

import android.os.Looper;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.MessageSender;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(RobolectricTestRunner.class)
public class MessageSenderTest {

    // Sample users used by the tests
    User provider;
    User receiver;

    // Chat shared between the users
    Chat chat;

    // Mock Firebase manager used to simulate database operations
    static FirebaseManager mockManager;

    /**
     * Installs a mocked FirebaseManager so no real
     * Firebase requests are made during testing.
     */
    @BeforeClass
    public static void setUpClass() {
        mockManager = mock(FirebaseManager.class);
        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        // Create sample provider
        provider = new User("provider@email.com", "seller");
        provider.setId("1");
        provider.setMessageInbox(new HashMap<>());

        // Create sample receiver
        receiver = new User("receiver@email.com", "buyer");
        receiver.setId("2");
        receiver.setMessageInbox(new HashMap<>());

        // Product is not used directly by these tests
        Product productMock = mock(Product.class);

        // Create a chat shared between the users
        HashMap<String, String> messageIds = new HashMap<>();
        chat = new Chat(
                provider.getEmail(),
                receiver.getEmail(),
                "productName",
                "productId",
                messageIds,
                "1",
                provider.getId(),
                receiver.getId()
        );

        // Simulate adding a message to Firebase.
        // A new Message is created and its ID is stored in the chat.
        doAnswer(invocation -> {
            String chatId = invocation.getArgument(0);
            String senderName = invocation.getArgument(1);
            String timeSent = invocation.getArgument(2);
            String messageString = invocation.getArgument(3);

            Message message = new Message(
                    senderName,
                    timeSent,
                    messageString,
                    chatId,
                    "m1"
            );

            chat.getMessageIds().put(message.getId(), message.getId());

            return Tasks.forResult(message);
        }).when(mockManager).addMessageToChat(eq("1"), any(), any(), any());

        // Simulate adding a message to a user's inbox.
        // The message is marked as unread (true).
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            String messageId = invocation.getArgument(1);

            Map<String, Boolean> inbox = userId.equals(provider.getId())
                    ? provider.getMessageInbox()
                    : receiver.getMessageInbox();

            inbox.put(messageId, true);

            return Tasks.forResult(null);
        }).when(mockManager).addMessageToUserInbox(any(), any());
    }

    /**
     * Verifies that when the provider sends a message,
     * the message is added to the chat and only the
     * receiver's inbox is updated with an unread message.
     */
    @Test
    public void testMessageSendsToChatAndInboxReceiver()
            throws ExecutionException, InterruptedException, TimeoutException {

        MessageSender messageSender =
                new MessageSender(provider.getId(), provider.getEmail(), chat);

        Task<Message> sendMessageTask =
                messageSender.sendMessage("test", "This is a test!");

        // Wait for the asynchronous task to complete
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() ->
                Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        // Execute pending main-thread tasks
        shadowOf(Looper.getMainLooper()).idle();

        Message sent = future.get(5, TimeUnit.SECONDS);

        // Verify the message was stored in the chat
        assertTrue(chat.getMessageIds().containsValue(sent.getId()));

        // Verify only the receiver received an unread inbox entry
        assertTrue(receiver.getMessageInbox().containsKey(sent.getId()));
        assertEquals(Boolean.TRUE, receiver.getMessageInbox().get(sent.getId()));

        // Sender should not receive their own message notification
        assertTrue(provider.getMessageInbox().isEmpty());
    }

    /**
     * Verifies that when the receiver sends a message,
     * the message is added to the chat and only the
     * provider's inbox is updated with an unread message.
     */
    @Test
    public void testMessageSendsToChatAndInboxProvider()
            throws ExecutionException, InterruptedException, TimeoutException {

        MessageSender messageSender =
                new MessageSender(receiver.getId(), receiver.getEmail(), chat);

        Task<Message> sendMessageTask =
                messageSender.sendMessage("test", "This is a test!");

        // Wait for the asynchronous task to complete
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() ->
                Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        // Execute pending main-thread tasks
        shadowOf(Looper.getMainLooper()).idle();

        Message sent = future.get(5, TimeUnit.SECONDS);

        // Verify the message was stored in the chat
        assertTrue(chat.getMessageIds().containsValue(sent.getId()));

        // Verify only the provider received an unread inbox entry
        assertTrue(provider.getMessageInbox().containsKey(sent.getId()));
        assertEquals(Boolean.TRUE, provider.getMessageInbox().get(sent.getId()));

        // Sender should not receive their own message notification
        assertTrue(receiver.getMessageInbox().isEmpty());
    }
}