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

        // Create the role spinner
        Spinner roleSpinner = createRoleSpinner(findViewById(R.id.roleSpinner));

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        // Create watcher that disables sign-up button when there is nothing in either the email or
        // the password inputs
        TextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher(
                signUpButton, emailInput, passwordInput, roleSpinner
        );
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);

        //Create spinner listener
        AdapterView.OnItemSelectedListener roleSpinnerSelectionListener = createRoleSpinnerSelectionListener(
                signUpButton, emailInput, passwordInput, roleSpinner
        );
        roleSpinner.setOnItemSelectedListener(roleSpinnerSelectionListener);

        // Create an on-click listener for the sign-up button to handle submission
        View.OnClickListener signUpButtonClickListener = createSignUpButtonClickListener(emailInput,
                passwordInput, snackbar);
        signUpButton.setOnClickListener(signUpButtonClickListener);
    }

    private Spinner createRoleSpinner(Spinner roleSpinner) {
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.roleSpinnerOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(aa);
        return roleSpinner;
    }

    private void checkSignUpButtonState(EditText emailInput, EditText passwordInput,
                                        Spinner roleSpinner, Button signUpButton) {
        signUpButton.setEnabled((passwordInput.getText().length() != 0) &&
                (emailInput.getText().length() != 0) && (roleSpinner.getSelectedItemPosition() != 0));
    }

    private TextWatcher createEmptyFieldsWatcher(Button signUpButton,
                                                 EditText emailInput,
                                                 EditText passwordInput,
                                                 Spinner roleSpinner) {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkSignUpButtonState(emailInput, passwordInput, roleSpinner, signUpButton);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
    }

    private AdapterView.OnItemSelectedListener createRoleSpinnerSelectionListener(Button signUpButton,
                                                 EditText emailInput,
                                                 EditText passwordInput,
                                                 Spinner roleSpinner) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkSignUpButtonState(emailInput, passwordInput, roleSpinner, signUpButton);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private View.OnClickListener createSignUpButtonClickListener(EditText emailInput,
                                                                 EditText passwordInput,
                                                                 Snackbar snackbar) {
        return view -> {
            // Check if email input is valid and a password has been entered
            if(EmailValidator.getInstance().isValid(emailInput.getText().toString()) &&
                    passwordInput.getText().length() != 0) {
                submitSignUp(emailInput.getText().toString(),
                        passwordInput.getText().toString(),
                        snackbar);
            } else {
                snackbar.setText("Error: Email is invalid");
                snackbar.show();
            }
        };
    }

    private void submitSignUp(String emailString, String passwordString, Snackbar snackbar) {
        try {
            FirebaseManager.addUser(emailString, passwordString, "Placeholder");
        } catch (FirebaseConnectionError e) {
            snackbar.setText("Error: could not connect to Firebase");
            snackbar.show();
            return;
        }

        Intent roleSelectionActivityIntent = new Intent(this,
                RoleSelectionActivity.class);
        startActivity(roleSelectionActivityIntent);
    }
}
