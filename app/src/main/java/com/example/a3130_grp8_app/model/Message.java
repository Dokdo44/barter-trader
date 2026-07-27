package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private String id;
    private User sender;
    private String timeSent;
    private String messageString;

    public Message(User sender, String timeSent, String messageString, String id) {
        this.sender = sender;
        this.timeSent = timeSent;
        this.messageString = messageString;
        this.id = id;
    }

    public Message() {}

    public User getSender() {
        return sender;
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
}
