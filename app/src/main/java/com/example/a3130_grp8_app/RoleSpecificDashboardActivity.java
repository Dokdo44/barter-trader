package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class RoleSpecificDashboardActivity extends AppCompatActivity {

    private User user;

    private TextView userText;

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

        user = (User) getIntent().getSerializableExtra("USER");
        userText = findViewById(R.id.userText);

        userText.setText("User: " + user.getEmail() + " Role: " + user.getRole());
    }
}
