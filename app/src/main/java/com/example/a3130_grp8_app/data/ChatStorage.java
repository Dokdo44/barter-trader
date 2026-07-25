package com.example.a3130_grp8_app.data;

import android.content.Context;
import android.util.Log;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.IObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatStorage implements IObserver<Chat> {

    public static final String CHATS_FILE_NAME = "chats.dat";
    StorageManager storageManager;
    List<Chat> chatUpdates;

    public ChatStorage(StorageManager storageManager) {
        this.storageManager = storageManager;
        this.chatUpdates = new ArrayList<>();
    }
    private void saveChats(List<Chat> chats) {
        storageManager.saveObject(CHATS_FILE_NAME, chats);
    }

    public List<Chat> getChats() {
        try {
            return (List<Chat>) storageManager.loadObject(CHATS_FILE_NAME);
        } catch (Exception e) {
            Log.e("e", "cast failed for stored object");
            return null;
        }
    }

    private void updateChats(List<Chat> chats) {

    }

    @Override
    public void sendUpdate(Chat chat) {
        chatUpdates.add(chat);
    }
}
