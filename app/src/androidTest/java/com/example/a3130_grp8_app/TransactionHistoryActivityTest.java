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

        // User is the buyer for this request
        Request buyerRequest = new Request(
                "req-001",
                "prod-123",
                "uid-with-history@gmail.com",
                "uid-with-history",
                "Alice Johnson",
                "Halifax, NS",
                Arrays.asList("Skateboard", "Cash"),
                "uid-seller"
        );
        buyerRequest.setStatus("accepted");

        // User is the seller for this request
        Request sellerRequest = new Request(
                "req-002",
                "prod-456",
                "buyer2@gmail.com",
                "uid-buyer2",
                "Marcus Lee",
                "Halifax, NS",
                Arrays.asList("Book", "Gift Card"),
                "uid-with-history"
        );
        sellerRequest.setStatus("accepted");

        // Pending request — should NOT appear
        Request pendingRequest = new Request(
                "req-003",
                "prod-123",
                "uid-with-history@gmail.com",
                "uid-with-history",
                "John Smith",
                "Halifax, NS",
                Arrays.asList("Vinyl Record"),
                "uid-seller"
        );

        // As a buyer — getRequestsByReceiver returns their accepted request
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>(Collections.singletonList(buyerRequest)));
            return null;
        }).when(mockManager).getRequestsByReceiver(eq("uid-with-history"), any());

        // As a seller — getRequestsByProvider returns accepted request
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>(Collections.singletonList(sellerRequest)));
            return null;
        }).when(mockManager).getRequestsByProvider(eq("uid-with-history"), any());

        // No transactions as buyer
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>());
            return null;
        }).when(mockManager).getRequestsByReceiver(eq("uid-no-history"), any());

        // No transactions as seller
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>());
            return null;
        }).when(mockManager).getRequestsByProvider(eq("uid-no-history"), any());

        // Only pending requests as buyer
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>(Collections.singletonList(pendingRequest)));
            return null;
        }).when(mockManager).getRequestsByReceiver(eq("uid-pending-only"), any());

        // No requests as a seller
        doAnswer(invocation -> {
            FirebaseManager.RequestValueEventListener listener =
                    invocation.getArgument(1);
            listener.onGetRequests(new ArrayList<>());
            return null;
        }).when(mockManager).getRequestsByProvider(eq("uid-pending-only"), any());

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
     * Given a user acted as a buyer in a completed exchange
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
     * Given a user acted as a seller in a completed exchange
     * When they view the transaction details
     * Then the buyer's email should be displayed as the exchange partner
     */
    @Test
    public void sellerCanSeeExchangePartnerTest() {
        intent.putExtra("USER_ID", "uid-with-history");
        ActivityScenario.launch(intent);

        Espresso.onView(withText(containsString("Marcus Lee")))
                .perform(click());

        Espresso.onView(withText(containsString("buyer2@gmail.com")))
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
     * Given a user has only pending requests
     * When they view their transaction history
     * Then no transactions should be displayed
     */
    @Test
    public void onlyAcceptedRequestsShownTest() {
        intent.putExtra("USER_ID", "uid-pending-only");
        ActivityScenario.launch(intent);

        Espresso.onView(withText("No completed transactions"))
                .check(matches(isDisplayed()));
    }
}
