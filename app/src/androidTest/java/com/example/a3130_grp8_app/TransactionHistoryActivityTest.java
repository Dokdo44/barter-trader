package com.example.a3130_grp8_app;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.a3130_grp8_app.activities.TransactionHistoryActivity;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Request;
import com.example.a3130_grp8_app.model.User;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(AndroidJUnit4.class)
public class TransactionHistoryActivityTest {

    Intent intent;

    @BeforeClass
    public static void setUpClass() {
        FirebaseManager mockManager =
                Mockito.mock(FirebaseManager.class);

        FirebaseUser mockUser =
                Mockito.mock(FirebaseUser.class);

        doReturn("uid-with-history").when(mockUser).getUid();

        doReturn(mockUser).when(mockManager).getCurrentUser();

        Product testProduct =
                new Product(
                        "Test Bike",
                        "A great bike",
                        "1869 Upper Water St, Halifax, NS",
                        0.0f,
                        Collections.singletonList("Sports"),
                        true,
                        null,
                        "uid-seller",
                        "2025-07-13",
                        "2025-07-20",
                        Arrays.asList(
                                "Skateboard",
                                "Cash"
                        )
                );
        testProduct.setId("prod-123");


        // Mock product lookup
        doAnswer(invocation -> {
            FirebaseManager.ProductCallback callback = invocation.getArgument(1);

            callback.onProductLoaded(testProduct);
            return null;

        }).when(mockManager).getProductById(eq("prod-123"), any());


        // Mock seller lookup
        User seller =
                new User(
                        "seller@gmail.com",
                        "provider"
                );
        doReturn(
                Tasks.forResult(seller)
        )
                .when(mockManager)
                .getUserById("uid-seller");


        // User is the buyer for this request
        Request request =
                new Request(
                        "req-001",
                        "prod-123",
                        "seller@gmail.com",
                        "uid-seller",
                        "Alice Johnson",
                        "Halifax, NS",
                        Arrays.asList(
                                "Skateboard",
                                "Cash"
                        ),
                        "uid-seller"
                );
        request.setStatus("accepted");


        // As a buyer — getRequestsByReceiver returns accepted request
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);

            listener.onGetRequests(new ArrayList<>(Collections.singletonList(request)));

            return null;
        }).when(mockManager).getRequestsByReceiver(eq("uid-with-history"), any());

        // As a seller — no extra requests
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);

            listener.onGetRequests(
                    new ArrayList<>()
            );
            return null;
        }).when(mockManager).getRequestsByProvider(eq("uid-with-history"), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        intent =
                new Intent(
                        ApplicationProvider.getApplicationContext(),
                        TransactionHistoryActivity.class
                );
    }

    /**
     * Verifies that completed transactions are displayed.
     */
    @Test
    public void completedTransactionsDisplayedTest() {
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Test Bike"))).check(matches(isDisplayed()));
    }

    /**
     * Verifies that transaction details are displayed.
     */
    @Test
    public void transactionDetailsDisplayedTest() {
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson"))).check(matches(isDisplayed()));
        Espresso.onView(withText(containsString("Skateboard"))).check(matches(isDisplayed()));
    }

    /**
     * Verifies that seller information is displayed.
     */
    @Test
    public void sellerInformationDisplayedTest() {
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("seller@gmail.com"))).check(matches(isDisplayed()));
    }

    /**
     * Verifies that accepted transactions are shown.
     */
    @Test
    public void acceptedTransactionDisplayedTest() {
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("accepted"))).check(matches(isDisplayed()));
    }
}