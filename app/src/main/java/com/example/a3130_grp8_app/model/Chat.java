package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Chat implements Serializable {
    private String id;
    private User provider;
    private User receiver;
    private Product product;

    private List<Message> messages;

    public Chat(User provider, User receiver, Product product, List<Message> messages) {
        this.provider = provider;
        this.receiver = receiver;
        this.messages = messages;
        this.product = product;
    }

    public Chat() {}

    public List<Message> getMessages() {
        return messages;
    }

    public User getReceiver() {
        return receiver;
    }

    public User getProvider() {
        return provider;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return this.id.equals(chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
