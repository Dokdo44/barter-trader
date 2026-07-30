package com.example.a3130_grp8_app.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Chat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class ChatNotifier {

    private List<IChatObserver> chatObservers;

    public ChatNotifier(String userId) {
        chatObservers = new ArrayList<>();
        FirebaseManager.getInstance().addChildEventListenerToUserChats(userId, chatChildEventListener);
    }


    private ChildEventListener chatChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Chat chat = snapshot.getValue(Chat.class);
            if(chat == null) {
                Log.e("e", "Could not get chat from snapshot");
                return;
            }
            updateObservers(chat);
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

    private void updateObservers(Chat chat) {
        for(IChatObserver observer : chatObservers) {
            observer.update(chat);
        }
    }

    public void attach(IChatObserver observer) {
        chatObservers.add(observer);
    }

    public void detach(IChatObserver observer) {
        chatObservers.remove(observer);
    }
}
