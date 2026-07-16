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

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProductDetailsActivityTest {

    private static final String PRODUCT_ID = "product-details-test-product";
    private static final String PRODUCT_DETAILS_ACTIVITY =
            "com.example.a3130_grp8_app.ProductDetailsActivity";

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
        Espresso.onView(withText("Halifax, NS")).check(matches(isDisplayed()));
        Espresso.onView(withText("Available: 2026-07-30")).check(matches(isDisplayed()));
        Espresso.onView(withText("Approx. market value: $45")).check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Vintage Lamp picture"))
                .check(matches(isDisplayed()));
    }

    /**
     * Verifies that receivers can start the request flow from the product details screen.
     *
     * @throws Exception when the detail screen cannot be launched or loaded
     */
    @Test
    public void sendOfferButtonIsAvailableFromProductDetailsTest() throws Exception {
        openProductDetails();

        waitForText("Send Offer");
        Espresso.onView(withText("Send Offer")).check(matches(isDisplayed()));
    }

    private void openProductDetails() {
        Intent intent = new Intent();
        intent.setClassName(BuildConfig.APPLICATION_ID, PRODUCT_DETAILS_ACTIVITY);
        intent.putExtra("productId", PRODUCT_ID);
        intent.putExtra("productTitle", "Vintage Lamp");
        intent.putExtra("productDescription", "Brass table lamp in working condition");
        intent.putExtra("productLocation", "Halifax, NS");
        intent.putExtra("productAvailabilityDate", "2026-07-30");
        intent.putExtra("productApproximateMarketValue", 45);
        intent.putExtra("productImageUrl", "https://example.com/vintage-lamp.jpg");
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
