package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.Firebase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class LocationActivityTest {

    private final String dataFilePathFromAssets = "LocationActivityTest/database_test_data.json";

    private Context assetContext;

    private ArrayList<Product> products;

    public ActivityScenario<ProductSearchActivity> activity;

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private class TestFirebaseManager extends FirebaseManager {
        private Comparator<Product> getProductComparator(String orderBy) {
            return new Comparator<Product>() {
                @Override
                public int compare(Product productA, Product productB) {
                    return switch (orderBy) {
                        case "providerUid" ->
                                CharSequence.compare(productA.getProviderUid(), productB.getProviderUid());
                        case "name" -> CharSequence.compare(productA.getName(), productB.getName());
                        case "description" ->
                                CharSequence.compare(productA.getDescription(), productB.getDescription());
                        case "imageUrl" ->
                                CharSequence.compare(productA.getImageUrl(), productB.getImageUrl());
                        case "address" ->
                                CharSequence.compare(productA.getAddress(), productB.getAddress());
                        case "available" ->
                                Boolean.compare(productA.isAvailable(), productB.isAvailable());
                        case "price" -> Float.compare(productA.getPrice(), productB.getPrice());
                        case "timeUploaded" ->
                                productA.getTimeUploaded().compareTo(productB.getTimeUploaded());
                        default ->
                                throw new IllegalStateException("invalid orderBy string: " + orderBy);
                    };
                }
            };
        }

        @Override
        public void addProductChildEventListener(
                String orderBy,
                ProductChildEventListener productChildEventListener) {
            products.sort(getProductComparator(orderBy));
            for(Product product : products) {
                productChildEventListener.onGetProduct(product);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        assetContext = InstrumentationRegistry.getInstrumentation().getContext();
        products = parseJsonToProducts();

        FirebaseManager.install(new TestFirebaseManager());
        activity = ActivityScenario.launch(ProductSearchActivity.class);
    }


    private ArrayList<Product> parseJsonToProducts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(assetContext.getAssets().open(dataFilePathFromAssets));
        JsonNode productsNode = rootNode.get("products");

        return mapper.readerFor(new TypeReference<List<Product>>() {}).readValue(productsNode);
    }



    /**
     * Given I have searched for used products when I click
     * the map view button then I should see the map view displayed.
     */
    @Test
    public void mapViewButtonShowsMapTest() throws InterruptedException {
        Espresso.onView(withId(R.id.mapViewButton)).perform(click());
        TestAuthHelper.waitForDisplayedView(R.id.mapFragment);
    }

}
