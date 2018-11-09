package com.example.abhishekkoranne.news;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int NEWS_LOADER_ID = 1;

    private static final String THEGUARDIAN_NEWS_REQUEST_URL = "https://content.guardianapis.com/search?api-key=b8550f32-d817-498a-84d2-29335d3db618&show-tags=contributor";

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

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
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals(getString(R.string.settingsSectionKey))
                || key.equals(getString(R.string.settingsOrderByKey))) {
            newsAdapter.clear();
            emptyStateTextView.setVisibility(View.GONE);
            View newsProgressbar = findViewById(R.id.loadingSpinner);
            newsProgressbar.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String section = sharedPreferences.getString(
                getString(R.string.settingsSectionKey),
                getString(R.string.settingsSectionByDefault)
        );
        String orderBy = sharedPreferences.getString(
                getString(R.string.settingsOrderByKey),
                getString(R.string.settingsOrderByDefault)
        );
        Uri baseUri = Uri.parse(THEGUARDIAN_NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("section", section);
        return new NewsLoader(this, uriBuilder.toString());

        //        return new NewsLoader(this, THEGUARDIAN_NEWS_REQUEST_URL);
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

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}