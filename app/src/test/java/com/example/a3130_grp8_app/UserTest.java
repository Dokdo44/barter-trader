package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;

import com.example.a3130_grp8_app.model.User;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserTest {

    @Test
    public void userStoresPreferredProductsAndCategories() {
        User user = new User("receiver@example.com", "Buyer");
        Map<String, String> productIds = new LinkedHashMap<>();
        productIds.put("preference-1", "product-1");
        Map<String, String> categoryNames = new LinkedHashMap<>();
        categoryNames.put("Technology", "Technology");

        user.setPreferredProductIds(productIds);
        user.setPreferredCategoryNames(categoryNames);

        assertEquals(productIds, user.getPreferredProductIds());
        assertEquals(categoryNames, user.getPreferredCategoryNames());
    }
}
