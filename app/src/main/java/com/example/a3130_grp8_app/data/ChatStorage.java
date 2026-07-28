package com.example.a3130_grp8_app.data;

import android.util.Log;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.example.a3130_grp8_app.util.MessageNotifier;
import com.example.a3130_grp8_app.util.IChatObserver;
import com.example.a3130_grp8_app.util.IMessageObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatStorage implements IMessageObserver {

    public static final String CHATS_FILE_NAME = "chats.dat";
    public static final String MESSAGES_FILE_NAME = "messages.dat";
    StorageManager storageManager;
    List<Message> messageUpdates;
    private MessageNotifier messageNotifier;
    Chat currChat;

    private static ChatStorage instance;
    public static ChatStorage getInstance(StorageManager storageManager, MessageNotifier messageNotifier) {
        if(instance != null) {
            instance.getChatNotifier().detach(instance);
        }
        instance = new ChatStorage(storageManager, messageNotifier);
        return instance;
    }

    private ChatStorage(StorageManager storageManager, MessageNotifier messageNotifier) {
        this.storageManager = storageManager;
        this.messageUpdates = new ArrayList<>();
        this.messageNotifier = messageNotifier;
        this.currChat = messageNotifier.getChat();
        messageNotifier.attach(this);
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

    public HashMap<String, Message> getMessages() {
        HashMap<String, Message> currMap;
        try {
            currMap = (HashMap<String, Message>) storageManager.loadObject(MESSAGES_FILE_NAME);
        } catch (Exception e) {
            Log.e("e", "Cast failed for stored object");
            return null;
        }

        return currMap;
    }

    private HashMap<String, Message> updateMessages(HashMap<String, Message> messages) {
        return messages;
    }

    private List<Chat> updateChats(List<Chat> chats) {
        List<Chat> updatedChats = new ArrayList<>(chats);

        int index = updatedChats.indexOf(currChat);
        if(index < 0) {
            updatedChats.add(currChat);
            index = updatedChats.size() - 1;
        }
        for(int i = 0; i < messageUpdates.size(); i++) {
            Map<String, String> chatMessageIds = updatedChats.get(index).getMessageIds();
            if(!chatMessageIds.containsValue(messageUpdates.get(i).getId())) {
                updatedChats.get(index).addMessageId(messageUpdates.get(i).getId());
            }
        }

        saveChats(updatedChats);
        return updatedChats;
    }


    @Override
    public void update(Message message) {
        messageUpdates.add(message);
    }

    public MessageNotifier getChatNotifier() {
        return messageNotifier;
    }
}
