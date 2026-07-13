package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

/**
 * Temporary activity to demonstrate request functionality independently of the product detail page.
 * Replace DUMMY_PRODUCT_ID and setupDummyProduct() with values passed via Intent
 * when integrating with the real product detail page.
 */
public class RequestProductActivity extends AppCompatActivity {

    // String productId = getIntent().getStringExtra("PRODUCT_ID");
    private static final String DUMMY_PRODUCT_ID = "dummy_product_001";

    private Button requestButton;
    private TextView requestSuccess;
    private TextView productTitle;
    private TextView productDescription;
    private TextView productCategory;
    private Snackbar snackbar;

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
        productTitle = findViewById(R.id.productTitle);
        productDescription = findViewById(R.id.productDescription);
        productCategory = findViewById(R.id.productCategory);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);

        // productTitle.setText(getIntent().getStringExtra("PRODUCT_TITLE"));
        // productDescription.setText(getIntent().getStringExtra("PRODUCT_DESCRIPTION"));
        // productCategory.setText(getIntent().getStringExtra("PRODUCT_CATEGORY"));
        setupDummyProduct();

        // requestButton.setOnClickListener(v -> submitRequest(productId));
        requestButton.setOnClickListener(v -> submitRequest(DUMMY_PRODUCT_ID));

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSpecificDashboardActivity.class));
            finish();
        });
    }

    /**
     * Sets up for testing purposes.
     * Remove when integrating with the real product detail page.
     */
    private void setupDummyProduct() {
        Product dummyProduct = new Product(
                "Dummy Test Bike",
                "A dummy product for testing requests",
                "1869 Upper Water St, Halifax, NS",
                0.0,
                Collections.singletonList("Sports"),
                true,
                null,
                "dummy_provider_uid",
                "2025-07-13",
                "2025-07-20",
                Collections.emptyList()
        );

        // Uses the same FirebaseManager instance as submitRequest so both point to the same DB
        FirebaseManager.getInstance().getDb()
                .getReference("product")
                .child(DUMMY_PRODUCT_ID)
                .setValue(dummyProduct);

        productTitle.setText(dummyProduct.getName());
        productDescription.setText(dummyProduct.getDescription());
        productCategory.setText(dummyProduct.getCategories().get(0));
    }

    /**
     * Submits a request for the given product ID.
     * This method is ready to be dropped into the product detail page as-is.
     *
     * @param productId the ID of the product to request
     */
    private void submitRequest(String productId) {
        requestButton.setEnabled(false);
        try {
            FirebaseManager.submitRequest(productId)
                    .addOnSuccessListener(unused -> {
                        requestButton.setVisibility(View.GONE);
                        requestSuccess.setVisibility(View.VISIBLE);
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
