package com.example.a3130_grp8_app.util;

import androidx.lifecycle.Observer;

import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Message;
import com.google.firebase.database.ChildEventListener;

import java.util.List;

public class ChatNotifier {
    List<Observer<Chat>> chatObservers;
    public void attach(Observer<Chat> chatObserver) {
        chatObservers.add(chatObserver);
    }
}
