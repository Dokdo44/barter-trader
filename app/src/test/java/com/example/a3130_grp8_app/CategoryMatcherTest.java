package com.example.a3130_grp8_app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.a3130_grp8_app.util.CategoryMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CategoryMatcherTest {

    @Test
    public void categoryMatcherCorrectlyMatches() {
        ArrayList<String> selectedCategories = new ArrayList<>(List.of("Technology"));
        ArrayList<String> itemCategories = new ArrayList<>(List.of("Decoration", "Technology"));

        boolean result = CategoryMatcher.matchesAtLeastOne(itemCategories, selectedCategories);
        assertTrue(result);
    }

    @Test
    public void categoryMatcherReturnsFalseWhenNoneMatch() {
        ArrayList<String> selectedCategories = new ArrayList<>(List.of("Other"));
        ArrayList<String> itemCategories = new ArrayList<>(List.of("Decoration", "Technology"));

        boolean result = CategoryMatcher.matchesAtLeastOne(itemCategories, selectedCategories);
        assertFalse(result);
    }
}
