package com.example.a3130_grp8_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.adapters.TransactionAdapter;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Request;
import com.example.a3130_grp8_app.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private FirebaseManager firebaseManager;

    private ListView transactionListView;
    private TextView noTransactionsText;

    private String userId;

    private final List<Transaction> transactions = new ArrayList<>();

    private boolean buyerLoaded = false;
    private boolean sellerLoaded = false;

    private int pendingProducts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        firebaseManager = FirebaseManager.getInstance();

        transactionListView = findViewById(R.id.transactionListView);
        noTransactionsText = findViewById(R.id.noTransactionsText);

        userId = getIntent().getStringExtra("USER_ID");
        // Testing only
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
                        buyerLoaded = true;
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
                        sellerLoaded = true;
                        checkFinishedLoading();
                    }
                }
        );
    }


    private void addCompletedTransactions(List<Request> requests) {
        for (Request request : requests) {
            if ("accepted".equals(request.getStatus())) {
                pendingProducts++;

                firebaseManager.getProductById(
                        request.getProductId(),
                        product -> {
                            Transaction transaction =
                                    new Transaction(
                                            product.getName(),
                                            product.getAddress(),
                                            request.getRequesterName(),
                                            request.getRequesterLocation(),
                                            request.getOfferedItems(),
                                            request.getStatus(),
                                            request.getTimestamp()
                                    );

                            transactions.add(transaction);
                            pendingProducts--;
                            checkFinishedLoading();
                        }
                );
            }
        }
    }


    private void checkFinishedLoading() {
        if (!buyerLoaded || !sellerLoaded || pendingProducts > 0) {
            return;
        }
        if (transactions.isEmpty()) {
            noTransactionsText.setVisibility(View.VISIBLE);
            transactionListView.setVisibility(View.GONE);
        } else {
            noTransactionsText.setVisibility(View.GONE);
            transactionListView.setVisibility(View.VISIBLE);
            TransactionAdapter adapter =
                    new TransactionAdapter(
                            this,
                            transactions
                    );
            transactionListView.setAdapter(adapter);
        }
    }
}