package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.adapters.PreferredProductTeasersAdapter;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferredProductsActivity extends AppCompatActivity {
    private final List<Product> preferredProducts = new ArrayList<>();
    private final Map<String, String> preferenceKeyByProductId = new HashMap<>();
    private final Map<String, Chip> chipsByCategory = new HashMap<>();
    private boolean applyingSavedCategories;

    private PreferredProductTeasersAdapter preferredProductsAdapter;
    private View mainView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferred_products);

        mainView = findViewById(R.id.preferredProductsMain);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, this::onApplyWindowInsets);
        findViewById(R.id.preferredProductsBackButton).setOnClickListener(v -> finish());

        preferredProductsAdapter = new PreferredProductTeasersAdapter(
                preferredProducts, this::removePreferredProduct);
        RecyclerView recyclerView = findViewById(R.id.preferredProductsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(preferredProductsAdapter);

        bindCategoryChip(R.id.decorationChip, "Decoration");
        bindCategoryChip(R.id.sportsChip, "Sports");
        bindCategoryChip(R.id.technologyChip, "Technology");
        bindCategoryChip(R.id.personalCareChip, "Personal Care");
        bindCategoryChip(R.id.otherChip, "Other");

        FirebaseUser currentUser = FirebaseManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Snackbar.make(mainView, "Please sign in to view preferred products",
                    BaseTransientBottomBar.LENGTH_SHORT).show();
            return;
        }

        userId = currentUser.getUid();
        loadPreferredProducts();
        loadPreferredCategories();
    }

    private void bindCategoryChip(int chipId, String categoryName) {
        Chip chip = findViewById(chipId);
        chipsByCategory.put(categoryName, chip);
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!applyingSavedCategories) {
                updatePreferredCategory(categoryName, isChecked);
            }
        });
    }

    private void loadPreferredProducts() {
        FirebaseManager.getInstance().addChildEventListenerForUserPreferredProductIds(
                userId, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        String productId = snapshot.getValue(String.class);
                        if (productId == null) {
                            return;
                        }
                        preferenceKeyByProductId.put(productId, snapshot.getKey());
                        FirebaseManager.getInstance().getProduct(productId)
                                .addOnSuccessListener(product -> addPreferredProduct(productId, product));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildName) { }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {
                        String productId = snapshot.getValue(String.class);
                        if (productId != null) {
                            preferenceKeyByProductId.remove(productId);
                            removeProductFromDisplay(productId);
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildName) { }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Snackbar.make(mainView, "Could not load preferred products",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void addPreferredProduct(String productId, Product product) {
        if (product == null || preferenceKeyByProductId.containsKey(productId) == false) {
            return;
        }
        product.setId(productId);
        for (Product displayedProduct : preferredProducts) {
            if (productId.equals(displayedProduct.getId())) {
                return;
            }
        }
        preferredProducts.add(product);
        preferredProductsAdapter.notifyItemInserted(preferredProducts.size() - 1);
    }

    private void removePreferredProduct(Product product) {
        String preferenceId = preferenceKeyByProductId.get(product.getId());
        if (preferenceId == null) {
            return;
        }

        FirebaseManager.getInstance().deletePreferredProductForUser(userId, preferenceId)
                .addOnSuccessListener(unused -> {
                    preferenceKeyByProductId.remove(product.getId());
                    removeProductFromDisplay(product.getId());
                    Snackbar.make(mainView, "Preferred product deleted successfully",
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> Snackbar.make(mainView,
                        "Could not delete product: " + error.getMessage(),
                        BaseTransientBottomBar.LENGTH_SHORT).show());
    }

    private void removeProductFromDisplay(String productId) {
        for (int index = 0; index < preferredProducts.size(); index++) {
            if (productId.equals(preferredProducts.get(index).getId())) {
                preferredProducts.remove(index);
                preferredProductsAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void loadPreferredCategories() {
        FirebaseManager.getInstance().addChildEventListenerForUserPreferredCategoryNames(
                userId, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        String categoryName = snapshot.getValue(String.class);
                        Chip chip = chipsByCategory.get(categoryName);
                        if (chip != null) {
                            applyingSavedCategories = true;
                            chip.setChecked(true);
                            applyingSavedCategories = false;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildName) { }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {
                        String categoryName = snapshot.getValue(String.class);
                        Chip chip = chipsByCategory.get(categoryName);
                        if (chip != null) {
                            applyingSavedCategories = true;
                            chip.setChecked(false);
                            applyingSavedCategories = false;
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildName) { }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Snackbar.make(mainView, "Could not load preferred categories",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void updatePreferredCategory(String categoryName, boolean selected) {
        Task<Void> task = selected
                ? FirebaseManager.getInstance().addPreferredCategoryForUser(userId, categoryName)
                : FirebaseManager.getInstance().deletePreferredCategoriesForUser(userId, categoryName);
        if (task != null) {
            task.addOnFailureListener(error -> Snackbar.make(mainView,
                    "Could not update preferred category: " + error.getMessage(),
                    BaseTransientBottomBar.LENGTH_SHORT).show());
        }
    }

    private WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }
}
