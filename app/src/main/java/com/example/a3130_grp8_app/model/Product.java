package com.example.a3130_grp8_app.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Product {
    private String providerUid;
    private String name;
    private String description;
    private String imageUrl;
    private ArrayList<String> categories;
    private String address;
    private boolean available;
    private float price;
    private String timeUploaded;

    public Product(String providerUid, String name, String description, String imageUrl,
                   ArrayList<String> categories, String address, boolean available, float price,
                   String timeUploaded) {
        this.providerUid = providerUid;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categories = categories;
        this.address = address;
        this.available = available;
        this.price = price;
        this.timeUploaded = timeUploaded;
    }

    public Product() {}

    public String getProviderUid() {
        return providerUid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getAddress() {
        return address;
    }

    public boolean isAvailable() {
        return available;
    }

    public float getPrice() {
        return price;
    }

    public LocalDateTime getTimeUploaded() {
        return LocalDateTime.parse(timeUploaded);
    }
}
