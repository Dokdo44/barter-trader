package com.example.a3130_grp8_app.model;

public class Request {
    private String productId;
    private String receiverUid;
    private String status;
    private long timestamp;

    public Request() {
    }

    public Request(String productId, String receiverUid) {
        this.productId = productId;
        this.receiverUid = receiverUid;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    public String getProductId() { return productId; }
    public String getReceiverUid() { return receiverUid; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setReceiverUid(String receiverUid) { this.receiverUid = receiverUid; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
