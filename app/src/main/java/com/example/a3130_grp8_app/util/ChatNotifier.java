package com.example.a3130_grp8_app.util;

import androidx.lifecycle.Observer;

import com.example.a3130_grp8_app.model.Message;

import java.util.List;

public class ChatNotifier {
    List<Observer<Message>> messageObservers;

    public void attach(Observer<Message> messageObserver) {
        messageObservers.add(messageObserver);
    }


}
