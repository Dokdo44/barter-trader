package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;

import com.example.a3130_grp8_app.model.Product;

import org.junit.Test;

import java.util.ArrayList;

public class ProductTest {

    @Test
    public void createProductSnippetCreatesCorrectly() {

        Product product = new Product(
                "Vintage Lamp",
                "Brass table lamp in working condition",
                "Halifax, NS",
                45.0f,
                new ArrayList<String>(),
                true,
                "https://example.com/vintage-lamp.jpg",
                "placeholder",
                "2026-07-30",
                "2026-07-30",
                new ArrayList<String>()
        );

        String expected = String.format(
                        "Price: %s, Categories: %s, Address: %s%n",
                        product.getPrice(),
                        String.join(", ", product.getCategories()),
                        product.getAddress()
                );

        assertEquals(product.createProductSnippet(), expected);
    }
}
