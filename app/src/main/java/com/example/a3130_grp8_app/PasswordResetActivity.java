package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_reset);
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        resetButton.setEnabled(false);
        emailInput.addTextChangedListener(createEmailWatcher());

        // Password reset logic
        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                Snackbar.make(v, "Reset email sent, Check your spam", Snackbar.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Snackbar.make(v, "Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
            });
        });

        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PasswordResetActivity.this,
                    LoginActivity.class
            );
            startActivity(intent);
        });
    }

    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void checkResetButtonState() {
        String email = emailInput.getText().toString().trim();
        resetButton.setEnabled(isValidEmail(email));
    }

    private TextWatcher createEmailWatcher() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkResetButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }
}
