package com.redridgeapps.pagingdemo.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.redridgeapps.pagingdemo.api.GitHubService;
import com.redridgeapps.pagingdemo.model.RequestFailure;
import com.redridgeapps.pagingdemo.model.SearchItem;
import com.redridgeapps.pagingdemo.model.SearchModel;
import com.redridgeapps.pagingdemo.util.function.Retryable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class SearchDataSource extends PageKeyedDataSource<Integer, SearchItem> {

    private final GitHubService service;
    private final String queryString;
    private final MutableLiveData<RequestFailure> requestFailureLiveData;

    public SearchDataSource(GitHubService service, String queryString) {
        this.service = service;
        this.queryString = queryString;
        this.requestFailureLiveData = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, SearchItem> callback) {

        // Initial page
        final int page = 1;

        // `params.requestedLoadSize` is chosen based on the options provided in PagedList.Config
        // while setting up PagedList.Builder. It'll use the `InitialLoadSizeHint` value if provided
        // or the `pageSize` value if not.
        Call<SearchModel> call = service.getSearchResults(queryString, page, params.requestedLoadSize);

        Callback<SearchModel> requestCallback = new Callback<SearchModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchModel> call, @NonNull Response<SearchModel> response) {
                SearchModel searchModel = response.body();

                if (searchModel == null) {
                    onFailure(call, new HttpException(response));
                    return;
                }

                // Result can be passed asynchronously
                callback.onResult(
                        searchModel.getSearchItems(), // List of data items
                        0, // Position of first item
                        searchModel.getTotalCount(), // Total number of items that can be fetched from api
                        null, // Previous page. `null` if there's no previous page
                        page + 1 // Next Page (Used at the next request). Return `null` if this is the last page.
                );
            }

            @Override
            public void onFailure(@NonNull Call<SearchModel> call, @NonNull Throwable t) {
                // Allow user to retry the failed request
                Retryable retryable = new Retryable() {
                    @Override
                    public void retry() {
                        loadInitial(params, callback);
                    }
                };

                handleError(retryable, t);
            }
        };

        call.enqueue(requestCallback);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, SearchItem> callback) {
        // This is not necessary in our case as our data doesn't change. It's useful in cases where
        // the data changes and we need to fetch our list starting from the middle.
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, SearchItem> callback) {

        // Next page.
        final int page = params.key;

        // `params.requestedLoadSize` is the `pageSize` value provided while setting up PagedList.Builder
        Call<SearchModel> call = service.getSearchResults(queryString, page, params.requestedLoadSize);

        Callback<SearchModel> requestCallback = new Callback<SearchModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchModel> call, @NonNull Response<SearchModel> response) {
                SearchModel searchModel = response.body();

                if (searchModel == null) {
                    onFailure(call, new HttpException(response));
                    return;
                }

                // Result can be passed asynchronously
                callback.onResult(
                        searchModel.getSearchItems(), // List of data items
                        // Next Page key (Used at the next request). Return `null` if this is the last page.
                        page + 1
                );
            }

            @Override
            public void onFailure(@NonNull Call<SearchModel> call, @NonNull Throwable t) {
                // Allow user to retry the failed request
                Retryable retryable = new Retryable() {
                    @Override
                    public void retry() {
                        loadAfter(params, callback);
                    }
                };

                handleError(retryable, t);
            }
        };

        call.enqueue(requestCallback);
    }

    public LiveData<RequestFailure> getRequestFailureLiveData() {
        return requestFailureLiveData;
    }

    private void handleError(Retryable retryable, Throwable t) {
        requestFailureLiveData.postValue(new RequestFailure(retryable, t.getMessage()));
    }
}
