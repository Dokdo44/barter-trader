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

    /**
     * Creates a new product request with the specified details.
     *
     * @param requestId the unique ID of the request
     * @param productId the ID of the requested product
     * @param receiverEmail the email address of the product owner
     * @param receiverUid the UID of the product owner
     * @param requesterName the name of the requester
     * @param requesterLocation the requester's location
     * @param offeredItems the items offered in exchange
     */
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

    /**
     * Creates a new product request without a request ID or receiver email.
     *
     * Mainly used by FirebaseManager when these values are assigned separately through setters.
     *
     * @param productId the ID of the requested product
     * @param receiverUid the UID of the product owner
     * @param requesterName the name of the requester
     * @param requesterLocation the requester's location
     * @param offeredItems the items offered in exchange
     */
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

    /**
     * Returns the request ID.
     *
     * @return the request ID
     */
    public String getRequestId() { return requestId; }

    /**
     * Sets the request ID.
     *
     * @param requestId the request ID
     */
    public void setRequestId(String requestId) { this.requestId = requestId; }

    /**
     * Returns the ID of the requested product.
     *
     * @return the product ID
     */
    public String getProductId() { return productId; }

    /**
     * Sets the ID of the requested product.
     *
     * @param productId the product ID
     */
    public void setProductId(String productId) { this.productId = productId; }

    /**
     * Returns the email address of the product owner.
     *
     * @return the receiver email address
     */
    public String getReceiverEmail() { return receiverEmail; }

    /**
     * Sets the email address of the product owner.
     *
     * @param receiverEmail the receiver email address
     */
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    /**
     * Returns the UID of the product owner.
     *
     * @return the receiver UID
     */
    public String getReceiverUid() { return receiverUid; }

    /**
     * Sets the UID of the product owner.
     *
     * @param receiverUid the receiver UID
     */
    public void setReceiverUid(String receiverUid) { this.receiverUid = receiverUid; }

    /**
     * Returns the current status of the request.
     *
     * @return the request status
     */
    public String getStatus() { return status; }

    /**
     * Sets the current status of the request.
     *
     * @param status the request status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns the timestamp of the request.
     *
     * @return the request timestamp
     */
    public long getTimestamp() { return timestamp; }

    /**
     * Sets the timestamp of the request.
     *
     * @param timestamp the request timestamp
     */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /**
     * Returns the name of the requester.
     *
     * @return the requester name
     */
    public String getRequesterName() { return requesterName; }

    /**
     * Sets the name of the requester.
     *
     * @param requesterName the requester name
     */
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    /**
     * Returns the location of the requester.
     *
     * @return the requester location
     */
    public String getRequesterLocation() { return requesterLocation; }

    /**
     * Sets the location of the requester.
     *
     * @param requesterLocation the requester location
     */
    public void setRequesterLocation(String requesterLocation) { this.requesterLocation = requesterLocation; }

    /**
     * Returns the items offered by the requester.
     *
     * @return the offered items
     */
    public List<String> getOfferedItems() { return offeredItems; }

    /**
     * Sets the items offered by the requester.
     *
     * @param offeredItems the offered items
     */
    public void setOfferedItems(List<String> offeredItems) { this.offeredItems = offeredItems; }
}