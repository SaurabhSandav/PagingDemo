package com.redridgeapps.pagingdemo;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.redridgeapps.pagingdemo.model.SearchItem;

// PagedListAdapter<ItemModel, ViewHolder>
public class SearchListAdapter extends PagedListAdapter<SearchItem, SearchListViewHolder> {

    SearchListAdapter() {
        super(SearchItem.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SearchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SearchListViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }
}
