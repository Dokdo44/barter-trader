package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.android.material.snackbar.Snackbar;

/**
 * Handles product request submission for a receiver.
 * Checks availability and prevents duplicate requests before writing to Firebase.
 */
public class RequestActivity extends AppCompatActivity {

    private Button requestButton;
    private TextView requestSuccess;
    private Snackbar snackbar;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.requestProductMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestButton = findViewById(R.id.requestButton);
        requestSuccess = findViewById(R.id.requestSuccess);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);

        // Product ID is passed from the product detail screen
        productId = getIntent().getStringExtra("PRODUCT_ID");

        requestButton.setOnClickListener(v -> submitRequest());
    }

    /**
     * Submits a request for the current product via FirebaseManager.
     * Handles availability check, duplicate prevention, and database write.
     */
    private void submitRequest() {
        // Disable button to prevent duplicate taps while Firebase responds
        requestButton.setEnabled(false);

        try {
            FirebaseManager.submitRequest(productId)
                    .addOnSuccessListener(unused -> {
                        // Show success view and hide button
                        requestSuccess.setVisibility(android.view.View.VISIBLE);
                        requestButton.setVisibility(android.view.View.GONE);
                    })
                    .addOnFailureListener(error -> {
                        snackbar.setText(error.getMessage());
                        snackbar.show();
                        requestButton.setEnabled(true);
                    });
        } catch (FirebaseConnectionError e) {
            snackbar.setText("Could not connect to Firebase");
            snackbar.show();
            requestButton.setEnabled(true);
        }
    }
}
