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

import java.util.ArrayList;

public class PreferredProductsActivity extends AppCompatActivity {

    private PreferredProductTeasersAdapter preferredProductTeasersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_preferred_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.preferredProductsMain),
                this::onApplyWindowResets);

        preferredProductTeasersAdapter = new PreferredProductTeasersAdapter(this, new ArrayList<>());
        RecyclerView preferredProductsRecyclerView = findViewById(R.id.preferredProductsRecyclerView);
        preferredProductsRecyclerView.setAdapter(preferredProductTeasersAdapter);
        preferredProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private WindowInsetsCompat onApplyWindowResets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }
}
