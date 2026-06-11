package com.example.a3130_grp8_app;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Snackbar snackbar;
    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;

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
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);

        TextWatcher emptyFieldsWatcher = createEmptyFieldsWatcher();
        emailInput.addTextChangedListener(emptyFieldsWatcher);
        passwordInput.addTextChangedListener(emptyFieldsWatcher);
        loginButton.setOnClickListener(v -> submitLogin());
    }

    private void submitLogin() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            snackbar.setText("Invalid email or password");
                            snackbar.show();
                            return;
                        }

                        DataSnapshot userSnap = snapshot.getChildren().iterator().next();
                        String dataBasePassword = userSnap.child("password").getValue(String.class);

                        if (dataBasePassword != null && dataBasePassword.equals(password)) {
                            startActivity(new Intent(LoginActivity.this, RoleSpecificDashboardActivity.class));
                            finish();
                        } else {
                            snackbar.setText("Invalid email or password");
                            snackbar.show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        snackbar.setText("Could not connect to firebase");
                        snackbar.show();
                    }
                });
    }

    private TextWatcher createEmptyFieldsWatcher() {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(
                        emailInput.getText().length() != 0 && passwordInput.getText().length() != 0
                );
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
