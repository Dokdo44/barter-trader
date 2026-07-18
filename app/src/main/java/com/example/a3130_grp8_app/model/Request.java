package com.example.a3130_grp8_app.model;

import java.util.List;

/**
 * Represents a request made by a receiver for a product
 */
public class Request {
    private String requestId;
    private String productId;
    private String receiverEmail;
    private String receiverUid;
    private String status;
    private long timestamp;
    private String requesterName;
    private String requesterLocation;
    private List<String> offeredItems;

    public Request() {}

    public Request(String requestId, String productId, String receiverEmail, String receiverUid,
                   String requesterName, String requesterLocation, List<String> offeredItems) {
        this.requestId = requestId;
        this.productId = productId;
        this.receiverEmail = receiverEmail;
        this.receiverUid = receiverUid;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
        this.requesterName = requesterName;
        this.requesterLocation = requesterLocation;
        this.offeredItems = offeredItems;
    }

    // used by FirebaseManager - requestId/receiverEmail set separately via setters
    public Request(String productId, String receiverUid, String requesterName,
                   String requesterLocation, List<String> offeredItems) {
        this.productId = productId;
        this.receiverUid = receiverUid;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
        this.requesterName = requesterName;
        this.requesterLocation = requesterLocation;
        this.offeredItems = offeredItems;
    }

    // getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getReceiverUid() { return receiverUid; }
    public void setReceiverUid(String receiverUid) { this.receiverUid = receiverUid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterLocation() { return requesterLocation; }
    public void setRequesterLocation(String requesterLocation) { this.requesterLocation = requesterLocation; }

    public List<String> getOfferedItems() { return offeredItems; }
    public void setOfferedItems(List<String> offeredItems) { this.offeredItems = offeredItems; }

}