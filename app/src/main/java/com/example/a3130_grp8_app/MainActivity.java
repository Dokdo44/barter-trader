package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.data.FirebaseManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // MainActivity is the only launcher; it routes based on the current Firebase Auth session.
        Class<?> nextActivity = FirebaseManager.getCurrentUser() == null
                ? LoginActivity.class
                : RoleSpecificDashboardActivity.class;

        startActivity(new Intent(this, nextActivity));
        finish();
    }
}
