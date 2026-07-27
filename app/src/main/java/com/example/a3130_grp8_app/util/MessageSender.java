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
     * @param sender the sender of the message
     * @param timeSent the time that the message was sent, as a String
     * @param messageString the text content of the message to be sent
     * @return the message added task
     */
    public Task<Message> sendMessage(User sender, String timeSent, String messageString) {
        if(messageString.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("Message cannot be empty"));
        }
        return FirebaseManager.getInstance().addMessageToChat(chat, sender, timeSent, messageString);
    }
}
