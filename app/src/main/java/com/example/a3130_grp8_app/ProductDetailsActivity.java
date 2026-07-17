package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity responsible for displaying detailed information about a product listing.
 *
 * Loads product information from Firebase and displays it which then allows users to request the product.
 */
public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName;
    private TextView productCategory;
    private TextView productLocation;
    private TextView productAvailability;
    private TextView productMarketValue;
    private TextView productDescription;
    private TextView datePosted;
    private TextView productPreferredItems;

    private Button goBackToListingsButton;
    private Button requestItemButton;

    private View productDetailsView;
    private String productId;

    /**
     * Initializes product detail views and loads product information.
     *
     * @param savedInstanceState prior Activity state provided by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_product_details);

        productDetailsView = findViewById(R.id.productDetailsMain);

        ViewCompat.setOnApplyWindowInsetsListener(
                productDetailsView,
                this::onApplyWindowInsets
        );

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productCategory = findViewById(R.id.productCategory);
        productLocation = findViewById(R.id.productLocation);
        productAvailability = findViewById(R.id.productAvailability);
        productMarketValue = findViewById(R.id.productMarketValue);
        productDescription = findViewById(R.id.productDescription);
        datePosted = findViewById(R.id.datePosted);
        productPreferredItems = findViewById(R.id.productPreferredItems);

        goBackToListingsButton = findViewById(R.id.goBackToListingsButton);
        requestItemButton = findViewById(R.id.requestItemButton);

        goBackToListingsButton.setOnClickListener(v -> finish());

        requestItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestProductActivity.class);
            intent.putExtra("PRODUCT_ID", productId);
            startActivity(intent);
        });

        // Product ID passed from the search results or homepage.
        productId = getIntent().getStringExtra("productId");
        loadProductDetails();
    }

    private void loadProductDetails() {
        if (productId == null) {
            Snackbar.make(
                    productDetailsView,
                    "Product information unavailable",
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }

        FirebaseManager.getProduct(productId)
                .addOnSuccessListener(product -> {
                    if (product == null) {
                        Snackbar.make(
                                productDetailsView,
                                "Product not found",
                                Snackbar.LENGTH_SHORT
                        ).show();
                        return;
                    }
                    // Image URL to image
                    Glide.with(this)
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(productImage);
                    productName.setText(product.getName());
                    productCategory.setText(
                            "Category: " + String.join(", ", product.getCategories())
                    );
                    productLocation.setText(
                            "Location: " + product.getAddress()
                    );
                    productAvailability.setText(
                            "Available: " + product.isAvailable()
                    );
                    productMarketValue.setText(
                            "Approximate Market Value: " + product.getPrice() + "$"
                    );
                    productDescription.setText(
                            product.getDescription()
                    );
                    datePosted.setText(
                            "Date Posted" + product.getTimeUploaded()
                    );
                    if (product.getPreferredItems() != null && !product.getPreferredItems().isEmpty()) {
                        productPreferredItems.setText(
                                String.join(", ", product.getPreferredItems())
                        );
                    } else {
                        productPreferredItems.setText("None specified");
                    }

                    // Placeholder until Firebase Storage images are added.
                    productImage.setImageResource(
                            R.drawable.ic_launcher_foreground
                    );

                })
                .addOnFailureListener(error ->
                        Snackbar.make(
                                productDetailsView,
                                "Unable to load product details",
                                Snackbar.LENGTH_SHORT
                        ).show()
                );
    }

    private WindowInsetsCompat onApplyWindowInsets(
            View view,
            WindowInsetsCompat insets
    ){
        Insets systemBars =
                insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );
        view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
        );
        return insets;
    }
}