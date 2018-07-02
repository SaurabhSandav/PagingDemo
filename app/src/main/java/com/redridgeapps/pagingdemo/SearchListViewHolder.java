package com.redridgeapps.pagingdemo;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redridgeapps.pagingdemo.model.SearchItem;

public class SearchListViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView description;

    private SearchListViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tv_title);
        description = itemView.findViewById(R.id.tv_description);
    }

    public static SearchListViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchListViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void bindTo(SearchItem searchItem) {
        // If placeholders are enabled, Paging will pass null first and then pass the actual data when it's available.
        if (searchItem != null) {
            title.setText(searchItem.getFullName());
            description.setText(searchItem.getDescription());
        } else {
            title.setText("Loading...");
            description.setText("Loading...");
        }
    }
}
