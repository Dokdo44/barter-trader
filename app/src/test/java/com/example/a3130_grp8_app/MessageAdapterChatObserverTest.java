package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.data.MessageStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.MessageNotifier;
import com.example.a3130_grp8_app.util.MessageAdapterChatMessageObserver;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MessageAdapterChatObserverTest {

    // Objects under test
    MessageNotifier messageNotifier;
    MessageStorage messageStorage;

    // Mock adapter used to verify messages are inserted correctly
    MessagesAdapter messagesAdapter;

    // Test chat and message collections
    Chat chat;
    List<Message> messages;
    List<Message> messagesRet;

    /**
     * Installs a mocked FirebaseManager so no real Firebase
     * listeners are created during testing.
     */
    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager = mock(FirebaseManager.class);

        doNothing().when(mockManager)
                .addChildEventListenerForChatMessages(any(), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        // Create sample users
        User provider = new User("fake1@email.com", "seller");
        provider.setId("1");

        User receiver = new User("fake2@email.com", "buyer");
        receiver.setId("2");

        // Product is unused by this test, so a mock is sufficient
        Product product = mock(Product.class);

        // Create a sample chat
        chat = new Chat(
                provider.getEmail(),
                receiver.getEmail(),
                "productName",
                "productId",
                new HashMap<>(),
                "1",
                provider.getId(),
                receiver.getId()
        );

        // Create notifier and mocked adapter
        messageNotifier = new MessageNotifier(chat);
        messagesAdapter = mock(MessagesAdapter.class);

        // Simulated adapter backing list
        messagesRet = new ArrayList<>();

        // Simulate MessagesAdapter.addMessage()
        doAnswer(invocation -> {
            int position = invocation.getArgument(0);
            Message message = invocation.getArgument(1);

            messagesRet.add(position, message);
            return null;
        }).when(messagesAdapter).addMessage(anyInt(), any());

        // Return the simulated message list
        when(messagesAdapter.getMessages()).thenReturn(messagesRet);

        // Mock storage so nothing is loaded from disk
        StorageManager storageManager = mock(StorageManager.class);
        when(storageManager.loadObject(any())).thenReturn(null);

        messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);

        // Create messages with increasing timestamps
        Message message1 = new Message(
                receiver.getEmail(),
                "2026-07-26T09:00:00",
                "Hey, are you around?",
                "1",
                "m1"
        );

        Message message2 = new Message(
                receiver.getEmail(),
                "2026-07-26T09:05:32",
                "Just checking in on the order status.",
                "1",
                "m2"
        );

        Message message3 = new Message(
                receiver.getEmail(),
                "2026-07-26T09:12:47",
                "No rush, whenever you get a chance.",
                "1",
                "m3"
        );

        Message message4 = new Message(
                receiver.getEmail(),
                "2026-07-26T09:20:15",
                "Thanks, talk soon!",
                "1",
                "m4"
        );

        messages = new ArrayList<>(List.of(
                message1,
                message2,
                message3,
                message4
        ));
    }

    /**
     * Verifies that when messages are received by the observer,
     * they are inserted into the adapter in reverse chronological order
     * (newest message first).
     */
    @Test
    public void testMessagesAdapterMessagesUpdateInCorrectOrder() {
        MessageAdapterChatMessageObserver observer =
                new MessageAdapterChatMessageObserver(
                        messageNotifier,
                        messageStorage,
                        messagesAdapter
                );

        // Simulate receiving messages
        for (Message message : messages) {
            observer.update(message);
        }

        // Expected ordering: newest timestamp first
        List<Message> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(Message::getTimeSent).reversed())
                .toList();

        assertEquals(sortedMessages, messagesRet);
    }
}