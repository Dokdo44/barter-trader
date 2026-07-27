package com.example.a3130_grp8_app.data;

import android.content.Context;
import android.util.Log;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.ChatNotifier;
import com.example.a3130_grp8_app.util.IObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatStorage implements IObserver<Message> {

    public static final String CHATS_FILE_NAME = "chats.dat";
    StorageManager storageManager;
    List<Message> messageUpdates;
    private ChatNotifier chatNotifier;
    Chat currChat;

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
        this.messageUpdates = new ArrayList<>();
        this.chatNotifier = chatNotifier;
        this.currChat = chatNotifier.getChat();
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

        List<Chat> ret = updateChats(currList);
        return ret;
    }

    private List<Chat> updateChats(List<Chat> chats) {
        List<Chat> updatedChats = new ArrayList<>(chats);

        int index = updatedChats.indexOf(currChat);
        if(index < 0) {
            updatedChats.add(currChat);
            index = updatedChats.size() - 1;
        }
        for(int i = 0; i < messageUpdates.size(); i++) {
            Map<String, Message> chatMessages = updatedChats.get(index).getMessages();
            if(!chatMessages.containsValue(messageUpdates.get(i))) {
                updatedChats.get(index).addMessage(messageUpdates.get(i));
            }
        }

        saveChats(updatedChats);
        return updatedChats;
    }


    @Override
    public void sendUpdate(Message message) {
        messageUpdates.add(message);
    }

    public ChatNotifier getChatNotifier() {
        return chatNotifier;
    }
}
