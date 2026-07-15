package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        String productName = intent.getStringExtra("PRODUCT_NAME");

        TextView productNameTextView = findViewById(R.id.productNameTextView);
        productNameTextView.setText(productName);
    }
}
