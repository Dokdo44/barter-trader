package com.example.a3130_grp8_app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.PreferredProductsActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/** Instrumented tests for viewing, removing, and editing profile preferences. */
@RunWith(AndroidJUnit4.class)
public class PreferredProductsActivityTest {
    private FirebaseManager firebaseManager;
    private ActivityScenario<PreferredProductsActivity> scenario;

    /** Configures profile preference data returned by Firebase. */
    @Before
    public void setUp() {
        firebaseManager = mock(FirebaseManager.class);
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn("receiver-1");
        when(firebaseManager.getCurrentUser()).thenReturn(firebaseUser);

        Product firstProduct = createProduct("Saved Product One",
                "Technology", "Decoration");
        Product secondProduct = createProduct("Saved Product Two", "Sports");
        when(firebaseManager.getProduct("product-1")).thenReturn(Tasks.forResult(firstProduct));
        when(firebaseManager.getProduct("product-2")).thenReturn(Tasks.forResult(secondProduct));

        DataSnapshot firstPreference = preferenceSnapshot("preference-1", "product-1");
        DataSnapshot secondPreference = preferenceSnapshot("preference-2", "product-2");
        doAnswer(invocation -> {
            ChildEventListener listener = invocation.getArgument(1);
            listener.onChildAdded(firstPreference, null);
            listener.onChildAdded(secondPreference, "preference-1");
            return null;
        }).when(firebaseManager).addChildEventListenerForUserPreferredProductIds(
                eq("receiver-1"), any());

        DataSnapshot technologyPreference = preferenceSnapshot("Technology", "Technology");
        doAnswer(invocation -> {
            ChildEventListener listener = invocation.getArgument(1);
            listener.onChildAdded(technologyPreference, null);
            return null;
        }).when(firebaseManager).addChildEventListenerForUserPreferredCategoryNames(
                eq("receiver-1"), any());

        when(firebaseManager.deletePreferredProductForUser("receiver-1", "preference-1"))
                .thenReturn(Tasks.forResult(null));
        when(firebaseManager.deletePreferredProductForUser("receiver-1", "preference-2"))
                .thenReturn(Tasks.forException(new Exception("Mock exception")));

        FirebaseManager.install(firebaseManager);
        scenario = ActivityScenario.launch(PreferredProductsActivity.class);
    }

    /** Clears the injected singleton after each test. */
    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
        FirebaseManager.install(null);
    }

    /** Verifies that saved products and categories are shown from the user profile. */
    @Test
    public void displaysSavedProductsAndCategories() {
        onView(withText("Saved Product One")).check(matches(isDisplayed()));
        onView(withText("Saved Product Two")).check(matches(isDisplayed()));
        onView(withId(R.id.technologyChip)).check(matches(isChecked()));
    }

    /** Verifies that a receiver can remove a saved product. */
    @Test
    public void removesSavedProduct() {
        onView(withId(R.id.preferredProductsRecyclerView)).perform(actionOnItem(
                hasDescendant(withText("Saved Product One")),
                clickChildViewWithId(R.id.preferredProductTeaserDeleteButton)
        ));

        onView(withText("Preferred product deleted successfully")).check(matches(isDisplayed()));
        onView(withText("Saved Product One")).check(doesNotExist());
    }

    /** Verifies that category toggles persist changes through the data manager. */
    @Test
    public void updatesSavedCategories() {
        onView(withId(R.id.technologyChip)).perform(click());
        verify(firebaseManager).deletePreferredCategoriesForUser("receiver-1", "Technology");

        onView(withId(R.id.decorationChip)).perform(click());
        verify(firebaseManager).addPreferredCategoryForUser("receiver-1", "Decoration");
    }

    private static Product createProduct(String name, String... categories) {
        return new Product(name, "Description", "Halifax", 10.0f,
                Arrays.asList(categories), true, "", "seller", "today", "tomorrow",
                Arrays.asList());
    }

    private static DataSnapshot preferenceSnapshot(String key, String value) {
        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.getKey()).thenReturn(key);
        when(snapshot.getValue(String.class)).thenReturn(value);
        return snapshot;
    }

    private static ViewAction clickChildViewWithId(int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click a child view by ID.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.findViewById(id).performClick();
            }
        };
    }
}
