package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find relevant views
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button signUpButton = findViewById(R.id.signUpButton);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        // Create watcher that disables sign-up button when there is nothing in either the email or
        // the password inputs
        TextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher(signUpButton, emailInput, passwordInput);
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);

        View.OnClickListener signUpButtonClickListener = createSignUpButtonClickListener(emailInput,
                passwordInput, snackbar);
        signUpButton.setOnClickListener(signUpButtonClickListener);
    }

    private TextWatcher createEmptyFieldsWatcher(Button signUpButton,
                                                 EditText emailInput,
                                                 EditText passwordInput) {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                signUpButton.setEnabled((passwordInput.getText().length() != 0) &&
                        (emailInput.getText().length() != 0));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }

    private View.OnClickListener createSignUpButtonClickListener(EditText emailInput,
                                                                 EditText passwordInput,
                                                                 Snackbar snackbar) {
        return view -> {
            if(EmailValidator.getInstance().isValid(emailInput.getText().toString()) &&
                    passwordInput.getText().length() != 0) {
                Intent roleSelectionActivityIntent = new Intent(view.getContext(),
                        RoleSelectionActivity.class);
                startActivity(roleSelectionActivityIntent);
            } else {
                snackbar.setText("Error: Email is invalid");
                snackbar.show();
            }
        };
    }
}
