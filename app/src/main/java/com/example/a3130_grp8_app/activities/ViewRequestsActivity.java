package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Product;
import com.example.a3130_grp8_app.model.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity responsible for displaying requests submitted for a provider's product.
 *
 * Loads requests from Firebase and allows providers to view requester details.
 */
public class ViewRequestsActivity extends AppCompatActivity {

    private ListView requestListView;
    private TextView noRequestsText;
    private List<String> requestList;
    private List<String> requestKeys;
    private List<Request> requests;
    private ArrayAdapter<String> adapter;

    /**
     * Initializes the activity, sets up UI components, listeners, and form validation.
     *
     * @param savedInstanceState the previously saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewRequestsMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestListView = findViewById(R.id.requestListView);
        noRequestsText = findViewById(R.id.noRequestsText);

        requestList = new ArrayList<>();
        requestKeys = new ArrayList<>();
        requests = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList);
        requestListView.setAdapter(adapter);

        String productId = getIntent().getStringExtra("PRODUCT_ID");
        if(getIntent().hasExtra("PRODUCT")) {
            Product product = getIntent().getSerializableExtra("PRODUCT", Product.class);
            assert product != null;
            assert product.getId() != null;
            productId = product.getId();
        }
        if (productId == null) productId = "dummy_product_001";
        FirebaseManager.getInstance().getRequests(productId, requestValueEventListener);

        TextView backButton = findViewById(R.id.backButtonViewRequests);
        backButton.setOnClickListener(v -> finish());

        // show requester details when clicked
        requestListView.setOnItemClickListener((parent, view, position, id) -> {
            String requestKey = requestKeys.get(position);
            Request request = requests.get(position);
            showRequestDetailsDialog(requestKey, request);
        });
    }

    private void showRequestDetailsDialog(String requestKey, Request request) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Requester Details")
                .setMessage("Email: " + request.getReceiverEmail() + "\nOffered items: "+ String.join(", ", request.getOfferedItems()) + "\nStatus: " + request.getStatus())
                .setPositiveButton("Accept", (dialog, which) ->
                        updateRequestStatus(requestKey, "accepted"))
                .setNegativeButton("Decline", (dialog, which) ->
                        updateRequestStatus(requestKey, "declined"))
                .setNeutralButton("Close", null)
                .show();
    }

    private void updateRequestStatus(String requestKey, String status) {
        FirebaseManager.getInstance().updateRequestStatus(requestKey, status)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update request: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    // loads requests from firebase for the product
    private FirebaseManager.RequestValueEventListener requestValueEventListener =
    new FirebaseManager.RequestValueEventListener() {
        @Override
        public void onGetRequests(List<Request> requests) {
            requestList.clear();
            requestKeys.clear();
            ViewRequestsActivity.this.requests.clear();
            for(Request request : requests) {
                requestList.add(request.getRequesterName() + " - " + request.getStatus());
                requestKeys.add(request.getRequestId());
                ViewRequestsActivity.this.requests.add(request);
            }

            if (requestList.isEmpty()) {
                noRequestsText.setText("No offers have been submitted");
                noRequestsText.setVisibility(View.VISIBLE);
            } else {
                noRequestsText.setVisibility(View.GONE);
            }

            adapter.notifyDataSetChanged();
        }
    };
}