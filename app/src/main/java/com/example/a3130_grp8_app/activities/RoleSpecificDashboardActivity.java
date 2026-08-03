package com.example.a3130_grp8_app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.User;
import com.example.a3130_grp8_app.util.AndroidNotificationSender;
import com.example.a3130_grp8_app.util.DefaultPreferredCategoryMatcher;
import com.example.a3130_grp8_app.util.MessageNotificationManager;
import com.example.a3130_grp8_app.util.PreferredProductNotificationManager;

public class RoleSpecificDashboardActivity extends AppCompatActivity {

    private TextView dashboardEmail;
    private TextView dashboardRole;
    private Button viewMyChatsButton;
    private PreferredProductNotificationManager preferredProductNotificationManager;
    private MessageNotificationManager messageNotificationManager;

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
        Button transactionHistoryButton = findViewById(R.id.transactionHistoryButton);
        Button preferredProductsButton = findViewById(R.id.preferredProductsButton);

        // Add product button will start AddProduct activity. Only visible if user role is seller.
        productButton.setVisibility(View.GONE);
        productButton.setOnClickListener(v -> onAddProductButtonClick());

        // Add product button will start MyProducts activity. Only visible if user role is seller.
        myProductsButton.setVisibility(View.GONE);
        myProductsButton.setOnClickListener(v -> onMyProductsButtonClick());

        // Add product button will start ProductSearch activity. Only visible if user role is buyer.
        searchForProductsButton.setVisibility(View.GONE);
        searchForProductsButton.setOnClickListener(v -> onSearchForProductsButtonClick());

        // Add transaction history button will start TransactionHistory activity
        transactionHistoryButton.setOnClickListener(
                v -> onTransactionHistoryButtonClick()
        );

        // Logout clears the Auth session and returns to login.
        preferredProductsButton.setVisibility(View.GONE);
        preferredProductsButton.setOnClickListener(v -> onPreferredProductsButtonClick());

        // Logout clears the Auth session, stops notifications, and returns to login.
        logoutButton.setOnClickListener(v -> onLogoutButtonClick());

        viewMyChatsButton = findViewById(R.id.viewMyChatsButton);
        viewMyChatsButton.setOnClickListener(viewMyChatsButtonClickListener);

        loadCurrentUserProfile();
        askForPermissions();
        messageNotificationManager = new MessageNotificationManager(new AndroidNotificationSender(this));
        messageNotificationManager.start();
    }

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    100);
        }
    }

    /**
     * Starts listening for preferred product notifications, scoped to receivers (Buyer role).
     */
    private void startPreferredProductNotificationsIfBuyer() {
        if ("Role: Buyer".equals(dashboardRole.getText().toString())) {
            preferredProductNotificationManager = new PreferredProductNotificationManager(
                    FirebaseManager.getInstance(),
                    new AndroidNotificationSender(this),
                    new DefaultPreferredCategoryMatcher()
            );
            preferredProductNotificationManager.start();
        }
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
        if (preferredProductNotificationManager != null) {
            preferredProductNotificationManager.stop();
        }
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

        dashboardEmail.setText("Email: " + user.getEmail());
        dashboardRole.setText("Role: " + user.getRole());
        addProductButtonSetup();
        myProductsButtonSetup();
        searchForProductsButtonSetup();
        preferredProductsButtonSetup();
        startPreferredProductNotificationsIfBuyer();
    }

    private void onGetProfileFailure(Exception error) {
        dashboardEmail.setText("Could not load account");
        dashboardRole.setText("");
    }

    private void addProductButtonSetup() {
        Button productButton = findViewById(R.id.productButton);
        if (dashboardRole.getText().equals("Role: Seller")) {
            productButton.setVisibility(View.VISIBLE);
        }
    }

    private void onAddProductButtonClick() {
        startActivity(new Intent(this, AddProductActivity.class));
    }

    private void myProductsButtonSetup() {
        Button myProductsButton = findViewById(R.id.viewMyProductsButton);
        if (dashboardRole.getText().equals("Role: Seller")) {
            myProductsButton.setVisibility(View.VISIBLE);
        }
    }

    private void onMyProductsButtonClick() {
        startActivity(new Intent(this, MyProductsActivity.class));
    }

    private void searchForProductsButtonSetup() {
        Button searchForProductsButton = findViewById(R.id.searchForProductsButton);
        if (dashboardRole.getText().equals("Role: Buyer")) {
            searchForProductsButton.setVisibility(View.VISIBLE);
        }
    }

    private void onSearchForProductsButtonClick() {
        startActivity(new Intent(this, ProductSearchActivity.class));
    }

    private void onTransactionHistoryButtonClick() {
        startActivity(
                new Intent(this, TransactionHistoryActivity.class)
        );
    }

    private void preferredProductsButtonSetup() {
        Button preferredProductsButton = findViewById(R.id.preferredProductsButton);
        if (dashboardRole.getText().equals("Role: Buyer")) {
            preferredProductsButton.setVisibility(View.VISIBLE);
        }
    }

    private void onPreferredProductsButtonClick() {
        startActivity(new Intent(this, PreferredProductsActivity.class));
    }

    /**
     * Stops the notification listener when the activity is destroyed to avoid leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preferredProductNotificationManager != null) {
            preferredProductNotificationManager.stop();
        }
    }

    private View.OnClickListener viewMyChatsButtonClickListener = v -> {
        Intent intent = new Intent(this, ChatsListActivity.class);
        startActivity(intent);
    };
}
