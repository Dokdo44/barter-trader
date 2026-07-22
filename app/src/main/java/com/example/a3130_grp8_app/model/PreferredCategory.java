package com.example.a3130_grp8_app.model;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class PreferredCategory {
    private String id;
    private String userId;
    private String categoryName;

    public PreferredCategory(String userId, String categoryName) {
        this.userId = userId;
        this.categoryName = categoryName;
    }

    public static void get(String id,
                           OnSuccessListener<PreferredProduct> successListener,
                           OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void save(String userId,
                            String productId,
                            OnSuccessListener<Void> successListener,
                            OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void delete(String id,
                              OnSuccessListener<Void> successListener,
                              OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
