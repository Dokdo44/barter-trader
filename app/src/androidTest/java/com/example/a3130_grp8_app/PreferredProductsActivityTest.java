package com.example.a3130_grp8_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.example.a3130_grp8_app.activities.PreferredProductsActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.PreferredProduct;
import com.example.a3130_grp8_app.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PreferredProductsActivityTest {

    private Context applicationContext;
    private UiDevice device;
    final String launcherPackage = "com.example.a3130_grp8_app";
    final static String dataFilePathFromAssets = "PreferredProductsActivityTest/database_test_data.json";

    private static ArrayList<Product> parseJsonToProducts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(getInstrumentation().getContext().getAssets().open(dataFilePathFromAssets));
        JsonNode productsNode = rootNode.get("products");

        return mapper.readerFor(new TypeReference<List<Product>>() {}).readValue(productsNode);
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        FirebaseManager mockManager = mock(FirebaseManager.class);

        List<Product> products = parseJsonToProducts();

        when(mockManager.getProduct("product1")).thenReturn(Tasks.forResult(products.get(0)));
        when(mockManager.getProduct("product2")).thenReturn(Tasks.forResult(products.get(1)));
        when(mockManager.getProduct("product3")).thenReturn(Tasks.forResult(products.get(2)));

        PreferredProduct preferredProduct1 = new PreferredProduct(
                "1", "product1");
        PreferredProduct preferredProduct2 = new PreferredProduct(
                "1", "product2");
        PreferredProduct preferredProduct3 = new PreferredProduct(
                "1", "product3");

        DataSnapshot mockSnapshot1 = mock(DataSnapshot.class);
        when(mockSnapshot1.getValue(PreferredProduct.class)).thenReturn(preferredProduct1);
        when(mockSnapshot1.getKey()).thenReturn("preferredProduct1");

        DataSnapshot mockSnapshot2 = mock(DataSnapshot.class);
        when(mockSnapshot2.getValue(PreferredProduct.class)).thenReturn(preferredProduct2);
        when(mockSnapshot2.getKey()).thenReturn("preferredProduct2");

        DataSnapshot mockSnapshot3 = mock(DataSnapshot.class);
        when(mockSnapshot3.getValue(PreferredProduct.class)).thenReturn(preferredProduct3);
        when(mockSnapshot3.getKey()).thenReturn("preferredProduct3");

        doAnswer(invocation -> {
            ChildEventListener childEventListener = invocation.getArgument(1);
            childEventListener.onChildAdded(mockSnapshot1, null);
            childEventListener.onChildAdded(mockSnapshot2, mockSnapshot1.getKey());
            childEventListener.onChildAdded(mockSnapshot3, mockSnapshot2.getKey());
            return null;
        }).when(mockManager).addPreferredProductChildEventListenerByUserId(eq("1"), any());

        when(mockManager.deletePreferredProduct("preferredProduct1")).thenReturn(Tasks.forResult(null));
        when(mockManager.deletePreferredProduct("preferredProduct2"))
                .thenReturn(Tasks.forException(new Exception("Mock exception")));

        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("1");
        when(mockManager.getCurrentUser()).thenReturn(mockUser);

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        applicationContext = ApplicationProvider.getApplicationContext();

        device = UiDevice.getInstance(getInstrumentation());

        final Intent launchIntent = new Intent(applicationContext, PreferredProductsActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        applicationContext.startActivity(launchIntent);
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000);

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void preferredProductsDisplayedCorrectly() {
        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(
                                withText("Toyota Something")
                        )
                ));

        onView(withText("Toyota Something")).check(matches(isDisplayed()));

        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(
                                withText("Ford Something")
                        )
                ));

        onView(withText("Ford Something")).check(matches(isDisplayed()));

        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(
                                withText("Honda Something")
                        )
                ));

        onView(withText("Honda Something")).check(matches(isDisplayed()));
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

    @Test
    public void preferredProductDeleteSuccess() {
        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(actionOnItem(
                        hasDescendant(withText(containsString("Technology, Decoration"))),
                        clickChildViewWithId(R.id.preferredProductTeaserDeleteButton)
                ));

        // Snackbar shows that delete was successful
        onView(withText("Preferred product deleted successfully"))
                .check(matches(isDisplayed()));

        // Preferred product that was deleted is no longer displayed
        onView(withText("Technology, Decoration"))
                .check(doesNotExist());
    }

    @Test
    public void preferredProductDeleteFailure() {
        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(actionOnItem(
                        hasDescendant(withText(containsString("Technology, Decoration"))),
                        clickChildViewWithId(R.id.preferredProductTeaserDeleteButton)
                ));

        // Snackbar shows that delete failed
        onView(withText("Could not delete product: Mock exception"))
                .check(matches(isDisplayed()));
    }
}
