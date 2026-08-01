package com.example.a3130_grp8_app.model;

import java.io.Serializable;

public class Message implements Serializable {

    private String id;
    private String senderName;
    private String timeSent;
    private String messageString;
    private String chatId;

    /**
     * Creates a new Message with the specified sender, timestamp, content,
     * associated chat, and unique identifier.
     *
     * @param senderName the display name of the user who sent the message
     * @param timeSent the time at which the message was sent
     * @param messageString the text content of the message
     * @param chatId the unique identifier of the chat containing this message
     * @param id the unique identifier of the message
     */
    public Message(String senderName, String timeSent, String messageString, String chatId, String id) {
        this.senderName = senderName;
        this.timeSent = timeSent;
        this.messageString = messageString;
        this.id = id;
        this.chatId = chatId;
    }

    /**
     * Creates an empty Message instance.
     *
     * <p>This constructor is primarily intended for Firebase object
     * deserialization.</p>
     */
    public Message() {}

    /**
     * Returns the name of the user who sent the message.
     *
     * @return the sender's display name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Returns the time when the message was sent.
     *
     * @return the message timestamp
     */
    public String getTimeSent() {
        return timeSent;
    }

    /**
     * Returns the text content of the message.
     *
     * @return the message text
     */
    public String getMessageString() {
        return messageString;
    }

    /**
     * Returns the unique identifier of this message.
     *
     * @return the message ID
     */
    public String getId() {
        return id;
    }

    /**
     * Compares this message to another object for equality.
     *
     * <p>Two Message objects are considered equal if they have the same
     * message ID.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if both objects represent the same message;
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message otherMessage = (Message) o;
        return this.id.equals(otherMessage.id);
    }

    /**
     * Returns the unique identifier of the chat containing this message.
     *
     * @return the chat ID
     */
    public String getChatId() {
        return chatId;
    }
}