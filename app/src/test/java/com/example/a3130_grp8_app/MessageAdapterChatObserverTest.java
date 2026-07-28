package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.FirebaseManager;
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

    MessageNotifier messageNotifier;
    ChatStorage chatStorage;
    MessagesAdapter messagesAdapter;
    Chat chat;
    List<Message> messages;
    List<Message> messagesRet;

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager = mock(FirebaseManager.class);
        doNothing().when(mockManager).addChildEventListenerForChatMessages(any(), any());
        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        User provider = new User("fake1@email.com", "seller");
        provider.setId("1");
        User receiver = new User("fake2@email.com", "buyer");
        receiver.setId("2");
        Product product = mock(Product.class);

        chat = new Chat(
                provider.getEmail(),
                receiver.getEmail(),
                "productName",
                "productId",
                new HashMap<>(),
                "1",
                provider.getId(),
                receiver.getId());

        messageNotifier = new MessageNotifier(chat);
        messagesAdapter = mock(MessagesAdapter.class);
        messagesRet = new ArrayList<>();

        doAnswer(invocation -> {
            int position = invocation.getArgument(0);
            Message message = invocation.getArgument(1);
            messagesRet.add(position, message);
            return null;
        }).when(messagesAdapter).addMessage(anyInt(), any());
        when(messagesAdapter.getMessages()).thenReturn(messagesRet);

        StorageManager storageManager = mock(StorageManager.class);
        when(storageManager.loadObject(any())).thenReturn(null);
        chatStorage = ChatStorage.getInstance(storageManager, messageNotifier);

        Message message1 = new Message(receiver.getEmail(), "2026-07-26T09:00:00", "Hey, are you around?", "1", "m1");
        Message message2 = new Message(receiver.getEmail(), "2026-07-26T09:05:32", "Just checking in on the order status.", "1", "m2");
        Message message3 = new Message(receiver.getEmail(), "2026-07-26T09:12:47", "No rush, whenever you get a chance.", "1", "m3");
        Message message4 = new Message(receiver.getEmail(), "2026-07-26T09:20:15", "Thanks, talk soon!", "1", "m4");
        messages = new ArrayList<>(List.of(message1, message2, message3, message4));
    }

    @Test
    public void testMessagesAdapterMessagesUpdateInCorrectOrder() {
        MessageAdapterChatMessageObserver messageAdapterChatObserver =
                new MessageAdapterChatMessageObserver(messageNotifier, chatStorage, messagesAdapter);
        for (Message message : messages) {
            messageAdapterChatObserver.update(message);
        }
        List<Message> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(Message::getTimeSent).reversed())
                .toList();
        assertEquals(messagesRet, sortedMessages);
    }
}