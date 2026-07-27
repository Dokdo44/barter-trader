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

/** Displays saved product preferences and delegates removal to its owning Activity. */
public class PreferredProductTeasersAdapter extends RecyclerView.Adapter<PreferredProductTeasersAdapter.ViewHolder> {

    /** Receives a product selected for removal. */
    public interface OnDeleteClickListener {
        /**
         * Handles a request to remove a saved product.
         *
         * @param product saved product selected by the receiver
         */
        void onDelete(Product product);
    }

    private final List<Product> preferredProducts;
    private final OnDeleteClickListener onDeleteClickListener;

    /** Holds the views that display one saved product. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;
        private final TextView productCategoriesTextView;
        private final Button deleteButton;

        /**
         * Creates a holder for a preferred-product row.
         *
         * @param view inflated row view
         */
        public ViewHolder(View view) {
            super(view);
            productNameTextView = view.findViewById(R.id.productNameTextView);
            productCategoriesTextView = view.findViewById(R.id.productCategoriesTextView);
            deleteButton = view.findViewById(R.id.preferredProductTeaserDeleteButton);
        }
    }

    /**
     * Creates an adapter for the receiver's saved product list.
     *
     * @param preferredProducts mutable list displayed by this adapter
     * @param onDeleteClickListener callback for removal actions
     */
    public PreferredProductTeasersAdapter(
            List<Product> preferredProducts, OnDeleteClickListener onDeleteClickListener) {
        this.preferredProducts = preferredProducts;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_preferred_product_teaser, parent, false);
        return new ViewHolder(view);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int getItemCount() {
        return preferredProducts.size();
    }
}
