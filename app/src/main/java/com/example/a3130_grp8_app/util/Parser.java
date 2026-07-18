package com.example.a3130_grp8_app.util;

public class Parser {

    private Parser() {}

    /**
     * Parses string to float, and returns 0 on failure
     * @param rawValue the string to be parsed to a float
     * @return the parsed float
     */
    public static float parsePrice(String rawValue) {
        try {
            return Float.parseFloat(rawValue);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}
