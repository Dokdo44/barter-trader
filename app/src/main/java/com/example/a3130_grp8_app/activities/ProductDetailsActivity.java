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
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;
import com.example.a3130_grp8_app.model.Chat;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

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
    private Button sendMessageToProviderButton;

    private View productDetailsView;

    private View.OnClickListener requestItemButtonClickListener = v -> {
        Intent intent = new Intent(this, RequestProductActivity.class);
        intent.putExtra(PRODUCT_EXTRA_TAG, product);
        startActivity(intent);
    };

    private View.OnClickListener sendMessgageToProviderButtonClickListener = v -> {
        try {
            FirebaseManager.getInstance().getCurrentUserProfile()
                    .addOnSuccessListener(user -> onUserSuccess(user, v))
                    .addOnFailureListener(e -> onUserFailure(e, v));
        } catch(FirebaseConnectionError e) {
            onUserFailure(e, v);
        }
    };

    private void onUserSuccess(User user, View v) {
        FirebaseManager.getInstance().getChatsForProduct(product.getId())
                .addOnSuccessListener(snapshot -> onChatsSnapshotSuccess(snapshot, user, v))
                .addOnFailureListener(e -> {onChatFailure(e, v);});
    }

    private void onUserFailure(Exception e, View view) {
        Snackbar.make(view,
                "Failed to get user profile: " + e.getMessage(),
                Snackbar.LENGTH_SHORT).show();
    }

    private void onChatsSnapshotSuccess(DataSnapshot snapshot, User user, View v) {
        Chat productChat = null;
        if(snapshot.exists()) {
            for(DataSnapshot snapshotChild : snapshot.getChildren()) {
                Chat chat = snapshotChild.getValue(Chat.class);
                assert chat != null;
                if(chat.getReceiverUid().equals(user.getId())) {
                    productChat = chat;
                }
            }
        }
        if(productChat != null) {
            onGetChatSuccess(productChat);
        } else {
            FirebaseManager.getInstance().getUserByUid(product.getProviderUid())
                    .addOnSuccessListener(provider -> getProviderSuccess(provider, user, v))
                    .addOnFailureListener(e -> getProviderFailure(e, v));
        }
    }

    private void getProviderSuccess(User provider, User receiver, View v) {
        FirebaseManager.getInstance().addChat(
                        provider.getEmail(),
                        receiver.getEmail(),
                        product.getName(),
                        product.getId(),
                        new HashMap<>(),
                        product.getProviderUid(),
                        receiver.getId())
                .addOnSuccessListener(this::onGetChatSuccess)
                .addOnFailureListener(e -> onGetChatFailure(e, v));
    }

    private void getProviderFailure(Exception e, View v) {
        Snackbar.make(v, "Failed to get provider user: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void onChatFailure(Exception e, View v) {
        Snackbar.make(v,
                "Failed to get chats: " + e.getMessage(),
                Snackbar.LENGTH_SHORT).show();
    }

    private void onGetChatSuccess(Chat chat) {
        Intent intent = new Intent(this, ProductChatActivity.class);
        intent.putExtra("CHAT", chat);
        startActivity(intent);
    }

    private void onGetChatFailure(Exception e, View v) {
        Snackbar.make(v,
                "Failed to create chat: " + e.getMessage(),
                Snackbar.LENGTH_SHORT).show();
    }

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
        sendMessageToProviderButton = findViewById(R.id.sendMessageToProviderButton);
    }

    private void addClickListeners() {
        goBackToListingsButton.setOnClickListener(v -> finish());
        requestItemButton.setOnClickListener(requestItemButtonClickListener);
        sendMessageToProviderButton.setOnClickListener(sendMessgageToProviderButtonClickListener);
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