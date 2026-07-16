package com.example.a3130_grp8_app.model;

import java.util.List;

public class Request {
    private String productId;
    private String receiverUid;
    private String status;
    private long timestamp;
    private String requesterName;
    private String requesterLocation;
    private List<String> offeredItems;

    public Request() {}

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

    public String getProductId() { return productId; }
    public String getReceiverUid() { return receiverUid; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getRequesterName() { return requesterName; }
    public String getRequesterLocation() { return requesterLocation; }
    public List<String> getOfferedItems() { return offeredItems; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setReceiverUid(String receiverUid) { this.receiverUid = receiverUid; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public void setRequesterLocation(String requesterLocation) { this.requesterLocation = requesterLocation; }
    public void setOfferedItems(List<String> offeredItems) { this.offeredItems = offeredItems; }
}
