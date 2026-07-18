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

    private String id;

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

    /**
     * Returns the product name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the product description.
     *
     * @return the product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     *
     * @param description the product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the product address.
     *
     * @return the product address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the product address.
     *
     * @param address the product address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the product price.
     *
     * @return the product price
     */
    public float getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the product price
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * Returns the product categories.
     *
     * @return the product categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Sets the product categories.
     *
     * @param categories the product categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * Returns whether the product is available.
     *
     * @return {@code true} if the product is available, otherwise {@code false}
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the product availability status.
     *
     * @param available whether the product is available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns the product image URL.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the product image URL.
     *
     * @param imageUrl the image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the UID of the product provider.
     *
     * @return the provider UID
     */
    public String getProviderUid() {
        return providerUid;
    }

    /**
     * Sets the UID of the product provider.
     *
     * @param providerUid the provider UID
     */
    public void setProviderUid(String providerUid) {
        this.providerUid = providerUid;
    }

    /**
     * Returns the time the product was uploaded.
     *
     * @return the upload timestamp
     */
    public String getTimeUploaded() {
        return timeUploaded;
    }

    /**
     * Sets the time the product was uploaded.
     *
     * @param timeUploaded the upload timestamp
     */
    public void setTimeUploaded(String timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    /**
     * Returns the date the product becomes available.
     *
     * @return the availability date
     */
    public String getDateAvailable() {
        return dateAvailable;
    }

    /**
     * Sets the date the product becomes available.
     *
     * @param dateAvailable the availability date
     */
    public void setDateAvailable(String dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    /**
     * Returns the preferred exchange items for the product.
     *
     * @return the preferred exchange items
     */
    public List<String> getPreferredItems() {
        return preferredItems;
    }

    /**
     * Sets the preferred exchange items for the product.
     *
     * @param preferredItems the preferred exchange items
     */
    public void setPreferredItems(List<String> preferredItems) {
        this.preferredItems = preferredItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Creates a snippet for a map marker from the product
     * @return the String to be used as a map marker snippet
     */
    public String createProductSnippet() {
        return String.format(
                "Price: %s, Categories: %s, Address: %s%n",
                getPrice(),
                String.join(", ", getCategories()),
                getAddress()
        );
    }
}

