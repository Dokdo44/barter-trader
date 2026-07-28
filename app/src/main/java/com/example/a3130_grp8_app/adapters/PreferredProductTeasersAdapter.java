package com.example.a3130_grp8_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.model.Product;

import java.util.List;

public class PreferredProductTeasersAdapter extends RecyclerView.Adapter<PreferredProductTeasersAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Product product);
    }

    private final List<Product> preferredProducts;
    private final OnDeleteClickListener onDeleteClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;
        private final TextView productCategoriesTextView;
        private final Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            productNameTextView = view.findViewById(R.id.productNameTextView);
            productCategoriesTextView = view.findViewById(R.id.productCategoriesTextView);
            deleteButton = view.findViewById(R.id.preferredProductTeaserDeleteButton);
        }
    }

    public PreferredProductTeasersAdapter(
            List<Product> preferredProducts, OnDeleteClickListener onDeleteClickListener) {
        this.preferredProducts = preferredProducts;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_preferred_product_teaser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product preferredProduct = preferredProducts.get(position);
        holder.productNameTextView.setText(preferredProduct.getName());
        holder.productCategoriesTextView.setText(
                preferredProduct.getCategories() == null
                        ? ""
                        : String.join(", ", preferredProduct.getCategories())
        );
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDelete(preferredProduct));
    }

    @Override
    public int getItemCount() {
        return preferredProducts.size();
    }
}
