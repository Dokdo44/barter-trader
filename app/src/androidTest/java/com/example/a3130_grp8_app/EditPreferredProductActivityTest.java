package com.example.a3130_grp8_app;

import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.EditPreferredProductActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.PreferredProduct;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EditPreferredProductActivityTest {
    private static FirebaseManager mockManager;
    private Intent intent;

    @BeforeClass
    public static void setUpClass() {
        mockManager = mock(FirebaseManager.class);
        when(mockManager.editPreferredProduct("1", "1", "IPad", List.of("Technology")))
                .thenReturn(Tasks.forResult(null));
        when(mockManager.editPreferredProduct("2", "1", "Plant", List.of("Decoration")))
                .thenReturn(Tasks.forException(new Exception("Mock exception")));

        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("1");
        when(mockManager.getCurrentUser()).thenReturn(mockUser);
    }

    @Before
    public void setUp() {
        intent = new Intent(ApplicationProvider.getApplicationContext(), EditPreferredProductActivity.class);
        FirebaseManager.install(mockManager);
    }

    @Test
    public void fieldsFilledOnEntry() {
        PreferredProduct preferredProduct = new PreferredProduct("1", "Plant", List.of("Decoration"));
        intent.putExtra("PREFERRED_PRODUCT", preferredProduct);
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.editPreferredProductQueryEditText))
                .check(matches(withText("Plant")));
        Espresso.onView(withId(R.id.editPreferredProductCategoriesEditText))
                .check(matches(withText("Decoration")));
    }

    @Test
    public void editSuccess() {
        PreferredProduct preferredProduct = new PreferredProduct("1", "Plant", List.of("Decoration"));
        intent.putExtra("PREFERRED_PRODUCT", preferredProduct);
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.editPreferredProductQueryEditText))
                .perform(replaceText("IPad"));
        Espresso.onView(withId(R.id.editPreferredProductCategoriesEditText))
                .perform(replaceText("Technology"));
        Espresso.onView(withId(R.id.editPreferredProductButton))
                .perform(click());

        // Snackbar shows that the preferred product was edited
        Espresso.onView(withText("Edited preferred product successfully"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void editFailure() {
        PreferredProduct preferredProduct = new PreferredProduct("1", "IPad", List.of("Technology"));
        intent.putExtra("PREFERRED_PRODUCT", preferredProduct);
        ActivityScenario.launch(intent);

        Espresso.onView(withId(R.id.editPreferredProductQueryEditText))
                .perform(replaceText("Plant"));
        Espresso.onView(withId(R.id.editPreferredProductCategoriesEditText))
                .perform(replaceText("Decoration"));
        Espresso.onView(withId(R.id.editPreferredProductButton))
                .perform(click());

        // Snackbar shows that the preferred product was edited
        Espresso.onView(withText("Failed to edit product: Mock exception"))
                .check(matches(isDisplayed()));
    }
}
