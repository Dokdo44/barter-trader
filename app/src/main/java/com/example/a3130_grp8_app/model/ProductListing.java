package com.example.a3130_grp8_app.model;

/**
 * ProductListing is the Firebase data shape saved when a provider posts an item.
 */
public class ProductListing {
    public String id;
    public String providerUid;
    public String title;
    public String description;
    public String location;
    public String dateOfAvailability;
    public String marketValue;
    public String preferredItems;
    public String category;
    public String photoBase64;

    public ProductListing() {
        // Required by Firebase when it deserializes listing data.
    }

    public ProductListing(String id, String providerUid, String title, String description,
                          String location, String dateOfAvailability, String marketValue,
                          String preferredItems, String category, String photoBase64) {
        this.id = id;
        this.providerUid = providerUid;
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateOfAvailability = dateOfAvailability;
        this.marketValue = marketValue;
        this.preferredItems = preferredItems;
        this.category = category;
        this.photoBase64 = photoBase64;
    }
}
