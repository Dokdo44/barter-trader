package com.example.a3130_grp8_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3130_grp8_app.R;
import com.example.a3130_grp8_app.model.PreferredProduct;

import java.util.List;

public class PreferredProductTeasersAdapter extends RecyclerView.Adapter<PreferredProductTeasersAdapter.ViewHolder> {

    private List<PreferredProduct> preferredProducts;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView queryTextView;
        private final Button editButton;
        private final Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            queryTextView = (TextView)view.findViewById(R.id.queryTextView);
            editButton = (Button)view.findViewById(R.id.preferredProductTeaserEditButton);
            deleteButton = (Button)view.findViewById(R.id.preferredProductTeaserDeleteButton);
        }

        public TextView getQueryTextView() {
            return queryTextView;
        }

        public Button getEditButton() {
            return editButton;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
    }

    public PreferredProductTeasersAdapter(Context context, List<PreferredProduct> preferredProducts) {
        this.preferredProducts = preferredProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_preferred_product_teaser, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        PreferredProduct preferredProduct = preferredProducts.get(position);
        throw new UnsupportedOperationException("This function needs to be implemented");
    }

    @Override
    public int getItemCount() {
        return preferredProducts.size();
    }
}
