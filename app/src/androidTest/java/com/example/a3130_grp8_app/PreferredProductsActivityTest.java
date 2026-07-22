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
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.a3130_grp8_app.activities.AddPreferredProductActivity;
import com.example.a3130_grp8_app.activities.EditPreferredProductActivity;
import com.example.a3130_grp8_app.activities.PreferredProductsActivity;
import com.example.a3130_grp8_app.activities.ProductSearchActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.PreferredProduct;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PreferredProductsActivityTest {

    private Context assetContext;
    private Context applicationContext;
    private UiDevice device;
    final String launcherPackage = "com.example.a3130_grp8_app";

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager = mock(FirebaseManager.class);

        PreferredProduct preferredProduct1 = new PreferredProduct(
                "1", "", List.of("Technology", "Decoration"));
        PreferredProduct preferredProduct2 = new PreferredProduct(
                "1", "Plant", List.of());
        PreferredProduct preferredProduct3 = new PreferredProduct(
                "1", "IPad", List.of("Technology"));

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
        assetContext = getInstrumentation().getContext();
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
                                withText("Query: Any")
                        )
                ));

        onView(withText("Query: Any")).check(matches(isDisplayed()));
        onView(withText("Categories: Technology, Decoration")).check(matches(isDisplayed()));

        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(
                                withText("Query: Plant")
                        )
                ));

        onView(withText("Query: Plant")).check(matches(isDisplayed()));
        onView(withText("Categories: Any")).check(matches(isDisplayed()));

        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(
                                withText("Query: IPad")
                        )
                ));

        onView(withText("Query: IPad")).check(matches(isDisplayed()));
        onView(withText("Categories: Technology")).check(matches(isDisplayed()));
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

    @Test
    public void editButtonCreatesCorrectIntent() {
        onView(withId(R.id.preferredProductsRecyclerView))
                .perform(actionOnItem(
                        hasDescendant(withText(contains("Technology, Decoration"))),
                        clickChildViewWithId(R.id.preferredProductTeaserEditButton)
                ));

        PreferredProduct preferredProductToBeExtra = new PreferredProduct("1", "", List.of("Technology", "Decoration"));
        intended(allOf(
                hasComponent(EditPreferredProductActivity.class.getName()),
                hasExtra("PREFERRED_PRODUCT", equalTo(preferredProductToBeExtra))
        ));
    }

    @Test
    public void addProductButtonCreatesIntent() {
        onView(withId(R.id.addPreferredProductsButton))
                .perform(click());

        intended(hasComponent(AddPreferredProductActivity.class.getName()));
    }
}
