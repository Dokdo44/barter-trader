package com.example.a3130_grp8_app.activities;

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

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity responsible for displaying detailed information about a product listing.
 *
 * Loads product information from Firebase and displays it which then allows users to request the product.
 */
public class ProductDetailsActivity extends AppCompatActivity {
    private static final String PRODUCT_EXTRA_TAG = "PRODUCT";

    private Product product;

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

    private View.OnClickListener requestItemButtonClickListener = v -> {
        Intent intent = new Intent(this, RequestProductActivity.class);
        intent.putExtra(PRODUCT_EXTRA_TAG, product);
        startActivity(intent);
    };

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

        initViews();
        addClickListeners();

        // Product ID passed from the search results or homepage.
        if(getIntent().hasExtra(PRODUCT_EXTRA_TAG)) {
            product = getIntent().getSerializableExtra(PRODUCT_EXTRA_TAG, Product.class);
            setViewsToProductDetails();
        } else if(getIntent().hasExtra("PRODUCT_ID")) {
            loadProductById(getIntent().getStringExtra("PRODUCT_ID"));
        }
    }

    private void initViews() {
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
    }

    private void addClickListeners() {
        goBackToListingsButton.setOnClickListener(v -> finish());
        requestItemButton.setOnClickListener(requestItemButtonClickListener);
    }

    private void loadProductById(String productId) {
        FirebaseManager.getInstance().getProduct(productId)
                .addOnSuccessListener(onFindProductByIdSuccess)
                .addOnFailureListener(error ->
                        Snackbar.make(
                                productDetailsView,
                                "Unable to load product details",
                                BaseTransientBottomBar.LENGTH_SHORT
                        ).show()
                );
    }

    private final OnSuccessListener<Product> onFindProductByIdSuccess = p -> {
        if(p == null) {
            Snackbar.make(
                    productDetailsView,
                    "Could not get product",
                    BaseTransientBottomBar.LENGTH_SHORT
            ).show();
        }

        this.product = p;

        setViewsToProductDetails();
    };

    private void setViewsToProductDetails() {
        assert product != null;

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

        String availabilityText = product.getDateAvailable();
        if(!product.isAvailable()) {
            availabilityText = "Product is no longer available";
        }
        productAvailability.setText(
                "Available: " + availabilityText
        );
        productMarketValue.setText(
                "Approximate Market Value: " + "$" + String.format("%.2f", product.getPrice())
        );
        productDescription.setText(
                product.getDescription()
        );
        datePosted.setText(
                "Date Posted: " + product.getTimeUploaded()
        );
        if (product.getPreferredItems() != null && !product.getPreferredItems().isEmpty()) {
            productPreferredItems.setText(
                    String.join(", ", product.getPreferredItems())
            );
        } else {
            productPreferredItems.setText("None specified");
        }
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