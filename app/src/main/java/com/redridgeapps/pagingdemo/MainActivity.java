package com.redridgeapps.pagingdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.readystatesoftware.chuck.ChuckInterceptor;
import com.redridgeapps.pagingdemo.api.GitHubService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    private GitHubService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupGitHubService();
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
}
