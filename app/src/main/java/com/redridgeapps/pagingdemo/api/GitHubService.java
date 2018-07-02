package com.redridgeapps.pagingdemo.api;

import com.redridgeapps.pagingdemo.model.SearchModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface GitHubService {

    // Default page size to be requested
    int DEFAULT_PER_PAGE = 25;

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("/search/repositories")
    Call<SearchModel> getSearchResults(
            @Query("q") String searchQuery,
            @Query("page") int page,
            @Query("per_page") int per_page
    );
}
