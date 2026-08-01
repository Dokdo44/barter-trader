package com.example.a3130_grp8_app;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.util.ChatNotifier;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatStorageTest {

    // Mocked dependencies used by ChatStorage
    StorageManager storageManager;
    ChatNotifier chatNotifier;

    // In-memory map used to simulate persistent storage
    Map<String, Object> fakeStore;

    // Sample chat objects used in tests
    Chat chat1;
    Chat chat2;

    @Before
    public void setUp() {
        // Create mock objects
        storageManager = mock(StorageManager.class);
        chatNotifier = mock(ChatNotifier.class);

        // Simulate save/load behavior using a HashMap
        // This allows objects saved by ChatStorage to be retrieved later
        fakeStore = new HashMap<>();

        doAnswer(invocation -> {
            fakeStore.put(
                    invocation.getArgument(0), // filename/key
                    invocation.getArgument(1)  // object to store
            );
            return null;
        }).when(storageManager).saveObject(any(), any());

        when(storageManager.loadObject(any()))
                .thenAnswer(invocation ->
                        fakeStore.get(invocation.getArgument(0)));

        // Create sample chats for testing
        chat1 = new Chat(
                "providerA", "receiverA",
                "productA", "productIdA",
                new HashMap<>(),
                "chat1",
                "uidP1", "uidR1"
        );

        chat2 = new Chat(
                "providerB", "receiverB",
                "productB", "productIdB",
                new HashMap<>(),
                "chat2",
                "uidP2", "uidR2"
        );
    }

    /**
     * Verifies that updating ChatStorage with a new chat
     * adds it to the list and persists it.
     */
    @Test
    public void updateAddsNewChatAndPersistsIt() {
        ChatStorage chatStorage =
                ChatStorage.getInstance(storageManager, chatNotifier);

        chatStorage.update(chat1);

        List<Chat> chats = chatStorage.getChats();

        assertEquals(1, chats.size());
        assertTrue(chats.contains(chat1));
    }

    /**
     * Verifies that multiple distinct chats
     * are stored correctly.
     */
    @Test
    public void updateAppendsMultipleDistinctChats() {
        ChatStorage chatStorage =
                ChatStorage.getInstance(storageManager, chatNotifier);

        chatStorage.update(chat1);
        chatStorage.update(chat2);

        List<Chat> chats = chatStorage.getChats();

        assertEquals(2, chats.size());
        assertTrue(chats.contains(chat1));
        assertTrue(chats.contains(chat2));
    }

    /**
     * Verifies that duplicate chats are not added.
     * Chat equality is determined by Chat.equals().
     */
    @Test
    public void updateDoesNotDuplicateSameChat() {
        ChatStorage chatStorage =
                ChatStorage.getInstance(storageManager, chatNotifier);

        chatStorage.update(chat1);
        chatStorage.update(chat1); // duplicate

        assertEquals(1, chatStorage.getChats().size());
    }

    /**
     * Verifies that observers receive all chats
     * when feedObservers() is called.
     */
    @Test
    public void feedObserversNotifiesAttachedObservers() {
        ChatStorage chatStorage =
                ChatStorage.getInstance(storageManager, chatNotifier);

        chatStorage.update(chat1);
        chatStorage.update(chat2);

        // Store chats received by the observer
        List<Chat> received = new ArrayList<>();

        // Attach observer
        chatStorage.attach(received::add);

        // Notify observers
        chatStorage.feedObservers();

        assertEquals(2, received.size());
        assertTrue(received.contains(chat1));
        assertTrue(received.contains(chat2));
    }

    /**
     * Verifies that when getInstance() is called again,
     * the previous ChatStorage instance is detached
     * from the notifier and the new one is attached.
     */
    @Test
    public void getInstanceDetachesPreviousInstanceFromNotifier() {
        ChatStorage first =
                ChatStorage.getInstance(storageManager, chatNotifier);

        ChatStorage second =
                ChatStorage.getInstance(storageManager, chatNotifier);

        verify(chatNotifier).detach(first);
        verify(chatNotifier, atLeastOnce()).attach(second);
    }
}