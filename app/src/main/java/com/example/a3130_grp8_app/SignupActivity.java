package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupActivity extends AppCompatActivity {

    private String[] roleSpinnerOptions = {"Select a Role", "Buyer", "Seller"};

    // View variables
    private EditText emailInput;
    private EditText passwordInput;
    private Spinner roleSpinner;
    private Button signUpButton;
    private TextView backToLoginLink;
    private Snackbar snackbar;

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
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signUpButton = findViewById(R.id.signUpButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);

        // Create the role spinner
        roleSpinner = createRoleSpinner(findViewById(R.id.roleSpinner));

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        // Create watcher that disables sign-up button when there is nothing in either the email or
        // the password inputs
        TextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher();
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);

        //Create spinner listener
        AdapterView.OnItemSelectedListener roleSpinnerSelectionListener = createRoleSpinnerSelectionListener();
        roleSpinner.setOnItemSelectedListener(roleSpinnerSelectionListener);

        //Create login link listener
        View.OnClickListener loginLinkListener = createBackToLoginLinkClickListener();
        backToLoginLink.setOnClickListener(loginLinkListener);

        // Create an on-click listener for the sign-up button to handle submission
        View.OnClickListener signUpButtonClickListener = createSignUpButtonClickListener();
        signUpButton.setOnClickListener(signUpButtonClickListener);
    }

    private Spinner createRoleSpinner(Spinner roleSpinner) {
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.roleSpinnerOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(aa);
        return roleSpinner;
    }

    private void checkSignUpButtonState() {
        signUpButton.setEnabled((passwordInput.getText().length() != 0) &&
                (emailInput.getText().length() != 0) && (roleSpinner.getSelectedItemPosition() != 0));
    }

    private TextWatcher createEmptyFieldsWatcher() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkSignUpButtonState();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }

    private AdapterView.OnItemSelectedListener createRoleSpinnerSelectionListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkSignUpButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private View.OnClickListener createSignUpButtonClickListener() {
        return view -> {
            // Check if email input is valid and a password has been entered
            if(EmailValidator.getInstance().isValid(emailInput.getText().toString()) &&
                    passwordInput.getText().length() != 0) {
                submitSignUp();
            } else {
                snackbar.setText("Error: Email is invalid");
                snackbar.show();
            }
        };
    }

    private View.OnClickListener createBackToLoginLinkClickListener() {
        return view -> {
            goToLogin();
        };
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void submitSignUp() {
        // Prevent duplicate submissions while Firebase Auth creates the account.
        signUpButton.setEnabled(false);

        User user = new User();
        user.setEmail_id(emailInput.getText().toString().trim());
        user.setPassword(passwordInput.getText().toString());
        user.setRole(roleSpinner.getSelectedItem().toString());

        // Create the Auth account first; the dashboard opens only after profile data is saved.
        FirebaseManager.createUser(user.getEmail_id(),
                        user.getPassword(),
                        user.getRole())
                .addOnSuccessListener(unused -> {
                    Intent roleSelectionActivityIntent = new Intent(this,
                            RoleSpecificDashboardActivity.class);

                    roleSelectionActivityIntent.putExtra("USER", user);

                    startActivity(roleSelectionActivityIntent);
                    finish();
                })
                .addOnFailureListener(error -> {
                    snackbar.setText("Error: could not create account");
                    snackbar.show();
                    // Re-enable the button only if the form is still valid.
                    checkSignUpButtonState();
                });
    }
}
