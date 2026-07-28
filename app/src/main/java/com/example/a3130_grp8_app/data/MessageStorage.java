package com.example.a3130_grp8_app.data;

import android.util.Log;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.IMessageObserver;
import com.example.a3130_grp8_app.util.MessageNotifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageStorage implements IMessageObserver {

    private static final String MESSAGES_FILE_NAME = "messages.dat";
    private StorageManager storageManager;
    private List<Message> messageUpdates;
    private MessageNotifier messageNotifier;
    Chat currChat;
    private List<IMessageObserver> observers;

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
        this.observers = new ArrayList<>();
        messageNotifier.attach(this);
    }

    private void saveMessages(HashMap<String, List<Message>> messages) {
        storageManager.saveObject(MESSAGES_FILE_NAME, messages);
    }

    private HashMap<String, List<Message>> updateMessages() {
        HashMap<String, List<Message>> ret = getMessages();
        if(ret == null) {
            ret = new HashMap<>();
        }
        for(Message message : messageUpdates) {
            if(ret.containsKey(currChat.getId())) {
                ret.get(currChat.getId()).add(message);
            } else {
                ret.put(currChat.getId(), new ArrayList<>(List.of(message)));
            }
            messageUpdates.remove(message);
        }
        saveMessages(ret);
        return ret;
    }

    public HashMap<String, List<Message>> getMessages() {
        HashMap<String, List<Message>> currMap;
        try {
            currMap = (HashMap<String, List<Message>>) storageManager.loadObject(MESSAGES_FILE_NAME);
        } catch (Exception e) {
            Log.e("e", "Cast failed for stored object");
            return null;
        }

        return currMap;
    }

    public MessageNotifier getMessageNotifier() {
        return messageNotifier;
    }

    public void update(Message message) {
        messageUpdates.add(message);
        updateMessages();
    }

    private void updateObservers(Message message) {
        for(IMessageObserver observer : observers) {
            observer.update(message);
        }
    }

    public void attach(IMessageObserver observer) {
        observers.add(observer);
    }

    public void detach(IMessageObserver observer) {
        observers.add(observer);
    }

    public void feedObservers(String chatId) {
        HashMap<String, List<Message>> messages = getMessages();
        if(!messages.containsKey(chatId)) {
            Log.e("e", "Not able to get chat for key " + chatId);
            return;
        }
        for(Message message : messages.get(chatId)) {
            updateObservers(message);
        }
    }
}
