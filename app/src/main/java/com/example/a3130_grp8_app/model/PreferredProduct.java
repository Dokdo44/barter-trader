package com.example.a3130_grp8_app.model;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.List;

public class PreferredProduct implements Serializable {
    private String id;
    private String userId;
    private String query;
    private List<String> categories;

    public PreferredProduct(String userId, String query, List<String> categories) {
        this.userId = userId;
        this.query = query;
        this.categories = categories;
    }

    public PreferredProduct() {}

    public static void get(String id,
                           OnSuccessListener<PreferredProduct> successListener,
                           OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void save(String userId,
                            String query,
                            List<String> categories,
                            OnSuccessListener<Void> successListener,
                            OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void edit(String id,
                            String userId,
                            String query,
                            List<String> categories,
                            OnSuccessListener<Void> successListener,
                            OnFailureListener failureListener) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void delete(String preferredProductId,
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

    public String getQuery() {
        return query;
    }

    public List<String> getCategories() {
        return categories;
    }
}
