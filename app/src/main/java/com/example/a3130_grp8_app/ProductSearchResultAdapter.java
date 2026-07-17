package com.example.a3130_grp8_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.model.Product;

import java.util.List;

public class ProductSearchResultAdapter extends RecyclerView.Adapter<ProductSearchResultAdapter.ViewHolder> {

    private List<Product> products;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;

        public ViewHolder(View view) {
            super(view);

            productNameTextView = (TextView)view.findViewById(R.id.productName);
        }

        public TextView getProductNameTextView() {
            return productNameTextView;
        }
    }

    public ProductSearchResultAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_product_result, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getProductNameTextView().setText(products.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void resetData(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        this.notifyDataSetChanged();
    }
}
