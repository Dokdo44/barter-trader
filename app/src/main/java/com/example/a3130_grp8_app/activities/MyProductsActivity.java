package com.example.a3130_grp8_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.adapters.MyProductsAdapter;
import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyProductsActivity extends AppCompatActivity {

    private MyProductsAdapter myProductsAdapter;

    private final FirebaseManager.ProductValueEventListener productValueEventListener =
            new FirebaseManager.ProductValueEventListener() {
                @Override
                public void onGetProducts(List<Product> products) {
                    myProductsAdapter.resetData(products);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myProductsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myProductsAdapter = new MyProductsAdapter(this, new ArrayList<>());
        RecyclerView productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setAdapter(myProductsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView backButton = findViewById(R.id.backButtonMyProducts);
        backButton.setOnClickListener(v -> finish());

        FirebaseAuth auth = FirebaseManager.getInstance().getAuth();

        Button viewMyRatingsButton = findViewById(R.id.viewMyRatingsButton);
        viewMyRatingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserRatingActivity.class);
            intent.putExtra("PROVIDER_ID", auth.getUid());
            startActivity(intent);
        });

        FirebaseManager.getInstance().getProductsByUserId(auth.getUid(),
                productValueEventListener);
    }
}