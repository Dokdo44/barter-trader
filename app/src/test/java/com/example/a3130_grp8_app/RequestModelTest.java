package com.example.a3130_grp8_app;

import static org.junit.Assert.*;

import com.example.a3130_grp8_app.model.Request;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RequestModelTest {

    private Request makeRequest() {
        return new Request(
                "request_123",
                "product_123",
                "test@email.com",
                "receiver_uid",
                "John",
                "Halifax, NS",
                Arrays.asList("Skateboard", "Cash")
        );
    }

    /**
     * Given a request is created with a product ID
     * When I retrieve the product ID
     * Then it should match what was set
     */
    @Test
    public void requestStoresProductIdCorrectlyTest() {
        Request request = makeRequest();
        assertEquals("product_123", request.getProductId());
    }

    /**
     * Given a request is created with a receiver UID
     * When I retrieve the receiver UID
     * Then it should match what was set
     */
    @Test
    public void requestStoresReceiverUidCorrectlyTest() {
        Request request = makeRequest();
        assertEquals("receiver_uid", request.getReceiverUid());
    }

    /**
     * Given a request is created with a receiver email
     * When I retrieve the receiver email
     * Then it should match what was set
     */
    @Test
    public void requestStoresReceiverEmailCorrectlyTest() {
        Request request = makeRequest();
        assertEquals("test@email.com", request.getReceiverEmail());
    }

    /**
     * Given a request is created
     * When I check its status
     * Then it should be pending by default
     */
    @Test
    public void requestHasDefaultPendingStatusTest() {
        Request request = makeRequest();
        assertEquals("pending", request.getStatus());
    }

    /**
     * Given a request is created with offered items
     * When I retrieve the offered items
     * Then they should match what was set
     */
    @Test
    public void requestStoresOfferedItemsCorrectlyTest() {
        List<String> items = Arrays.asList("Skateboard", "Cash");
        Request request = makeRequest();
        assertEquals(items, request.getOfferedItems());
    }

    /**
     * Given a request is created with a name and location
     * When I retrieve them
     * Then they should match what was set
     */
    @Test
    public void requestStoresRequesterNameAndLocationCorrectlyTest() {
        Request request = makeRequest();
        assertEquals("John", request.getRequesterName());
        assertEquals("Halifax, NS", request.getRequesterLocation());
    }

    /**
     * Given a request is created
     * When I check its timestamp
     * Then it should be a recent time
     */
    @Test
    public void requestTimestampIsSetOnCreationTest() {
        long before = System.currentTimeMillis();
        Request request = makeRequest();
        long after = System.currentTimeMillis();
        assertTrue(request.getTimestamp() >= before);
        assertTrue(request.getTimestamp() <= after);
    }

    /**
     * Given a request exists
     * When I update its status
     * Then the status should reflect the change
     */
    @Test
    public void requestStatusCanBeUpdatedTest() {
        Request request = makeRequest();
        request.setStatus("accepted");
        assertEquals("accepted", request.getStatus());
    }
}
