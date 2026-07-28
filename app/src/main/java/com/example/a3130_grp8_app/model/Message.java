package com.example.a3130_grp8_app.model;

import java.io.Serializable;

public class Message implements Serializable {

    private String id;
    private String senderName;
    private String timeSent;
    private String messageString;
    private String chatId;

    public Message(String senderName, String timeSent, String messageString, String chatId, String id) {
        this.senderName = senderName;
        this.timeSent = timeSent;
        this.messageString = messageString;
        this.id = id;
        this.chatId = chatId;
    }

    public Message() {}

    public String getSenderName() {
        return senderName;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public String getMessageString() {
        return messageString;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message otherMessage = (Message) o;
        return this.id.equals(otherMessage.id);
    }

    public String getChatId() {
        return chatId;
    }
}
