package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
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

import java.time.LocalDateTime;
import java.util.HashMap;
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

    int messageId;

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager = mock(FirebaseManager.class);
        doAnswer(invocation -> {
            Chat chat = invocation.getArgument(0);
            User sender = invocation.getArgument(1);
            String timeSent = invocation.getArgument(2);
            String messageString = invocation.getArgument(3);

            Message message = new Message(sender, timeSent, messageString, "1");
            chat.addMessage(message);
            return Tasks.forResult(message);
        }).when(mockManager).addMessageToChat(any(), any(), any(), any());

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            Message message = invocation.getArgument(1);

            user.getMessageInbox().put(message.getId(), message);
            return Tasks.forResult(message);
        }).when(mockManager).addMessageToUserInbox(any(), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        provider = new User("provider@email.com", "seller");
        provider.setId("1");
        provider.setMessageInbox(new HashMap<>());

        receiver = new User("receiver@email.com", "buyer");
        receiver.setId("2");
        receiver.setMessageInbox(new HashMap<>());

        chat = new Chat(provider, receiver, mock(Product.class), new HashMap<>(), "1");
    }

    @Test
    public void testMessageSendsToChatAndInboxProvider() throws ExecutionException, InterruptedException, TimeoutException {
        MessageSender messageSender = new MessageSender(provider, chat);
        Task<Message> sendMessageTask = messageSender.sendMessage("text", "This is a test!");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() -> Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        // Flush Robolectric's main-thread queue so the task's internal
        // callbacks (which likely post back to main) actually run
        shadowOf(Looper.getMainLooper()).idle();

        future.get(5, TimeUnit.SECONDS); // rethrows any exception from await

        Message expected = new Message(provider, "test", "This is a test!", "1");
        HashMap<String, Message> expectedMessageHashMap = new HashMap<>();
        expectedMessageHashMap.put(expected.getId(), expected);

        assertEquals(chat.getMessages(), expectedMessageHashMap);
        assertEquals(receiver.getMessageInbox(), expectedMessageHashMap);
    }

    @Test
    public void testMessageSendsToChatAndInboxReceiver() throws ExecutionException, InterruptedException, TimeoutException {
        MessageSender messageSender = new MessageSender(receiver, chat);
        Task<Message> sendMessageTask = messageSender.sendMessage("text", "This is a test!");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Message> future = executor.submit(() -> Tasks.await(sendMessageTask, 5, TimeUnit.SECONDS));

        // Flush Robolectric's main-thread queue so the task's internal
        // callbacks (which likely post back to main) actually run
        shadowOf(Looper.getMainLooper()).idle();

        future.get(5, TimeUnit.SECONDS); // rethrows any exception from await

        Message expected = new Message(receiver, "test", "This is a test!", "1");
        HashMap<String, Message> expectedMessageHashMap = new HashMap<>();
        expectedMessageHashMap.put(expected.getId(), expected);

        assertEquals(chat.getMessages(), expectedMessageHashMap);
        assertEquals(provider.getMessageInbox(), expectedMessageHashMap);
    }
}
