package com.example.a3130_grp8_app.model;

/**
 * Represents a request made by a receiver for a product
 */
public class Request {
    private String requestId;
    private String productId;
    private String receiverEmail;
    private String receiverUid;
    private String status;

    // empty constructor for Firebase
    public Request() {
    }

    public Request(String requestId, String productId, String receiverEmail, String receiverUid) {
        this.requestId = requestId;
        this.productId = productId;
        this.receiverEmail = receiverEmail;
        this.receiverUid = receiverUid;
        this.status = "pending";
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
}