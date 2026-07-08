package com.example.a3130_grp8_app;

import static org.junit.Assert.*;

import com.example.a3130_grp8_app.model.Product;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductLocationTest {

    private Product makeProduct(String name, String category, String address) {
        return new Product(
                "uid123",
                name,
                "A description",
                null,
                new String[]{category},
                address,
                true,
                0.0f,
                LocalDateTime.now()
        );
    }

    /**
     * Given products have an address
     * When I filter them for map display
     * Then only products with an address should be included
     */
    @Test
    public void filterRemovesProductsWithNoAddressTest() {
        List<Product> products = Arrays.asList(
                makeProduct("bike", "Sports", "1869 Upper Water St, Halifax, NS"),
                makeProduct("chair", "Furniture", ""),
                makeProduct("table", "Furniture", "5251 Duke St, Halifax, NS")
        );

        List<Product> result = products.stream()
                .filter(p -> p.getAddress() != null && !p.getAddress().isEmpty())
                .collect(Collectors.toList());

        assertEquals(2, result.size());
    }

    /**
     * Given I have filtered products by search text
     * when I open the map view then only the filtered
     * products should appear on the map.
     */
    @Test
    public void filterByTextReturnsOnlyMatchingProductsTest() {
        List<Product> products = Arrays.asList(
                makeProduct("bike", "Sports", "1869 Upper Water St, Halifax, NS"),
                makeProduct("chair", "Furniture", "6080 Young St, Halifax, NS"),
                makeProduct("mountain bike", "Sports", "5675 Spring Garden Rd, Halifax, NS")
        );

        String searchText = "bike";
        List<Product> result = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        assertEquals(2, result.size());
    }

    /**
     * Given I have filtered products by category
     * when I open the map view then only the filtered
     * products should appear on the map.
     */
    @Test
    public void filterByCategoryReturnsOnlyMatchingProductsTest() {
        List<Product> products = Arrays.asList(
                makeProduct("bike", "Sports", "1869 Upper Water St, Halifax, NS"),
                makeProduct("chair", "Furniture", "6080 Young St, Halifax, NS"),
                makeProduct("table", "Furniture", "5675 Spring Garden Rd, Halifax, NS")
        );

        String category = "Furniture";
        List<Product> result = products.stream()
                .filter(p -> Arrays.asList(p.getCategories()).contains(category))
                .collect(Collectors.toList());

        assertEquals(2, result.size());
    }
}