package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.view.Gravity;
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

        TextView backButton = findViewById(R.id.backButtonUserRating);
        backButton.setOnClickListener(v -> finish());

        String providerId = getIntent().getStringExtra("PROVIDER_ID");

        firebaseManager.getRatings(providerId, ratings -> {
            displayRatings(ratings);
        });
    }

    private void displayRatings(ArrayList<Rating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            TextView noRatingsText = new TextView(this);
            noRatingsText.setText("No ratings yet");
            noRatingsText.setTextSize(16f);
            noRatingsText.setGravity(Gravity.CENTER);
            ratingsContainer.addView(noRatingsText);
            return;
        }

        // Display all ratings
        for (Rating rating : ratings) {
            ratingsContainer.addView(createRatingView(rating));
        }

        // Calculate and display average rating
        double average = ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0);

        averageRatingText.setText(String.valueOf(average));
    }

    private TextView createRatingView(Rating rating) {
        TextView ratingView = new TextView(this);
        ratingView.setText(rating.getComment());
        ratingView.setTextSize(16f);
        ratingView.setPadding(32, 32, 32, 32);
        ratingView.setBackgroundResource(R.drawable.list_item_border);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        ratingView.setLayoutParams(params);

        return ratingView;
    }
}