package com.example.a3130_grp8_app;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.model.Product;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ProductDetailsActivityTest {

    private static final String PRODUCT_ID = "product-details-test-product";
    private static final String PRODUCT_DETAILS_ACTIVITY =
            "com.example.a3130_grp8_app.activities.ProductDetailsActivity";

    /**
     * Verifies that opening a selected product shows every detail required by the user story.
     *
     * @throws Exception when the detail screen cannot be launched or loaded
     */
    @Test
    public void selectedProductShowsFullDetailsTest() throws Exception {
        openProductDetails();

        waitForText("Vintage Lamp");
        Espresso.onView(withText("Vintage Lamp")).check(matches(isDisplayed()));
        Espresso.onView(withText("Brass table lamp in working condition"))
                .check(matches(isDisplayed()));
        Espresso.onView(withText("Location: Halifax, NS")).check(matches(isDisplayed()));
        Espresso.onView(withText("Available: 2026-07-30")).check(matches(isDisplayed()));
        Espresso.onView(withText("Approximate Market Value: $45.00")).check(matches(isDisplayed()));
    }

    /**
     * Verifies that receivers can start the request flow from the product details screen.
     *
     * @throws Exception when the detail screen cannot be launched or loaded
     */
    @Test
    public void sendOfferButtonIsAvailableFromProductDetailsTest() throws Exception {
        openProductDetails();

        waitForText("Request Item");
        Espresso.onView(withText("Request Item")).check(matches(isDisplayed()));
    }

    private void openProductDetails() {
        Intent intent = new Intent();
        intent.setClassName(BuildConfig.APPLICATION_ID, PRODUCT_DETAILS_ACTIVITY);

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

        intent.putExtra("PRODUCT", product);
        ActivityScenario.launch(intent);
    }

    private void waitForText(String text) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            try {
                Espresso.onView(withText(text)).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError error) {
                Thread.sleep(250);
            }
        }

        Espresso.onView(withText(text)).check(matches(isDisplayed()));
    }
}
