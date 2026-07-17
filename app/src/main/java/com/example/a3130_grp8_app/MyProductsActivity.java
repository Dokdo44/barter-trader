package com.example.a3130_grp8_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class MyProductsActivity extends AppCompatActivity {

    private MyProductsAdapter myProductsAdapter;
    private RecyclerView productsRecyclerView;

    private FirebaseManager.ProductValueEventListener productValueEventListener =
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

        myProductsAdapter = new MyProductsAdapter(this, new ArrayList<>());
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setAdapter(myProductsAdapter);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseAuth auth = FirebaseManager.getInstance().getAuth();
        FirebaseManager.getInstance().getProductsByUserId(auth.getUid(),
                productValueEventListener);
    }
}
