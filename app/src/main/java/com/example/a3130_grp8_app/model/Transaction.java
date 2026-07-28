package com.example.a3130_grp8_app.model;

import java.util.List;

public class Transaction {

    private String productName;
    private String productDescription;
    private String productAddress;

    private String sellerName;
    private String buyerName;
    private String buyerLocation;

    private List<String> offeredItems;
    private String status;
    private long timestamp;

    public Transaction() {
        // Required empty constructor for Firebase
    }

    public Transaction(
            String productName, String productDescription, String productAddress,
            String sellerName, String buyerName, String buyerLocation,
            List<String> offeredItems, String status, long timestamp) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productAddress = productAddress;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.buyerLocation = buyerLocation;
        this.offeredItems = offeredItems;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerLocation() {
        return buyerLocation;
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