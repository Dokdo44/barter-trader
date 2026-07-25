package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    List<Chat> originalChatList;

    @Before
    public void setUp() {
        storageManager = mock(StorageManager.class);

        user1 = new User("fake1@email.com", "buyer", "1");
        user2 = new User("fake2@email.com", "seller", "2");
        timeString = LocalDateTime.now().toString();
        Product productMock = mock(Product.class);

        initialMessage = new Message(user1, timeString, "Hello, is this available?", "1");
        List<Message> messageList = new ArrayList<>(List.of(initialMessage));

        originalChatList = new ArrayList<>(List.of(new Chat(user2, user1, productMock, messageList)));
        when(storageManager.loadObject(ChatStorage.CHATS_FILE_NAME))
                .thenReturn(originalChatList);
    }

    @Test
    public void getChatsUpdatesSuccessfully() {
        Message nextMessage = new Message(user2, timeString, "Yes, it is available", "2");

        ChatStorage chatStorage = new ChatStorage(storageManager);
        assertEquals(chatStorage.getChats(), originalChatList);

        List<Message> messageList = new ArrayList<>(List.of(initialMessage, nextMessage));
        Chat nextChat = new Chat(user2, user1, mock(Product.class), messageList);
        chatStorage.sendUpdate(nextChat);

        List<Chat> chats = chatStorage.getChats();
        assertEquals(1, chats.size());
        List<Message> newChatMessages = chats.getFirst().getMessages();
        assertTrue(newChatMessages.contains(initialMessage) && newChatMessages.contains(nextMessage));

    }
}
