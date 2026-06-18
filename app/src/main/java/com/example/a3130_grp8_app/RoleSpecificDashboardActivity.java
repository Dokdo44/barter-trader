package com.example.a3130_grp8_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.User;

public class RoleSpecificDashboardActivity extends AppCompatActivity {

    private TextView dashboardEmail;
    private TextView dashboardRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_role_specific_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.roleSelectionMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dashboardEmail = findViewById(R.id.dashboardEmail);
        dashboardRole = findViewById(R.id.dashboardRole);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Logout clears the Auth session and returns to login.
        logoutButton.setOnClickListener(v -> {
            FirebaseManager.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        loadCurrentUserProfile();
    }

    private void loadCurrentUserProfile() {
        try {
            // The dashboard reads role/email from /users/{uid}, not from the Auth credential record.
            FirebaseManager.getCurrentUserProfile()
                    .addOnSuccessListener(user -> {
                        if (user == null) {
                            dashboardEmail.setText("Account profile not found");
                            dashboardRole.setText("");
                            return;
                        }

                        dashboardEmail.setText(user.getEmail());
                        dashboardRole.setText(user.getRole() + " dashboard");
                    })
                    .addOnFailureListener(error -> {
                        dashboardEmail.setText("Could not load account");
                        dashboardRole.setText("");
                    });
        } catch (FirebaseConnectionError e) {
            // A missing Auth session means this screen should not be visible.
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
