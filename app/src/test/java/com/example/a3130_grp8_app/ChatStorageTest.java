package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.ChatNotifier;
import com.example.a3130_grp8_app.util.IObserver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatStorageTest {

    StorageManager storageManager;

    User user1;
    User user2;
    String timeString;
    Message initialMessage;
    Chat originalChat;
    List<Chat> originalChatList;
    ChatNotifier notifier;

    @Before
    public void setUp() {
        storageManager = mock(StorageManager.class);

        user1 = new User("fake1@email.com", "buyer", "1");
        user2 = new User("fake2@email.com", "seller", "2");
        timeString = LocalDateTime.now().toString();
        Product productMock = mock(Product.class);

        initialMessage = new Message(user1, timeString, "Hello, is this available?", "1");
        List<Message> messageList = new ArrayList<>(List.of(initialMessage));

        originalChat = new Chat(user2, user1, productMock, messageList, "1");
        originalChatList = new ArrayList<>(List.of(originalChat));
        when(storageManager.loadObject(ChatStorage.CHATS_FILE_NAME))
                .thenReturn(originalChatList);

        notifier = mock(ChatNotifier.class);
        doNothing().when(notifier).attach(ArgumentMatchers.<IObserver<Message>>any());
        when(notifier.getChat()).thenReturn(originalChat);
    }

    @Test
    public void getChatsUpdatesSuccessfully() {
        Message nextMessage = new Message(user2, timeString, "Yes, it is available", "2");

        ChatStorage chatStorage = ChatStorage.getInstance(storageManager, notifier);
        assertEquals(chatStorage.getChats(), originalChatList);

        chatStorage.sendUpdate(nextMessage);
        chatStorage.sendUpdate(initialMessage);

        List<Chat> chats = chatStorage.getChats();
        assertEquals(1, chats.size());
        List<Message> newChatMessages = chats.getFirst().getMessages();
        assertEquals(2, newChatMessages.size());
        assertTrue(newChatMessages.contains(initialMessage) && newChatMessages.contains(nextMessage));

    }
}
