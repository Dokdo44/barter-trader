package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Product implements Serializable {
    private String name;
    private String description;
    private String address;
    private double price;
    private List<String> categories;
    private boolean available;
    private String imageUrl;
    private String providerUid;

    private String timeUploaded;

    public Product() {

    }

    public Product(String name, String description, String address, double price, List<String> categories, boolean available, String imageUrl, String providerUid, String timeUploaded) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.price = price;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.available = available;
        this.imageUrl = imageUrl;
        this.providerUid = providerUid;
        this.timeUploaded = timeUploaded;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProviderUid() {
        return providerUid;
    }

    public void setProviderUid(String providerUid) {
        this.providerUid = providerUid;
    }

    public String getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(String timeUploaded) {
        this.timeUploaded = timeUploaded;
    }
}
