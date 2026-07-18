package com.example.a3130_grp8_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.a3130_grp8_app.activities.AddProductActivity;

import org.junit.Test;

public class AddProductActivityTest {

    @Test
    public void parsePriceReturnsCorrectValueForDecimal() {
        float f = AddProductActivity.parsePrice("10.05");
        assertEquals(10.05f, f, 0.0);
    }

    @Test
    public void parsePriceReturnsZeroOnInvalidValue() {
        float f = AddProductActivity.parsePrice("invalid!");
        assertEquals(0f, f, 0.001f);
    }
}
