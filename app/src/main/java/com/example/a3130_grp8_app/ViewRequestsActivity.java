package com.example.a3130_grp8_app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        setContentView(R.layout.activity_view_requests);

        requestListView = findViewById(R.id.requestListView);
        noRequestsText = findViewById(R.id.noRequestsText);

        requestList = new ArrayList<>();
        requestKeys = new ArrayList<>();
        requests = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList);
        requestListView.setAdapter(adapter);

        String productId = getIntent().getStringExtra("productId");
        if (productId == null) productId = "dummy_product_001";
        loadRequests(productId);

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
                .setMessage("Email: " + request.getReceiverEmail() + "\nStatus: " + request.getStatus())
                .setPositiveButton("Accept", (dialog, which) ->
                        updateRequestStatus(requestKey, "accepted"))
                .setNegativeButton("Decline", (dialog, which) ->
                        updateRequestStatus(requestKey, "declined"))
                .setNeutralButton("Close", null)
                .show();
    }

    private void updateRequestStatus(String requestKey, String status) {
        FirebaseManager.updateRequestStatus(requestKey, status)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update request: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    // loads requests from firebase for the product
    private void loadRequests(String productId) {
        FirebaseManager.getInstance().getDb().getReference("requests")
                .orderByChild("productId")
                .equalTo(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        requestList.clear();
                        requestKeys.clear();
                        requests.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Request request = child.getValue(Request.class);
                            if (request != null) {
                                requestList.add(request.getReceiverEmail() + " - " + request.getStatus());
                                requestKeys.add(child.getKey());
                                requests.add(request);
                            }
                        }

                        if (requestList.isEmpty()) {
                            noRequestsText.setText("No offers have been submitted");
                            noRequestsText.setVisibility(View.VISIBLE);
                        } else {
                            noRequestsText.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        noRequestsText.setText("No offers have been submitted");
                        noRequestsText.setVisibility(View.VISIBLE);
                    }
                });
    }
}