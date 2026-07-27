package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PreferredProductNotificationManager {

    private final FirebaseManager firebaseManager;
    private NotificationSender notificationSender;
    private final PreferredCategoryMatcher categoryMatcher;

    private ProductListenerRegistration productListenerRegistration;
    private List<String> preferredCategories = new ArrayList<>();

    public PreferredProductNotificationManager(FirebaseManager firebaseManager,
                                               NotificationSender notificationSender,
                                               PreferredCategoryMatcher categoryMatcher) {
        this.firebaseManager = firebaseManager;
        this.notificationSender = notificationSender;
        this.categoryMatcher = categoryMatcher;
    }

    /**
     * Loads the current user's preferred categories and begins listening for new products.
     */
    public void start() {
        String userId = firebaseManager.getCurrentUserID();
        if (userId == null) return;

        firebaseManager.loadPreferredCategories(userId, new PreferredCategoriesListener() {
            @Override
            public void onCategoriesLoaded(List<String> categories) {
                preferredCategories = categories;
                registerProductListener();
            }

            @Override
            public void onError(Exception e) {
                // Nothing to match against if categories fail to load
            }
        });
    }

    private void registerProductListener() {
        productListenerRegistration = firebaseManager.listenForNewProduct(new ProductPostedListener() {
            @Override
            public void onProductPosted(Product product) {
                if (categoryMatcher.matches(preferredCategories, product)) {
                    notificationSender.sendPreferredProductNotification(product);
                }
            }

            @Override
            public void onError(Exception e) {
                // Silently ignore listener errors for now
            }
        });
    }

    /**
     * Stops listening for new products.
     */
    public void stop() {
        if (productListenerRegistration != null) {
            productListenerRegistration.remove();
            productListenerRegistration = null;
        }
    }
}