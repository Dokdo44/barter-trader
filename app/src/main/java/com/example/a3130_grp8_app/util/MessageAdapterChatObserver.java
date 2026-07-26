package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapterChatObserver implements IObserver<Message> {

    ChatNotifier chatNotifier;
    ChatStorage chatStorage;
    MessagesAdapter messagesAdapter;

    public MessageAdapterChatObserver(ChatNotifier chatNotifier, ChatStorage chatStorage, MessagesAdapter messagesAdapter) {
        this.chatNotifier = chatNotifier;
        this.chatStorage = chatStorage;
        this.messagesAdapter = messagesAdapter;
        chatNotifier.attach(this);
    }

    @Override
    public void sendUpdate(Message update) {

    }

    public MessagesAdapter getMessagesAdapter() {
        return messagesAdapter;
    }
}
