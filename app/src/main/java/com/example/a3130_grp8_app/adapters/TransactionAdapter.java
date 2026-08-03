package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.data.FirebaseManager;
import com.example.a3130_grp8_app.model.Transaction;
import com.example.a3130_grp8_app.exceptions.FirebaseConnectionError;

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

        TextView productDescriptionText =
                convertView.findViewById(R.id.productDescriptionText);

        TextView productAddressText =
                convertView.findViewById(R.id.productAddressText);

        TextView sellerNameText =
                convertView.findViewById(R.id.sellerNameText);

        TextView buyerNameText =
                convertView.findViewById(R.id.buyerNameText);

        TextView buyerLocationText =
                convertView.findViewById(R.id.buyerLocationText);

        TextView offeredItemsText =
                convertView.findViewById(R.id.offeredItemsText);

        TextView statusText =
                convertView.findViewById(R.id.statusText);

        TextView dateText =
                convertView.findViewById(R.id.dateText);

        Button rateExchangeButton =
                convertView.findViewById(R.id.rateExchangeButton);

        Transaction transaction = transactions.get(position);

        productNameText.setText(transaction.getProductName());

        productDescriptionText.setText(
                "Description: " + transaction.getProductDescription()
        );

        productAddressText.setText(
                "Address: " + transaction.getProductAddress()
        );

        sellerNameText.setText(
                "Seller: " + transaction.getSellerName()
        );

        buyerNameText.setText(
                "Buyer: " + transaction.getBuyerName()
        );

        buyerLocationText.setText(
                "Buyer Location: " + transaction.getBuyerLocation()
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

        bindStatus(statusText, transaction.getStatus());

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

        rateExchangeButton.setOnClickListener(v -> showRatingDialog(transaction));

        return convertView;
    }

    private void showRatingDialog(Transaction transaction) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_rate_exchange, null);

        RadioGroup ratingRadioGroup =
                dialogView.findViewById(R.id.ratingRadioGroup);

        EditText commentInput =
                dialogView.findViewById(R.id.ratingCommentInput);

        Button submitRatingButton =
                dialogView.findViewById(R.id.submitRatingButton);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        submitRatingButton.setOnClickListener(v -> {
            int selectedId = ratingRadioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedButton = dialogView.findViewById(selectedId);
            int ratingValue = Integer.parseInt(selectedButton.getText().toString());
            String comment = commentInput.getText().toString().trim();

            submitRating(transaction, ratingValue, comment, dialog);
        });

        dialog.show();
    }

    private void submitRating(Transaction transaction, int ratingValue,
                              String comment, AlertDialog dialog) {
        FirebaseManager firebaseManager = FirebaseManager.getInstance();

        try {
            firebaseManager.submitRating(transaction.getSellerId(), ratingValue, comment)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Rating submitted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                    );
        } catch (FirebaseConnectionError e) {
            Toast.makeText(context, "Could not connect to Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindStatus(TextView statusText, String status) {
        boolean accepted = "accepted".equalsIgnoreCase(status);
        int fillColor = ContextCompat.getColor(context,
                accepted ? R.color.md_success : R.color.md_error);
        int textColor = ContextCompat.getColor(context,
                accepted ? R.color.md_on_success : R.color.md_on_error);

        statusText.setBackgroundTintList(ColorStateList.valueOf(fillColor));
        statusText.setTextColor(textColor);

        String label = (status == null || status.isEmpty())
                ? "Unknown"
                : Character.toUpperCase(status.charAt(0)) + status.substring(1).toLowerCase();
        statusText.setText(label);
    }
}