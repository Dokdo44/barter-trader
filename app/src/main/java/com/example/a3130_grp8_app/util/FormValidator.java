package com.example.a3130_grp8_app.util;

import android.util.Patterns;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Shared form rules extracted from Activities so event handlers only route UI events.
 */
public final class FormValidator {

    private FormValidator() {}

    public static boolean hasText(CharSequence value) {
        return value != null && value.length() > 0;
    }

    public static boolean isValidSignupEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidResetEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isSignupComplete(CharSequence email, CharSequence password, int rolePosition) {
        return hasText(email) && hasText(password) && rolePosition != 0;
    }

    public static boolean isLoginComplete(CharSequence email, CharSequence password) {
        return hasText(email) && hasText(password);
    }
}
