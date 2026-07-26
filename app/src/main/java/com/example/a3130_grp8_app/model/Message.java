package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Serializable {

    private User sender;
    private LocalDateTime timeSent;
    private String message;

    public Message(User sender, String timeSent, String message) {
        this.sender = sender;
        this.timeSent = LocalDateTime.parse(timeSent);
        this.message = message;
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
}
