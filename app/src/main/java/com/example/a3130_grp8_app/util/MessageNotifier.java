package com.example.a3130_grp8_app.util;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class MessageNotifier {
    List<IMessageObserver> messageObservers;
    Chat chat;

    private final ChildEventListener chatMessageChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String messageId = snapshot.getValue(String.class);
            if(messageId != null) {
                FirebaseManager.getInstance().getMessageById(messageId)
                                .addOnSuccessListener(message -> updateObservers(message));
            } else {
                Log.e("e", "Failed to get value of message snapshot");
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public MessageNotifier(Chat chat) {
        this.chat = chat;
        this.messageObservers = new ArrayList<>();
        initFirebase();
    }

    private void initFirebase() {
        FirebaseManager.getInstance().addChildEventListenerForChatMessages(chat.getId(), chatMessageChildEventListener);
    }

    private void updateObservers(Message message) {
        for(IMessageObserver observer : messageObservers) {
            observer.update(message);
        }
    }

    public void attach(IMessageObserver chatObserver) {
        messageObservers.add(chatObserver);
    }

    public void detach(IMessageObserver chatObserver) {
        messageObservers.remove(chatObserver);
    }

    public Chat getChat() {
        return chat;
    }
}
