package com.redridgeapps.pagingdemo;

import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.redridgeapps.pagingdemo.api.GitHubService;
import com.redridgeapps.pagingdemo.data.SearchDataSource;
import com.redridgeapps.pagingdemo.model.RequestFailure;
import com.redridgeapps.pagingdemo.model.SearchItem;
import com.redridgeapps.pagingdemo.util.MainThreadExecutor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    private MainThreadExecutor executor;
    private SearchListAdapter adapter;
    private GitHubService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executor = new MainThreadExecutor();

        setupGitHubService();
        setupSearch();
        setupRecyclerView();
    }

    private void setupGitHubService() {

        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .addInterceptor(new ChuckInterceptor(getApplicationContext()))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        service = retrofit.create(GitHubService.class);
    }

    private void setupSearch() {
        EditText searchEditText = findViewById(R.id.et_search);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setupDataSource(textView.getText().toString());
                    return true;
                }

                return false;
            }
        });
    }

    private void setupRecyclerView() {

        adapter = new SearchListAdapter();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setupDataSource(String queryString) {

        // Initialize Data Source
        SearchDataSource dataSource = new SearchDataSource(service, queryString);

        // Configure paging
        PagedList.Config config = new PagedList.Config.Builder()
                // Number of items to fetch at once. [Required]
                .setPageSize(GitHubService.DEFAULT_PER_PAGE)
                // Number of items to fetch on initial load. Should be greater than Page size. [Optional]
                .setInitialLoadSizeHint(GitHubService.DEFAULT_PER_PAGE * 2)
                .setEnablePlaceholders(true) // Show empty views until data is available
                .build();

        // Build PagedList
        PagedList<SearchItem> list =
                new PagedList.Builder<>(dataSource, config) // Can pass `pageSize` directly instead of `config`
                        // Do fetch operations on the main thread. We'll instead be using Retrofit's
                        // built-in enqueue() method for background api calls.
                        .setFetchExecutor(executor)
                        // Send updates on the main thread
                        .setNotifyExecutor(executor)
                        .build();

        // Ideally, the above code should be placed in a ViewModel class so that the list can be
        // retained across configuration changes.

        // Required only once. Paging will handle fetching and updating the list.
        adapter.submitList(list);

        dataSource.getRequestFailureLiveData().observe(this, new Observer<RequestFailure>() {
            @Override
            public void onChanged(@Nullable final RequestFailure requestFailure) {
                if (requestFailure == null) return;

                Snackbar.make(findViewById(android.R.id.content), requestFailure.getErrorMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Retry the failed request
                                requestFailure.getRetryable().retry();
                            }
                        }).show();
            }
        });
    }
}
