package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.MessageNotifier;
import com.example.a3130_grp8_app.util.IMessageObserver;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatStorageTest {

    StorageManager storageManager;

    User user1;
    User user2;
    String timeString;
    Message initialMessage;
    Chat originalChat;
    List<Chat> originalChatList;
    MessageNotifier notifier;

    @Before
    public void setUp() {
        storageManager = mock(StorageManager.class);

        user1 = new User("fake1@email.com", "buyer");
        user2 = new User("fake2@email.com", "seller");
        timeString = LocalDateTime.now().toString();
        Product productMock = mock(Product.class);

        Map<String, Message> messageMap = new HashMap<>();
        messageMap.put(initialMessage.getId(), initialMessage);

        originalChatList = new ArrayList<>(List.of(originalChat));
        when(storageManager.loadObject(ChatStorage.CHATS_FILE_NAME))
                .thenReturn(originalChatList);

        notifier = mock(MessageNotifier.class);
        when(notifier.getChat()).thenReturn(originalChat);
    }

    /**
     * Not a good method, need to update later
     */
    @Test
    public void getChatsUpdatesSuccessfully() {

        ChatStorage chatStorage = ChatStorage.getInstance(storageManager, notifier);
        assertEquals(chatStorage.getChats(), originalChatList);


        List<Chat> chats = chatStorage.getChats();
        assertEquals(1, chats.size());

    }
}
