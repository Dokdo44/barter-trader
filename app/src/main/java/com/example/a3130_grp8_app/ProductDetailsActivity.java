package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.android.material.snackbar.Snackbar;


public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName;
    private TextView productDescription;
    private TextView productLocation;
    private TextView productAvailability;
    private TextView productMarketValue;
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
        productDescription = findViewById(R.id.productDescription);
        productLocation = findViewById(R.id.productLocation);
        productAvailability = findViewById(R.id.productAvailability);
        productMarketValue = findViewById(R.id.productMarketValue);

        productId = getIntent().getStringExtra("productId");
        loadProductDetails();
    }

    private void loadProductDetails() {
        if (productId == null) {
            Snackbar.make(productDetailsView,"Product information unavailable", Snackbar.LENGTH_SHORT).show();
            return;
        }

        FirebaseManager.getProduct(productId).addOnSuccessListener(product -> {
            if (product == null) {
                Snackbar.make(productDetailsView,"Product not found", Snackbar.LENGTH_SHORT).show();
                return;
            }
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productLocation.setText("Location: " + product.getAddress());
            productAvailability.setText("Available: " + product.getDateAvailable());
            productMarketValue.setText("Approximate Value: $" + product.getPrice());
            productImage.setImageResource(R.drawable.ic_launcher_foreground);})
                .addOnFailureListener(error -> {
                    Snackbar.make(productDetailsView,"Unable to load product details", Snackbar.LENGTH_SHORT).show();
                });
    }

    private WindowInsetsCompat onApplyWindowInsets(
            View view,
            WindowInsetsCompat insets
    ){
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
        );
        return insets;
    }
}