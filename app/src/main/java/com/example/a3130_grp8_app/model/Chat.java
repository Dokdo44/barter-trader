package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Chat implements Serializable {
    private String id;
    private String providerName;
    private String receiverName;
    private String productName;
    private String productId;
    private String providerUid;
    private String receiverUid;

    private List<String> messageIds;

    public Chat(String providerName,
                String receiverName,
                String productName,
                String productId,
                List<String> messageIds,
                String id,
                String providerUid,
                String receiverUid) {
        this.providerName = providerName;
        this.receiverName = receiverName;
        this.messageIds = messageIds;
        this.productName = productName;
        this.id = id;
        this.providerUid = providerUid;
        this.receiverUid = receiverUid;
        this.productId = productId;
    }

    public Chat() {}

    public List<String> getMessageIds() {
        return messageIds;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getProviderName() {
        return providerName;
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

    public String getProductName() {
        return productName;
    }

    public void addMessageId(String messageId) {
        messageIds.add(messageId);
    }

    public String getProviderUid() {
        return providerUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getProductId() {
        return productId;
    }
}
