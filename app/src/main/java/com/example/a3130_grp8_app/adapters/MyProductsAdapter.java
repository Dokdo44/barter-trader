package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.activities.ViewRequestsActivity;
import com.example.a3130_grp8_app.model.Product;

import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.ViewHolder> {

    private List<Product> products;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTextView;
        private final ConstraintLayout productResultLayout;

        public ViewHolder(View view) {
            super(view);

            productNameTextView = (TextView)view.findViewById(R.id.productName);
            productResultLayout = (ConstraintLayout) view.findViewById(R.id.productResultLayout);
        }

        public TextView getProductNameTextView() {
            return productNameTextView;
        }
        public ConstraintLayout getProductViewBounds() { return productResultLayout; }
    }

    public MyProductsAdapter(Context context, List<Product> products) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_product_result, viewGroup, false);


        return new ViewHolder(view);
    }

    private View.OnClickListener getOnProductClickListener(Product product) {
        return view -> {
            Intent intent = new Intent(context, ViewRequestsActivity.class);
            intent.putExtra("PRODUCT", product);
            context.startActivity(intent);
        };
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getProductNameTextView().setText(products.get(position).getName());

        Product product = products.get(position);
        viewHolder.getProductViewBounds().setOnClickListener(getOnProductClickListener(product));
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
