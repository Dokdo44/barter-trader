package com.example.a3130_grp8_app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product listing with all the details that are relevant to it.
 */
public class Product implements Serializable {
    private String name;
    private String description;
    private String address;
    private float price;
    private List<String> categories;
    private boolean available;
    private String imageUrl;
    private String providerUid;

    private String timeUploaded;
    private String dateAvailable;
    private List<String> preferredItems;

    public Product() {

    }

    /**
     * Creates a product with the specified details.
     *
     * @param name the product name
     * @param description the product description
     * @param address the product location
     * @param price the product price
     * @param categories the categories associated with the product
     * @param available whether the product is available
     * @param imageUrl the URL of the product image
     * @param providerUid the UID of the product owner
     * @param timeUploaded the time the product was uploaded
     * @param dateAvailable the date the product becomes available
     * @param preferredItems the items the owner is willing to exchange for
     */
    public Product(String name, String description, String address, float price, List<String> categories, boolean available,
                   String imageUrl, String providerUid, String timeUploaded, String dateAvailable, List<String> preferredItems) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.price = price;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.available = available;
        this.imageUrl = imageUrl;
        this.providerUid = providerUid;
        this.timeUploaded = timeUploaded;
        this.dateAvailable = dateAvailable;
        this.preferredItems = preferredItems != null ? preferredItems : new ArrayList<>();
    }

    // Getters and Setters
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

    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
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
    public String getDateAvailable() {
        return dateAvailable;
    }

    public void setDateAvailable(String dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    public List<String> getPreferredItems() {
        return preferredItems;
    }

    public void setPreferredItems(List<String> preferredItems) {
        this.preferredItems = preferredItems;
    }

}

