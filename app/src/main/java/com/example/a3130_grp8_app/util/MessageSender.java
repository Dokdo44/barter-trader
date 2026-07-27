package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MessageSender {
    User sender;
    Chat chat;

    public MessageSender(User sender, Chat chat) {
        this.sender = sender;
        this.chat = chat;
    }

    /**
     * Add the message to the chat
     * @param timeSent the time that the message was sent, as a String
     * @param messageString the text content of the message to be sent
     * @return the message added task
     */
    public Task<Message> sendMessage(String timeSent, String messageString) {
        if(messageString.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("Message cannot be empty"));
        }

        Task<Message> addMessageToChatTask = FirebaseManager
                .getInstance()
                .addMessageToChat(chat, sender, timeSent, messageString);

        return addMessageToChatTask.continueWithTask(task -> {
            if(!task.isSuccessful() && task.getException() != null) {
                return Tasks.forException(task.getException());
            }

            Message message = task.getResult();
            if(sender.getId().equals(chat.getProvider().getId())) {
                return FirebaseManager
                        .getInstance()
                        .addMessageToUserInbox(chat.getReceiver(), message);
            } else {
                return FirebaseManager
                        .getInstance()
                        .addMessageToUserInbox(chat.getProvider(), message);
            }
        });
    }
}
