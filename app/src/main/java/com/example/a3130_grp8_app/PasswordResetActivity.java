package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * PasswordResetActivity lets a user request a Firebase Auth password reset email.
 */
public class PasswordResetActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "com.example.a3130_grp8_app.EXTRA_EMAIL";

    private EditText emailInput;
    private Button resetButton;
    private Button backButton;
    private Snackbar snackbar;

    /**
     * Initializes the reset form and pre-fills the email from LoginActivity when available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.passwordResetMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.resetEmailInput);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backToLoginButton);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        // Reuse the email typed on the login screen so the user does not need to re-enter it.
        emailInput.setText(getIntent().getStringExtra(EXTRA_EMAIL));
        emailInput.addTextChangedListener(createEmailWatcher());
        resetButton.setOnClickListener(v -> sendPasswordResetEmail());
        backButton.setOnClickListener(v -> finish());
        updateResetButtonState();
    }

    /**
     * Sends the reset email through Firebase Auth and gives the user a clear result message.
     */
    private void sendPasswordResetEmail() {
        String email = emailInput.getText().toString().trim();
        resetButton.setEnabled(false);

        FirebaseManager.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    snackbar.setText("Password reset email sent");
                    snackbar.show();
                })
                .addOnFailureListener(error -> {
                    snackbar.setText("Could not send reset email");
                    snackbar.show();
                    updateResetButtonState();
                });
    }

    /**
     * Enables reset only when the email looks valid enough for Firebase Auth.
     */
    private void updateResetButtonState() {
        String email = emailInput.getText().toString().trim();
        resetButton.setEnabled(Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Watches the email field and updates the reset button after edits.
     */
    private TextWatcher createEmailWatcher() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateResetButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }
}
