package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chat implements Serializable {
    private String id;
    private String providerName;
    private String receiverName;
    private String productName;
    private String productId;
    private String providerUid;
    private String receiverUid;

    private Map<String, String> messageIds;

    /**
     * Creates a new Chat with the specified participants, product information,
     * and associated messages.
     *
     * @param providerName the display name of the provider
     * @param receiverName the display name of the receiver
     * @param productName  the name of the product being discussed
     * @param productId    the unique identifier of the product
     * @param messageIds   a map containing the IDs of messages in the chat
     * @param id           the unique identifier of the chat
     * @param providerUid  the unique user ID of the provider
     * @param receiverUid  the unique user ID of the receiver
     */
    public Chat(String providerName,
                String receiverName,
                String productName,
                String productId,
                Map<String, String> messageIds,
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

    /**
     * Creates an empty Chat instance.
     *
     * <p>This constructor is primarily intended for Firebase object
     * deserialization.</p>
     */
    public Chat() {
    }

    /**
     * Returns the map of message IDs associated with this chat.
     *
     * @return a map whose keys and values are the message IDs in the chat
     */
    public Map<String, String> getMessageIds() {
        return messageIds;
    }

    /**
     * Returns the display name of the receiver.
     *
     * @return the receiver's name
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * Returns the display name of the provider.
     *
     * @return the provider's name
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Returns the unique identifier of this chat.
     *
     * @return the chat ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this chat.
     *
     * @param id the chat ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Compares this chat to another object for equality.
     *
     * <p>Two Chat objects are considered equal if they have the same chat ID.</p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects represent the same chat;
     * {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return this.id.equals(chat.id);
    }

    /**
     * Returns the hash code for this chat.
     *
     * <p>The hash code is computed from the chat ID.</p>
     *
     * @return the hash code for this chat
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns the name of the product associated with this chat.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Adds a message ID to this chat.
     *
     * @param messageId the ID of the message to add
     */
    public void addMessageId(String messageId) {
        messageIds.put(messageId, messageId);
    }

    /**
     * Returns the unique user ID of the provider.
     *
     * @return the provider's user ID
     */
    public String getProviderUid() {
        return providerUid;
    }

    /**
     * Returns the unique user ID of the receiver.
     *
     * @return the receiver's user ID
     */
    public String getReceiverUid() {
        return receiverUid;
    }

    /**
     * Returns the unique identifier of the product associated with this chat.
     *
     * @return the product ID
     */
    public String getProductId() {
        return productId;
    }
}