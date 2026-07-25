package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private String id;
    private User sender;
    private LocalDateTime timeSent;
    private String message;

    public Message(User sender, String timeSent, String message, String id) {
        this.sender = sender;
        this.timeSent = LocalDateTime.parse(timeSent);
        this.message = message;
        this.id = id;
    }

    public Message() {}

    public User getSender() {
        return sender;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }
}
