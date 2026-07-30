package com.example.a3130_grp8_app.util;

import com.example.a3130_grp8_app.model.Product;

import java.util.List;

/**
 * Default implementation of PreferredCategoryMatcher using the existing CategoryMatcher logic.
 */
public class DefaultPreferredCategoryMatcher implements PreferredCategoryMatcher {

    @Override
    public boolean matches(List<String> preferredCategories, Product product) {
        return CategoryMatcher.matchesAtLeastOne(product.getCategories(), preferredCategories);
    }
}
