package com.example.abhishekkoranne.news;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    private static final int NEWS_LOADER_ID = 1;

    private static final String THEGUARDIAN_NEWS_REQUEST_URL = "https://content.guardianapis.com/search?api-key=b8550f32-d817-498a-84d2-29335d3db618";

    private TextView emptyStateTextView;
    private ProgressBar loadingSpinner;

    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list);

        /**
         * TextView that is displayed when the list is empty
         */
        emptyStateTextView = (TextView) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyStateTextView);

        /*ProgressBar that is displayed till the data loads*/
        loadingSpinner = (ProgressBar) findViewById(R.id.loadingSpinner);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(newsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = newsAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            LoaderManager LoaderManager = getLoaderManager();
            LoaderManager.initLoader(NEWS_LOADER_ID, null, this).forceLoad();
        } else {
            loadingSpinner.setVisibility(View.GONE);
            emptyStateTextView.setText(getString(R.string.noInternet));
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, THEGUARDIAN_NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        emptyStateTextView.setText(getString(R.string.noNews));
        loadingSpinner.setVisibility(View.GONE);

        newsAdapter.clear();
        if (data != null && !data.isEmpty()) {
            newsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }
}