package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private Snackbar snackbar;
    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView resetPasswordLink;
    private TextView signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        resetPasswordLink = findViewById(R.id.resetPasswordLink);
        signUpLink = findViewById(R.id.signUpLink);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        TextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher();
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);
        loginButton.setOnClickListener(v -> submitLogin());
        // Password reset stays on the login screen and uses Firebase Auth email reset.
        resetPasswordLink.setOnClickListener(v -> sendPasswordResetEmail());
        // Signup is no longer a launcher activity, so login provides the path to account creation.
        signUpLink.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void submitLogin() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        // Disable while Auth checks credentials to avoid duplicate login requests.
        loginButton.setEnabled(false);

        // Firebase Auth validates credentials; the app no longer compares plaintext passwords.
        FirebaseManager.signIn(email, password)
                .addOnSuccessListener(unused -> {
                    startActivity(new Intent(LoginActivity.this, RoleSpecificDashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(error -> {
                    snackbar.setText("Invalid email or password");
                    snackbar.show();
                    updateLoginButtonState();
                });
    }

    private void sendPasswordResetEmail() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            snackbar.setText("Enter your email first");
            snackbar.show();
            return;
        }

        // Auth owns password reset delivery, so no database password lookup is needed.
        FirebaseManager.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    snackbar.setText("Password reset email sent");
                    snackbar.show();
                })
                .addOnFailureListener(error -> {
                    snackbar.setText("Could not send reset email");
                    snackbar.show();
                });
    }

    private void updateLoginButtonState() {
        loginButton.setEnabled(
                emailInput.getText().length() != 0 && passwordInput.getText().length() != 0
        );
    }

    private TextWatcher createEmptyFieldsWatcher() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateLoginButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }

}
