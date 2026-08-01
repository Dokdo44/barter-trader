package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.adapters.ChatsListItemAdapter;
import com.example.a3130_grp8_app.adapters.MessagesAdapter;
import com.example.a3130_grp8_app.data.ChatStorage;
import com.example.a3130_grp8_app.model.Chat;

public class ChatAdapterChatObserver implements IChatObserver {


    ChatNotifier chatNotifier;
    ChatStorage chatStorage;
    ChatsListItemAdapter chatsListItemAdapter;

    public ChatAdapterChatObserver(ChatNotifier chatNotifier, ChatStorage chatStorage, ChatsListItemAdapter chatsListItemAdapter) {
        this.chatNotifier = chatNotifier;
        this.chatStorage = chatStorage;
        this.chatsListItemAdapter = chatsListItemAdapter;
    }

    /**
     * On a chat update, add it to the adapter
     * @param chat the message update
     */
    @Override
    public void update(Chat chat) {
        chatsListItemAdapter.addChat(chatsListItemAdapter.getItemCount(), chat);
    }

    public ChatsListItemAdapter getChatsListItemAdapter() {
        return chatsListItemAdapter;
    }

    public void attachToChatNotifier() {
        chatStorage.detach(this);
        chatNotifier.attach(this);
    }

    public void attachToChatStorage() {
        chatNotifier.detach(this);
        chatStorage.attach(this);
    }

}



