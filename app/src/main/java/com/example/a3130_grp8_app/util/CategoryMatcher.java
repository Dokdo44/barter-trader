package com.example.a3130_grp8_app.util;

import java.util.List;

public class CategoryMatcher {
    private CategoryMatcher() {}

    /**
     * Checks if there is at least one of the selected categories in the item categories
     * @param itemCategories the categories that the current product has
     * @param selectedCategories the categories selected on search by the user
     * @return whether there is at least one of the selected categories in the item categories
     */
    public static boolean matchesAtLeastOne(List<String> itemCategories, List<String> selectedCategories) {
        if (selectedCategories.isEmpty()) return true;
        for (String category : itemCategories) {
            if (selectedCategories.contains(category))
            {
                return true;
            }
        }
        return false;
    }}
