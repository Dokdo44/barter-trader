package com.example.a3130_grp8_app;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.a3130_grp8_app.data.MessageStorage;
import com.example.a3130_grp8_app.data.StorageManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.IMessageObserver;
import com.example.a3130_grp8_app.util.MessageNotifier;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageStorageTest {

    StorageManager storageManager;
    MessageNotifier messageNotifier;
    Map<String, Object> fakeStore;
    Chat chat;
    Message message1;
    Message message2;

    @Before
    public void setUp() {
        storageManager = mock(StorageManager.class);
        messageNotifier = mock(MessageNotifier.class);

        chat = new Chat("provider", "receiver", "product", "productId",
                new HashMap<>(), "chat1", "uidP", "uidR");
        when(messageNotifier.getChat()).thenReturn(chat);

        fakeStore = new HashMap<>();
        doAnswer(invocation -> {
            fakeStore.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(storageManager).saveObject(any(), any());
        when(storageManager.loadObject(any()))
                .thenAnswer(invocation -> fakeStore.get(invocation.getArgument(0)));

        message1 = new Message("sender", "2026-07-29T09:00:00", "Hey", "chat1", "m1");
        message2 = new Message("sender", "2026-07-29T09:05:00", "Still there?", "chat1", "m2");
    }

    @Test
    public void updateStoresMessageUnderChatId() {
        MessageStorage messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);

        messageStorage.update(message1);

        Map<String, List<Message>> messages = messageStorage.getMessages();
        assertNotNull(messages);
        assertEquals(List.of(message1), messages.get("chat1"));
    }

    @Test
    public void updateAppendsSecondMessageToSameChat() {
        MessageStorage messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);

        messageStorage.update(message1);
        messageStorage.update(message2);

        assertEquals(List.of(message1, message2), messageStorage.getMessages().get("chat1"));
    }

    @Test
    public void feedObserversNotifiesAttachedObserversForChat() {
        MessageStorage messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);
        messageStorage.update(message1);
        messageStorage.update(message2);

        List<Message> received = new ArrayList<>();
        messageStorage.attach(received::add);

        messageStorage.feedObservers("chat1");

        assertEquals(List.of(message1, message2), received);
    }

    @Test
    public void detachStopsObserverFromReceivingUpdates() {
        MessageStorage messageStorage = MessageStorage.getInstance(storageManager, messageNotifier);
        messageStorage.update(message1);

        List<Message> received = new ArrayList<>();
        IMessageObserver observer = received::add;

        messageStorage.attach(observer);
        messageStorage.detach(observer);

        messageStorage.feedObservers("chat1");

        assertTrue(received.isEmpty());
    }
}