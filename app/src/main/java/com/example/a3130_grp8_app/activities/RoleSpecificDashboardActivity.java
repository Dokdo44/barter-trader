package com.example.a3130_grp8_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.User;

public class RoleSpecificDashboardActivity extends AppCompatActivity {

    private TextView dashboardEmail;
    private TextView dashboardRole;

    /**
     * Initializes dashboard views, logout behavior, and profile loading.
     *
     * @param savedInstanceState prior Activity state provided by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_role_specific_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.roleSelectionMain),
                this::onApplyWindowResets);

        dashboardEmail = findViewById(R.id.dashboardEmail);
        dashboardRole = findViewById(R.id.dashboardRole);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button productButton = findViewById(R.id.productButton);
        Button myProductsButton = findViewById(R.id.viewMyProductsButton);
        Button searchForProductsButton = findViewById(R.id.searchForProductsButton);

        // Add product button will start AddProduct activity. Only visible if user role is seller.
        productButton.setVisibility(View.INVISIBLE);
        productButton.setOnClickListener(v -> onAddProductButtonClick());

        // Add product button will start MyProducts activity. Only visible if user role is seller.
        myProductsButton.setVisibility(View.INVISIBLE);
        myProductsButton.setOnClickListener(v -> onMyProductsButtonClick());

        // Add product button will start ProductSearch activity. Only visible if user role is buyer.
        searchForProductsButton.setVisibility(View.INVISIBLE);
        searchForProductsButton.setOnClickListener(v -> onSearchForProductsButtonClick());

        // Logout clears the Auth session and returns to login.
        logoutButton.setOnClickListener(v -> onLogoutButtonClick());

        loadCurrentUserProfile();
    }

    private void loadCurrentUserProfile() {
        try {
            // The dashboard reads role/email from /users/{uid}, not from the Auth credential record.
            FirebaseManager.getInstance().getCurrentUserProfile()
                    .addOnSuccessListener(this::onGetProfileSuccess)
                    .addOnFailureListener(this::onGetProfileFailure);
        } catch (FirebaseConnectionError e) {
            // A missing Auth session means this screen should not be visible.
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private WindowInsetsCompat onApplyWindowResets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    private void onLogoutButtonClick() {
        FirebaseManager.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void onGetProfileSuccess(User user) {
        if (user == null) {
            dashboardEmail.setText("Account profile not found");
            dashboardRole.setText("");
            return;
        }

        dashboardEmail.setText(user.getEmail());
        dashboardRole.setText(user.getRole() + " dashboard");
        addProductButtonSetup();
        myProductsButtonSetup();
        searchForProductsButtonSetup();
    }

    private void onGetProfileFailure(Exception error) {
        dashboardEmail.setText("Could not load account");
        dashboardRole.setText("");
    }

    private void addProductButtonSetup() {
        Button productButton = findViewById(R.id.productButton);
        if (dashboardRole.getText().equals("Seller dashboard")) {
            productButton.setVisibility(View.VISIBLE);
        }
    }

    private void onAddProductButtonClick() {
        startActivity(new Intent(this, AddProductActivity.class));
    }

    private void myProductsButtonSetup() {
        Button myProductsButton = findViewById(R.id.viewMyProductsButton);
        if (dashboardRole.getText().equals("Seller dashboard")) {
            myProductsButton.setVisibility(View.VISIBLE);
        }
    }

    private void onMyProductsButtonClick() {
       startActivity(new Intent(this, MyProductsActivity.class));
    }

    private void searchForProductsButtonSetup() {
        Button searchForProductsButton = findViewById(R.id.searchForProductsButton);
        if (dashboardRole.getText().equals("Buyer dashboard")) {
            searchForProductsButton.setVisibility(View.VISIBLE);
        }
    }

    private void onSearchForProductsButtonClick() {
        startActivity(new Intent(this, ProductSearchActivity.class));
    }
}
