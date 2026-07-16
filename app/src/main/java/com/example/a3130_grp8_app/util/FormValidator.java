package com.example.a3130_grp8_app.util;

import android.util.Patterns;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Shared form rules extracted from Activities so event handlers only route UI events.
 */
public final class FormValidator {

    private FormValidator() {}

    /**
     * Checks whether a text value is present.
     *
     * @param value value to check
     * @return true when value is non-null and non-empty
     */
    public static boolean hasText(CharSequence value) {
        return value != null && value.length() > 0;
    }

    /**
     * Validates signup email input with the Apache email validator already used by the app.
     *
     * @param email email value entered during signup
     * @return true when the email format is valid for signup
     */
    public static boolean isValidSignupEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Validates password reset email input with Android's email pattern.
     *
     * @param email email value entered during password reset
     * @return true when the email format is valid for password reset
     */
    public static boolean isValidResetEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks whether the signup form has all required fields.
     *
     * @param email email text from the signup form
     * @param password password text from the signup form
     * @param rolePosition selected spinner index
     * @return true when email, password, and a non-placeholder role are present
     */
    public static boolean isSignupComplete(CharSequence email, CharSequence password, int rolePosition) {
        return hasText(email) && hasText(password) && rolePosition != 0;
    }

    /**
     * Checks whether the login form has both credential fields.
     *
     * @param email email text from the login form
     * @param password password text from the login form
     * @return true when both login fields contain text
     */
    public static boolean isLoginComplete(CharSequence email, CharSequence password) {
        return hasText(email) && hasText(password);
    }

    public static boolean isListingComplete (CharSequence title, CharSequence description,
                                             CharSequence location, CharSequence dateOfAvailability,
                                             CharSequence marketValue, int CategoryPosition) {

        return hasText(title) && hasText(location) && hasText(description) && hasText(dateOfAvailability)
                && hasText(marketValue) && CategoryPosition != 0;
    }
}
