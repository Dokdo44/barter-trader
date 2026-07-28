package com.example.a3130_grp8_app.model;

import java.util.List;

public class Transaction {

    private String productName;
    private String productAddress;
    private String requesterName;
    private String requesterLocation;
    private List<String> offeredItems;
    private String status;
    private long timestamp;

    public Transaction(String productName, String productAddress, String requesterName, String requesterLocation, List<String> offeredItems, String status, long timestamp) {
        this.productName = productName;
        this.productAddress = productAddress;
        this.requesterName = requesterName;
        this.requesterLocation = requesterLocation;
        this.offeredItems = offeredItems;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public String getRequesterLocation() {
        return requesterLocation;
    }

    public List<String> getOfferedItems() {
        return offeredItems;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}