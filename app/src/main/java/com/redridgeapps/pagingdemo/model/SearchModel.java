package com.redridgeapps.pagingdemo.model;

import com.squareup.moshi.Json;

import java.util.List;

public class SearchModel {

    @Json(name = "total_count")
    private Integer totalCount;

    @Json(name = "incomplete_results")
    private Boolean incompleteResults;

    @Json(name = "items")
    private List<SearchItem> searchItems = null;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(Boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }

}
