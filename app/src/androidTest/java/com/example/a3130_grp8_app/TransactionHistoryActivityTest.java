package com.example.a3130_grp8_app;

import static androidx.test.espresso.action.ViewActions.click;
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
import com.google.android.gms.tasks.Tasks;

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
        FirebaseManager mockManager = Mockito.mock(FirebaseManager.class);

        Product testProduct1 = new Product(
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
                Arrays.asList("Skateboard", "Cash")
        );
        testProduct1.setId("prod-123");

        Product testProduct2 = new Product(
                "Vintage Chair",
                "A vintage chair",
                "5251 Duke St, Halifax, NS",
                0.0f,
                Collections.singletonList("Furniture"),
                true,
                null,
                "uid-seller",
                "2025-07-13",
                "2025-07-20",
                Arrays.asList("Book", "Gift Card")
        );
        testProduct2.setId("prod-456");

        doReturn(Tasks.forResult(testProduct1))
                .when(mockManager).getProduct(eq("prod-123"));
        doReturn(Tasks.forResult(testProduct2))
                .when(mockManager).getProduct(eq("prod-456"));

        Request acceptedRequest = new Request(
                "req-001",
                "prod-123",
                "buyer1@gmail.com",
                "uid-buyer1",
                "Alice Johnson",
                "Halifax, NS",
                Arrays.asList("Skateboard", "Cash"),
                "uid-seller"
        );
        acceptedRequest.setStatus("accepted");

        Request acceptedRequest2 = new Request(
                "req-002",
                "prod-456",
                "buyer2@gmail.com",
                "uid-buyer2",
                "Marcus Lee",
                "Halifax, NS",
                Arrays.asList("Book", "Gift Card"),
                "uid-seller"
        );
        acceptedRequest2.setStatus("accepted");

        Request pendingRequest = new Request(
                "req-003",
                "prod-789",
                "buyer3@gmail.com",
                "uid-buyer3",
                "John Smith",
                "Halifax, NS",
                Arrays.asList("Vinyl Record"),
                "uid-seller"
        );

        ArrayList<Request> acceptedRequests = new ArrayList<>(
                Arrays.asList(acceptedRequest, acceptedRequest2));
        ArrayList<Request> emptyRequests = new ArrayList<>();

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(acceptedRequests);
            return null;
        }).when(mockManager).getRequests(eq("uid-with-history"), any());

        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(emptyRequests);
            return null;
        }).when(mockManager).getRequests(eq("uid-no-history"), any());

        FirebaseManager.install(mockManager);
    }

    @Before
    public void setUp() {
        intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                TransactionHistoryActivity.class
        );
    }

    /**
     * Given a user has completed exchanges
     * When they view their transaction history
     * Then all completed transactions should be displayed
     */
    @Test
    public void completedTransactionsDisplayedTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .check(matches(isDisplayed()));
        Espresso.onView(withText(containsString("Marcus Lee")))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user has completed exchanges
     * When they view their transaction history
     * Then each transaction should display the exchanged product
     */
    @Test
    public void transactionShowsExchangedProductTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .perform(click());

        Espresso.onView(withText(containsString("Test Bike")))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user has completed exchanges
     * When they view their transaction history
     * Then each transaction should display the exchange partner
     */
    @Test
    public void transactionShowsExchangePartnerTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .perform(click());

        Espresso.onView(withText(containsString("buyer1@gmail.com")))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user was a buyer in a completed transaction
     * When they view the transaction details
     * Then the seller's ID should be displayed as the exchange partner
     */
    @Test
    public void buyerCanSeeSellerInfoTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .perform(click());

        Espresso.onView(withText(containsString("uid-seller")))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user has completed exchanges
     * When they view their transaction history
     * Then each transaction should include a timestamp and item details
     */
    @Test
    public void transactionShowsTimestampAndItemDetailsTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .perform(click());

        Espresso.onView(withText(containsString("Skateboard")))
                .check(matches(isDisplayed()));
        Espresso.onView(withText(containsString("accepted")))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user has no completed exchanges
     * When they view their transaction history
     * Then a message indicating no transactions should be displayed
     */
    @Test
    public void noTransactionsMessageDisplayedTest() {
        intent.putExtra("USER_ID", "uid-no-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText("No completed transactions"))
                .check(matches(isDisplayed()));
    }

    /**
     * Given a user has both pending and accepted requests
     * When they view their transaction history
     * Then only accepted requests should appear
     */
    @Test
    public void onlyAcceptedRequestsShownTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Alice Johnson")))
                .check(matches(isDisplayed()));

        try {
            Espresso.onView(withText(containsString("John Smith")))
                    .check(matches(isDisplayed()));
            throw new AssertionError("Pending request should not be visible");
        } catch (Exception e) {
        }
    }
}
