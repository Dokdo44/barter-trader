package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends BaseAdapter {

    private final Context context;
    private final List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.transaction_item, parent, false);
        }


        TextView productNameText =
                convertView.findViewById(R.id.productNameText);
        TextView productAddressText =
                convertView.findViewById(R.id.productAddressText);
        TextView requesterText =
                convertView.findViewById(R.id.requesterText);
        TextView locationText =
                convertView.findViewById(R.id.locationText);
        TextView offeredItemsText =
                convertView.findViewById(R.id.offeredItemsText);
        TextView statusText =
                convertView.findViewById(R.id.statusText);
        TextView dateText =
                convertView.findViewById(R.id.dateText);

        Transaction transaction = transactions.get(position);

        productNameText.setText(
                "Product: " + transaction.getProductName()
        );
        productAddressText.setText(
                "Address: " + transaction.getProductAddress()
        );
        requesterText.setText(
                "Requester: " + transaction.getRequesterName()
        );
        locationText.setText(
                "Location: " + transaction.getRequesterLocation()
        );

        StringBuilder offeredItems = new StringBuilder();

        if (transaction.getOfferedItems() != null
                && !transaction.getOfferedItems().isEmpty()) {

            for (String item : transaction.getOfferedItems()) {
                offeredItems.append("- ")
                        .append(item)
                        .append("\n");
            }

        } else {
            offeredItems.append("None");
        }

        offeredItemsText.setText(
                "Offered Items:\n" + offeredItems
        );

        statusText.setText(
                "Status: " + transaction.getStatus()
        );

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "MMMM dd, yyyy",
                        Locale.getDefault()
                );
        String date =
                formatter.format(
                        new Date(transaction.getTimestamp())
                );
        dateText.setText(
                "Date: " + date
        );

        return convertView;
    }
}