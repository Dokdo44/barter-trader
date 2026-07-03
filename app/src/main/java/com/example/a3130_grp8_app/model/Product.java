package com.example.a3130_grp8_app.model;

public class Product {
    private String providerEmail;
    private String name;
    private String description;
    private String imageUrl;
    private String[] categories;
    private String address;
    private boolean available;
    private float price;

    public Product(String providerEmail, String name, String description, String imageUrl,
                   String[] categories, String address, boolean available, float price) {
        this.providerEmail = providerEmail;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categories = categories;
        this.address = address;
        this.available = available;
        this.price = price;
    }

    public Product() {}

    public String getProviderEmail() {
        return providerEmail;
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

    public String[] getCategories() {
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
}
