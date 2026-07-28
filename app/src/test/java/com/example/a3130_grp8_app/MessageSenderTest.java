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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(RobolectricTestRunner.class)
public class MessageSenderTest {

    User provider;
    User receiver;
    Chat chat;
    FirebaseManager mockManager;

    @Before
    public void setUp() {
        provider = new User("provider@email.com", "seller");
        provider.setId("1");
        provider.setMessageInbox(new HashMap<>());

        receiver = new User("receiver@email.com", "buyer");
        receiver.setId("2");
        receiver.setMessageInbox(new HashMap<>());

        Product productMock = mock(Product.class);

        List<String> messageIds = new ArrayList<>();
        chat = new Chat(
                provider.getEmail(),   // providerName
                receiver.getEmail(),   // receiverName
                "productName",
                "productId",
                messageIds,
                "1",                   // chat id
                provider.getId(),      // providerUid
                receiver.getId());     // receiverUid

        mockManager = mock(FirebaseManager.class);

        // Mirrors FirebaseManager.addMessageToChat(chatId, senderName, timeSent, messageString)
        doAnswer(invocation -> {
            String chatId = invocation.getArgument(0);
            String senderName = invocation.getArgument(1);
            String timeSent = invocation.getArgument(2);
            String messageString = invocation.getArgument(3);

            Message message = new Message(senderName, timeSent, messageString, chatId, "m1");
            chat.getMessageIds().add(message.getId());
            return Tasks.forResult(message);
        }).when(mockManager).addMessageToChat(eq("1"), any(), any(), any());

        // Mirrors FirebaseManager.addMessageToUserInbox(userId, messageId)
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            String messageId = invocation.getArgument(1);

            Map<String, String> inbox = userId.equals(provider.getId())
                    ? provider.getMessageInbox()
                    : receiver.getMessageInbox();
            inbox.put(messageId, chat.getId());
            return Tasks.forResult(null);
        }).when(mockManager).addMessageToUserInbox(any(), any());

        FirebaseManager.install(mockManager);
    }

    @Test
    public void testMessageSendsToChatAndInboxReceiver() throws ExecutionException, InterruptedException, TimeoutException {
        MessageSender messageSender = new MessageSender(provider.getId(), provider.getEmail(), chat);
        Task<Message> sendMessageTask = messageSender.sendMessage("test", "This is a test!");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() -> Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        shadowOf(Looper.getMainLooper()).idle();
        Message sent = future.get(5, TimeUnit.SECONDS);

        assertTrue(chat.getMessageIds().contains(sent.getId()));
        assertEquals(chat.getId(), receiver.getMessageInbox().get(sent.getId()));
        assertTrue(provider.getMessageInbox().isEmpty());
    }

    @Test
    public void testMessageSendsToChatAndInboxProvider() throws ExecutionException, InterruptedException, TimeoutException {
        MessageSender messageSender = new MessageSender(receiver.getId(), receiver.getEmail(), chat);
        Task<Message> sendMessageTask = messageSender.sendMessage("test", "This is a test!");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() -> Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        shadowOf(Looper.getMainLooper()).idle();
        Message sent = future.get(5, TimeUnit.SECONDS);

        assertTrue(chat.getMessageIds().contains(sent.getId()));
        assertEquals(chat.getId(), provider.getMessageInbox().get(sent.getId()));
        assertTrue(receiver.getMessageInbox().isEmpty());
    }
}