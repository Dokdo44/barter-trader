package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.Task;

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
     * @param message the message that each recipient will recieve
     * @return the message added task
     */
    public Task<Void> sendMessage(Message message) {
        return FirebaseManager.getInstance().addMessageToChat(chat, message);
    }
}
