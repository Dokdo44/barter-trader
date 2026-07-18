package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.util.FormValidator;
import com.example.a3130_grp8_app.util.SimpleTextWatcher;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * LoginActivity handles Firebase Auth login and links to account creation/password reset.
 */
public class LoginActivity extends AppCompatActivity {

    private Snackbar snackbar;
    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;

    /**
     * Initializes the login UI and wires auth-related click listeners.
     *
     * @param savedInstanceState prior Activity state provided by Android
     */
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
        TextView resetPasswordLink = findViewById(R.id.resetPasswordLink);
        TextView signUpLink = findViewById(R.id.signUpLink);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", BaseTransientBottomBar.LENGTH_LONG);

        SimpleTextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher();
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);
        loginButton.setOnClickListener(v -> submitLogin());
        resetPasswordLink.setOnClickListener(v -> goToPasswordReset());
        // Signup is no longer a launcher activity, so login provides the path to account creation.
        signUpLink.setOnClickListener(v -> goToSignup());
    }

    /**
     * Attempts login with Firebase Auth and opens the dashboard only after auth succeeds.
     */
    private void submitLogin() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        // Disable while Auth checks credentials to avoid duplicate login requests.
        loginButton.setEnabled(false);

        FirebaseManager.getInstance().signIn(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, RoleSpecificDashboardActivity.class));
                finish();
            } else {
                snackbar.setText("Invalid email or password");
                snackbar.show();
                updateLoginButtonState();
            }
        });
    }

    private void goToPasswordReset() {
        startActivity(new Intent(this, PasswordResetActivity.class));
    }

    private void goToSignup() {
        startActivity(new Intent(this, SignupActivity.class));
    }

    /**
     * Keeps login disabled until both credential fields have content.
     */
    private void updateLoginButtonState() {
        loginButton.setEnabled(FormValidator.isLoginComplete(
                emailInput.getText(),
                passwordInput.getText()
        ));
    }

    /**
     * Watches credential fields and updates the login button state after edits.
     */
    private SimpleTextWatcher createEmptyFieldsWatcher() {
        return new SimpleTextWatcher() {
            /**
             * Refreshes the login button after a credential field changes.
             *
             * @param s edited text from the watched field
             */
            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateLoginButtonState();
            }
        };
    }

}
