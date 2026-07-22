package com.example.a3130_grp8_app.model;

import java.util.List;

public class PreferredProduct {
    private String id;
    private User user;
    private String query;
    private List<String> categories;

    public PreferredProduct(User user, String query, List<String> categories) {
        this.user = user;
        this.query = query;
        this.categories = categories;
    }

    public PreferredProduct() {}

    public static PreferredProduct get(String id) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void save(User user, String query, List<String> categories) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void edit(String id, User user, String query, List<String> categories) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public static void delete(String preferredProductId) {
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getCategories() {
        return categories;
    }
}
