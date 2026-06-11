package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_reset);
        emailInput = findViewById(R.id.emailInput);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setEnabled(false);
        emailInput.addTextChangedListener(createEmailWatcher());
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
