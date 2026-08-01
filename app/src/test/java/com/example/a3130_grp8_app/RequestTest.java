package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;

import com.example.a3130_grp8_app.model.Request;

import org.junit.Test;

import java.util.Collections;

/**
 * Unit tests for exchange proposal state stored by the Request model.
 */
public class RequestTest {

    /**
     * Verifies that a new exchange proposal starts pending and retains its selected item.
     */
    @Test
    public void newRequestStartsPendingWithSelectedOfferTest() {
        Request request = new Request(
                "request-1",
                "product-1",
                "receiver@example.test",
                "receiver-uid",
                "Receiver One",
                "Dartmouth, NS",
                Collections.singletonList("Skateboard"),
                "provider-uid"
        );

        assertEquals("pending", request.getStatus());
        assertEquals(Collections.singletonList("Skateboard"), request.getOfferedItems());
    }
}
