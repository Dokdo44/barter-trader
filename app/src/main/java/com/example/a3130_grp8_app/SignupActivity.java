package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.util.FormValidator;
import com.example.a3130_grp8_app.util.SimpleTextWatcher;
import com.google.android.material.snackbar.Snackbar;

/**
 * SignupActivity keeps view wiring in the Activity and delegates validation/Firebase work to helpers.
 */
public class SignupActivity extends AppCompatActivity {

    private static final String[] ROLE_SPINNER_OPTIONS = {"Select a Role", "Buyer", "Seller"};

    // View variables
    private EditText emailInput;
    private EditText passwordInput;
    private Spinner roleSpinner;
    private Button signUpButton;
    private TextView backToLoginLink;
    private Snackbar snackbar;

    /**
     * Initializes the signup form, role spinner, navigation link, and submit handlers.
     *
     * @param savedInstanceState prior Activity state provided by Android
     */
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
        SimpleTextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher();
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
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                ROLE_SPINNER_OPTIONS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(aa);
        return roleSpinner;
    }

    private void checkSignUpButtonState() {
        signUpButton.setEnabled(FormValidator.isSignupComplete(
                emailInput.getText(),
                passwordInput.getText(),
                roleSpinner.getSelectedItemPosition()
        ));
    }

    private SimpleTextWatcher createEmptyFieldsWatcher() {
        return new SimpleTextWatcher() {
            /**
             * Refreshes signup availability after the email or password field changes.
             *
             * @param s edited text from the watched field
             */
            @Override
            public void afterTextChanged(android.text.Editable s) {
                checkSignUpButtonState();
            }
        };
    }

    private AdapterView.OnItemSelectedListener createRoleSpinnerSelectionListener() {
        return new AdapterView.OnItemSelectedListener() {
            /**
             * Refreshes signup availability when a role option is selected.
             *
             * @param parent spinner adapter view
             * @param view selected item view
             * @param position selected item index
             * @param id selected item row id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkSignUpButtonState();
            }

            /**
             * Handles the framework callback where no spinner item is selected.
             *
             * @param parent spinner adapter view
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private View.OnClickListener createSignUpButtonClickListener() {
        return view -> submitSignUpIfValid();
    }

    private View.OnClickListener createBackToLoginLinkClickListener() {
        return view -> goToLogin();
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void submitSignUpIfValid() {
        if (!FormValidator.isValidSignupEmail(emailInput.getText().toString())) {
            snackbar.setText("Error: Email is invalid");
            snackbar.show();
            return;
        }

        // Prevent duplicate submissions while Firebase Auth creates the account.
        signUpButton.setEnabled(false);

        // Create the Auth account first; the dashboard opens only after profile data is saved.
        FirebaseManager.createUser(emailInput.getText().toString().trim(),
                        passwordInput.getText().toString(),
                        roleSpinner.getSelectedItem().toString())
                .addOnSuccessListener(this::onCreateUserSuccess)
                .addOnFailureListener(this::onCreateUserFailure);
    }

    private void onCreateUserSuccess(Void unused) {
        Intent roleSelectionActivityIntent = new Intent(this,
                RoleSpecificDashboardActivity.class);
        startActivity(roleSelectionActivityIntent);
        finish();
    }

    private void onCreateUserFailure(Exception error) {
        snackbar.setText("Error: could not create account");
        snackbar.show();
        // Re-enable the button only if the form is still valid.
        checkSignUpButtonState();
    }
}
