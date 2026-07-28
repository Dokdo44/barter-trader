package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Rating;

import java.util.ArrayList;

public class UserRatingActivity extends AppCompatActivity {

    private TextView averageRatingText;
    private LinearLayout ratingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rating);

        averageRatingText = findViewById(R.id.averageRatingText);
        ratingsContainer = findViewById(R.id.ratingsContainer);
        FirebaseManager firebaseManager = FirebaseManager.getInstance();

        String providerId = getIntent().getStringExtra("PROVIDER_ID");

        firebaseManager.getRatings(providerId, ratings -> {
            displayRatings(ratings);
        });
    }

    private void displayRatings(ArrayList<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            TextView noRatingsText = new TextView(this);
            noRatingsText.setText("No ratings yet");
            ratingsContainer.addView(noRatingsText);
            return;
        }

        // Display all ratings
        for (Rating rating : ratings) {
            TextView ratingView = new TextView(this);
            ratingView.setText(rating.getComment());
            ratingsContainer.addView(ratingView);
        }

        // Calculate and display average rating
        double average = ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0);

        averageRatingText.setText(String.valueOf(average));
    }
}