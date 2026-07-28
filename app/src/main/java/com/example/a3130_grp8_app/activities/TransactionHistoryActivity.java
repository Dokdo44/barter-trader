package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Request;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;

    private ListView transactionListView;
    private TextView noTransactionsText;

    private String userId;

    private final List<Request> transactions = new ArrayList<>();
    private final List<String> transactionNames = new ArrayList<>();

    private int loadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        firebaseManager = FirebaseManager.getInstance();

        transactionListView = findViewById(R.id.transactionListView);
        noTransactionsText = findViewById(R.id.noTransactionsText);

        userId = getIntent().getStringExtra("USER_ID");
        // For testing
        userId = "mqssMmUHXYh2cxqU47OxWAxqYUv1";

        loadBuyerHistory();
        loadSellerHistory();
    }

    private void loadBuyerHistory() {
        firebaseManager.getRequestsByReceiver(
                userId,
                new FirebaseManager.RequestValueEventListener() {
                    @Override
                    public void onGetRequests(List<Request> requests) {
                        addCompletedTransactions(requests);
                        checkFinishedLoading();
                    }
                }
        );
    }


    private void loadSellerHistory() {
        firebaseManager.getRequestsByProvider(
                userId,
                new FirebaseManager.RequestValueEventListener() {
                    @Override
                    public void onGetRequests(List<Request> requests) {
                        addCompletedTransactions(requests);
                        checkFinishedLoading();
                    }
                }
        );
    }


    private void addCompletedTransactions(List<Request> requests) {
        for (Request request : requests) {
            if ("accepted".equals(request.getStatus())) {
                transactions.add(request);
                transactionNames.add(request.getRequesterName());
            }
        }
    }


    private void checkFinishedLoading() {
        loadCount++;
        if (loadCount < 2) {
            return;
        }

        if (transactions.isEmpty()) {
            noTransactionsText.setVisibility(View.VISIBLE);
            transactionListView.setVisibility(View.GONE);
        } else {
            noTransactionsText.setVisibility(View.GONE);
            transactionListView.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            transactionNames
                    );
            transactionListView.setAdapter(adapter);
        }
    }
}