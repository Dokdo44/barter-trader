package com.example.a3130_grp8_app;

import com.example.a3130_grp8_app.activities.ProductSearchActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.a3130_grp8_app.model.Product;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ProductSearchActivityTest {
    final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;
    final String launcherPackage = "com.example.a3130_grp8_app";
    final String dataFilePathFromAssets = "ProductSearchActivityTest/database_test_data.json";
    private Context assetContext;
    private Context applicationContext;
    private static ArrayList<Product> products;
    private FirebaseUser firebaseUser;

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private static class TestFirebaseManager extends FirebaseManager {

        private Comparator<Product> getProductComparator(String orderBy) {
            return new Comparator<Product>() {
                @Override
                public int compare(Product productA, Product productB) {
                    return switch(orderBy) {
                        case "providerUid" -> CharSequence.compare(productA.getProviderUid(), productB.getProviderUid());
                        case "name" -> CharSequence.compare(productA.getName(), productB.getName());
                        case "description" -> CharSequence.compare(productA.getDescription(), productB.getDescription());
                        case "imageUrl" -> CharSequence.compare(productA.getImageUrl(), productB.getImageUrl());
                        case "address" ->  CharSequence.compare(productA.getAddress(), productB.getAddress());
                        case "available" -> Boolean.compare(productA.isAvailable(), productB.isAvailable());
                        case "price" -> Double.compare(productA.getPrice(), productB.getPrice());
                        case "timeUploaded" -> productA.getTimeUploaded().compareTo(productB.getTimeUploaded());
                        default -> throw new IllegalStateException("invalid orderBy string: " + orderBy);
                    };
                }
            };
        }

        @Override
        public void getProducts(
                FirebaseManager.ProductValueEventListener productValueEventListener,
                int limitFirst) {
            products.sort(getProductComparator("timeUploaded"));
            productValueEventListener.onGetProducts(products);
        }
    }

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager.install(new TestFirebaseManager());
    }

    @Before
    public void setUp() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        assetContext = getInstrumentation().getContext();
        applicationContext = ApplicationProvider.getApplicationContext();

        products = parseJsonToProducts();

        device = UiDevice.getInstance(getInstrumentation());

        final Intent launchIntent = new Intent(applicationContext, ProductSearchActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        applicationContext.startActivity(launchIntent);

        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    private ArrayList<Product> parseJsonToProducts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(assetContext.getAssets().open(dataFilePathFromAssets));
        JsonNode productsNode = rootNode.get("products");

        return mapper.readerFor(new TypeReference<List<Product>>() {}).readValue(productsNode);
    }

    @Test
    public void productsOrderedByUploadTimeOnInitialEntry() throws UiObjectNotFoundException, IOException, InterruptedException {

        UiObject product1 = device.findObject(new UiSelector().textContains("Toyota Something"));
        UiObject product2 = device.findObject(new UiSelector().textContains("Ford Something"));
        UiObject product3 = device.findObject(new UiSelector().textContains("Honda Something"));

        Thread.sleep(2000);

        Rect product1Rect = product1.getBounds();
        Rect product2Rect = product2.getBounds();
        Rect product3Rect = product3.getBounds();

        assertTrue(product1Rect.centerY() < product2Rect.centerY());
        assertTrue(product2Rect.centerY() < product3Rect.centerY());
    }

    @Test
    public void onlyShowsResultsWithinSelectedVicinity() throws InterruptedException {
        Espresso.onView(withId(R.id.proximitySpinner)).perform(click());

        Espresso.onData(allOf(is(instanceOf(String.class)), is("5km")))
                .perform(click());

        Espresso.onView(withId(R.id.searchButton)).perform(click());

        Thread.sleep(2000);

        UiObject resultObject = device.findObject(new UiSelector().textContains("Toyota Something"));
        UiObject resultNotExist2 = device.findObject(new UiSelector().textContains("Ford Something"));
        assertTrue(resultObject.exists());
        assertFalse(resultNotExist2.exists());
    }

    @Test
    public void onlyShowsResultsWithSearchText() {
        Espresso.onView(withId(R.id.searchEditText)).perform(typeText("Honda"));
        Espresso.onView(withId(R.id.searchButton)).perform(click());

        UiObject resultObject = device.findObject(new UiSelector().textContains("Honda Something"));
        UiObject resultNotExist1 = device.findObject(new UiSelector().textContains("Toyota Something"));
        UiObject resultNotExist2 = device.findObject(new UiSelector().textContains("Ford Something"));

        assertTrue(resultObject.exists());
        assertFalse(resultNotExist1.exists());
        assertFalse(resultNotExist2.exists());
    }

    @Test
    public void onlyShowsResultsWithCategories() {
        Espresso.onView(withId(R.id.decorationChip)).perform(click());
        Espresso.onView(withId(R.id.searchButton)).perform(click());

        UiObject resultObject1 = device.findObject(new UiSelector().textContains("Honda Something"));
        UiObject resultObject2 = device.findObject(new UiSelector().textContains("Toyota Something"));
        UiObject resultNotExist2 = device.findObject(new UiSelector().textContains("Ford Something"));

        assertTrue(resultObject1.exists());
        assertTrue(resultObject2.exists());
        assertFalse(resultNotExist2.exists());
    }


}
