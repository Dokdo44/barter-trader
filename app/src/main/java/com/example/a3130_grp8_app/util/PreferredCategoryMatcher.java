package com.example.a3130_grp8_app.util;
import com.example.a3130_grp8_app.model.Product;
import java.util.List;
public interface PreferredCategoryMatcher {

    boolean matches(List<String> preferredCategories, Product product);
}
