package com.example.a3130_grp8_app.data;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.IMessageObserver;
import com.example.a3130_grp8_app.util.MessageNotifier;

import java.util.ArrayList;
import java.util.List;

public class MessageStorage implements IMessageObserver {

    private StorageManager storageManager;
    private List<Message> messageUpdates;
    private MessageNotifier messageNotifier;
    Chat currChat;

    private static MessageStorage instance;
    public static MessageStorage getInstance(StorageManager storageManager, MessageNotifier messageNotifier) {
        if(instance != null) {
            instance.getMessageNotifier().detach(instance);
        }
        instance = new MessageStorage(storageManager, messageNotifier);
        return instance;
    }

    private MessageStorage(StorageManager storageManager, MessageNotifier messageNotifier) {
        this.storageManager = storageManager;
        this.messageUpdates = new ArrayList<>();
        this.messageNotifier = messageNotifier;
        this.currChat = messageNotifier.getChat();
        messageNotifier.attach(this);
    }

    public MessageNotifier getMessageNotifier() {
        return messageNotifier;
    }

    public void update(Message message) {
        messageUpdates.add(message);
    }
}
