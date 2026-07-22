package com.example.a3130_grp8_app.model;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.List;

public class PreferredProduct {

    private String id;
    private String userId;
    private String productId;

    public PreferredProduct(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public PreferredProduct() {}

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
