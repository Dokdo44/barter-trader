package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the Send Request form for a product.
 * Accepts productId via Intent extra "PRODUCT_ID".
 */
public class RequestProductActivity extends AppCompatActivity {

    private String productId;
    private Button requestButton;
    private TextView requestSuccess;
    private EditText nameInput;
    private EditText locationInput;
    private LinearLayout checkboxContainer;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_product);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.requestProductMain), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        requestButton = findViewById(R.id.requestButton);
        requestSuccess = findViewById(R.id.requestSuccess);
        nameInput = findViewById(R.id.nameInput);
        locationInput = findViewById(R.id.locationInput);
        checkboxContainer = findViewById(R.id.checkboxContainer);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);

        productId = getIntent().getStringExtra("PRODUCT_ID");
        if(getIntent().hasExtra("PRODUCT")) {
            Product product = getIntent().getSerializableExtra("PRODUCT", Product.class);
            assert product != null;
            assert product.getId() != null;
            productId = product.getId();
        }

        loadProductPreferredItems(productId);

        requestButton.setOnClickListener(v -> submitRequest(productId));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    /**
     * Loads the product's preferred items from Firebase to populate checkboxes.
     *
     * @param productId the product to load preferred items for
     */
    private void loadProductPreferredItems(String productId) {
        if (productId == null) return;
        FirebaseManager.getInstance().getProduct(productId).addOnSuccessListener(product -> {
            if (product != null && product.getPreferredItems() != null) {
                populateCheckboxes(product.getPreferredItems());
            }
        });
    }

    /**
     * Dynamically creates a checkbox for each preferred item.
     *
     * @param preferredItems list of items the provider wants in exchange
     */
    private void populateCheckboxes(List<String> preferredItems) {
        checkboxContainer.removeAllViews();
        for (String item : preferredItems) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item);
            checkBox.setTextSize(16f);
            checkboxContainer.addView(checkBox);
        }
    }

    /**
     * Returns a list of checked item names from the checkbox container.
     */
    private List<String> getSelectedItems() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
            if (checkBox.isChecked()) {
                selected.add(checkBox.getText().toString());
            }
        }
        return selected;
    }

    /**
     * Submits a request for the given product ID.
     *
     * @param productId the ID of the product to request
     */
    private void submitRequest(String productId) {
        requestButton.setEnabled(false);
        String name = nameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        List<String> selectedItems = getSelectedItems();

        try {
            FirebaseManager.getInstance().submitRequest(productId, name, location, selectedItems)
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
