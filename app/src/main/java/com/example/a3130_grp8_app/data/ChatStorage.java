package com.example.a3130_grp8_app.data;

import android.util.Log;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.util.ChatNotifier;
import com.example.a3130_grp8_app.util.MessageNotifier;
import com.example.a3130_grp8_app.util.IChatObserver;

import java.util.ArrayList;
import java.util.List;

public class ChatStorage implements IChatObserver {

    public static final String CHATS_FILE_NAME = "chats.dat";
    StorageManager storageManager;
    List<Chat> chatUpdates;
    private ChatNotifier chatNotifier;
    List<IChatObserver> observers;

    private static ChatStorage instance;
    public static ChatStorage getInstance(StorageManager storageManager, ChatNotifier chatNotifier) {
        if(instance != null) {
            instance.getChatNotifier().detach(instance);
        }
        instance = new ChatStorage(storageManager, chatNotifier);
        return instance;
    }

    private ChatStorage(StorageManager storageManager, ChatNotifier chatNotifier) {
        this.storageManager = storageManager;
        this.chatUpdates = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.chatNotifier = chatNotifier;
        chatNotifier.attach(this);
    }
    private void saveChats(List<Chat> chats) {
        storageManager.saveObject(CHATS_FILE_NAME, chats);
    }

    public List<Chat> getChats() {
        List<Chat> currList;
        try {
            currList = (List<Chat>) storageManager.loadObject(CHATS_FILE_NAME);
        } catch (Exception e) {
            Log.e("e", "cast failed for stored object");
            return null;
        }
        return currList;
    }

    private List<Chat> updateChats(List<Chat> chats) {
        List<Chat> ret;
        if(chats == null) {
            ret = new ArrayList<Chat>();
        } else {
            ret = new ArrayList<>(List.copyOf(chats));
        }
        for(Chat chatUpdate : chatUpdates) {
            if(!ret.contains(chatUpdate)) {
                ret.add(chatUpdate);
            }
            chatUpdates.remove(chatUpdate);
        }
        saveChats(ret);
        return ret;
    }


    @Override
    public void update(Chat chat) {
        chatUpdates.add(chat);
        updateChats(getChats());
    }

    public ChatNotifier getChatNotifier() {
        return chatNotifier;
    }

    public void feedObservers() {
        for(Chat chat : getChats()) {
            updateObservers(chat);
        }
    }

    private void updateObservers(Chat chat) {
        for(IChatObserver observer : observers) {
            observer.update(chat);
        }
    }

    public void attach(IChatObserver observer) {
        observers.add(observer);
    }

    public void detach(IChatObserver observer) {
        observers.remove(observer);
    }
}
