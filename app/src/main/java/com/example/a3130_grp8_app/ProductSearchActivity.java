package com.example.a3130_grp8_app;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.a3130_grp8_app.model.Product;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProductSearchActivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference productReference;

    private List<Product> products = new ArrayList<>();

    EditText searchField;

    LinearLayout productContainer;

    FirebaseManager manager = FirebaseManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_product_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productSearchConstraintLayout),
                (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(v -> onSearch());
        searchField = findViewById(R.id.searchEditText);
        productContainer = findViewById(R.id.searchResultsLayout);
        beforeSearchPage();
        searchButton.setEnabled(true);

    }

    private void beforeSearchPage()
    {

        loadProductsWithListener();
        displaySearchResults(productContainer, "", false);

    }
    private void onSearch()
    {
        loadProductsWithListener();
        displaySearchResults(productContainer, searchField.getText().toString(), true);

    }

    private void displaySearchResults(LinearLayout productContainer, String searchText, boolean isSearching)
    {
        productContainer.removeAllViews();

        for (Product product : products)
        {
            if (!isSearching) {
                TextView productView = new TextView(this);
                productView.setText(product.getName());
                productView.setTextSize(20);

                productContainer.addView(productView);
            }
            else
            {
                if (product.getName().contains(searchText))
                {
                    TextView productView = new TextView(this);
                    productView.setText(product.getName());
                    productView.setTextSize(20);

                    productContainer.addView(productView);
                }
                else
                {
                    continue;
                }
            }
        }
    }

    private void  sortProductsByDate()
    {
        products.sort((p1, p2) -> {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-DD HH:mm:ss");

            return p2.getTimeUploaded().compareTo(p1.getTimeUploaded());
        });
    }



    private void loadProductsWithListener()
    {
        products.clear();
        manager.addProductChildEventListener("timeUploaded", new FirebaseManager.ProductChildEventListener()
        {
            @Override
            public void onGetProduct (Product product)
            {
                if (product == null) return;
                products.add(product);
                sortProductsByDate();


            }


        });
    }
}
