package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.util.FormValidator;
import com.example.a3130_grp8_app.util.SimpleTextWatcher;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * PasswordResetActivity handles UI events and delegates reset delivery to FirebaseManager.
 */
public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;

    /**
     * Initializes the password reset form and back-to-login navigation.
     *
     * @param savedInstanceState prior Activity state provided by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_reset);
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        resetButton.setEnabled(false);
        emailInput.addTextChangedListener(createEmailWatcher());

        resetButton.setOnClickListener(v -> requestPasswordReset());

        backToLoginButton.setOnClickListener(v -> onBackToLoginClick());
    }

    private void requestPasswordReset() {
        String email = emailInput.getText().toString().trim();
        // Firebase Auth owns password reset delivery, so the Activity only triggers the request.
        FirebaseManager.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> onResetRequestSuccess())
                .addOnFailureListener(this::onResetFailure);
    }

    private void onBackToLoginClick() {
        Intent intent = new Intent(
                PasswordResetActivity.this,
                LoginActivity.class
        );
        startActivity(intent);
    }

    private void onResetRequestSuccess() {
        Snackbar.make(resetButton, "Reset email sent, Check your spam",
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void onResetFailure(Exception e) {
        Snackbar.make(resetButton, "Failed: " + e.getMessage(),
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void checkResetButtonState() {
        String email = emailInput.getText().toString().trim();
        resetButton.setEnabled(FormValidator.isValidResetEmail(email));
    }

    private SimpleTextWatcher createEmailWatcher() {
        return new SimpleTextWatcher() {
            /**
             * Refreshes reset button availability after the email field changes.
             *
             * @param s edited email text
             */
            @Override
            public void afterTextChanged(android.text.Editable s) {
                checkResetButtonState();
            }
        };
    }
}
