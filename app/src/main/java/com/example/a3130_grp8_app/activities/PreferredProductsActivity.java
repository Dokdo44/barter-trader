package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class PreferredProductsActivity extends AppCompatActivity {

    private PreferredProductTeasersAdapter preferredProductTeasersAdapter;
    private List<Product> preferredProducts;

    private ChildEventListener preferredProductsChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_preferred_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferredProductsMain),
                this::onApplyWindowResets);

        preferredProducts = new ArrayList<>();
        preferredProductTeasersAdapter = new PreferredProductTeasersAdapter(this, preferredProducts);
        RecyclerView preferredProductsRecyclerView = findViewById(R.id.preferredProductsRecyclerView);
        preferredProductsRecyclerView.setAdapter(preferredProductTeasersAdapter);
        preferredProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currUser = FirebaseManager.getInstance().getCurrentUser();
        FirebaseManager.getInstance().addChildEventListenerForUserPreferredProductIds(
                currUser.getUid(),
                preferredProductsChildEventListener
        );
    }

    private WindowInsetsCompat onApplyWindowResets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }
}
