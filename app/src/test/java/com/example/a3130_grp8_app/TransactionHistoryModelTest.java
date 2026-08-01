package com.example.a3130_grp8_app;

import static org.junit.Assert.*;

import com.example.a3130_grp8_app.model.Request;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionHistoryModelTest {

    private Request makeRequest(String status) {
        Request request = new Request(
                "req-001",
                "prod-123",
                "buyer@gmail.com",
                "uid-buyer",
                "Alice Johnson",
                "Halifax, NS",
                Arrays.asList("Skateboard", "Cash"),
                "uid-seller"
        );
        request.setStatus(status);
        return request;
    }

    /**
     * Given a list of requests with mixed statuses
     * When I filter for completed exchanges
     * Then only accepted requests should be returned
     */
    @Test
    public void filterReturnsOnlyAcceptedRequestsTest() {
        List<Request> requests = Arrays.asList(
                makeRequest("accepted"),
                makeRequest("pending"),
                makeRequest("accepted"),
                makeRequest("declined")
        );

        List<Request> transactions = requests.stream()
                .filter(r -> "accepted".equals(r.getStatus()))
                .collect(Collectors.toList());

        assertEquals(2, transactions.size());
    }

    /**
     * Given a completed transaction
     * When I check if it records the exchange partner
     * Then the partner email and name should be stored correctly
     */
    @Test
    public void transactionRecordsExchangePartnerTest() {
        Request request = makeRequest("accepted");
        assertEquals("buyer@gmail.com", request.getReceiverEmail());
        assertEquals("Alice Johnson", request.getRequesterName());
    }

    /**
     * Given a completed transaction
     * When I check the exchanged items
     * Then the offered items should be stored correctly
     */
    @Test
    public void transactionRecordsExchangedItemsTest() {
        Request request = makeRequest("accepted");
        List<String> items = request.getOfferedItems();
        assertTrue(items.contains("Skateboard"));
        assertTrue(items.contains("Cash"));
    }

    /**
     * Given a completed transaction
     * When I check its timestamp
     * Then it should have been recorded at creation time
     */
    @Test
    public void transactionHasTimestampTest() {
        long before = System.currentTimeMillis();
        Request request = makeRequest("accepted");
        long after = System.currentTimeMillis();
        assertTrue(request.getTimestamp() >= before);
        assertTrue(request.getTimestamp() <= after);
    }

    /**
     * Given a list of requests with no accepted status
     * When I filter for completed exchanges
     * Then an empty list should be returned
     */
    @Test
    public void noAcceptedRequestsReturnsEmptyListTest() {
        List<Request> requests = Arrays.asList(
                makeRequest("pending"),
                makeRequest("declined")
        );

        List<Request> transactions = requests.stream()
                .filter(r -> "accepted".equals(r.getStatus()))
                .collect(Collectors.toList());

        assertTrue(transactions.isEmpty());
    }
}
