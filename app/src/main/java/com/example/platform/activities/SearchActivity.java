package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.SearchResultsAdapter;
import com.example.platform.models.Title;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SeachActivity";
    public String TITLES_SEARCH_URL_BASE = "https://api.themoviedb.org/3/search/%s?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&query=%s&page=1&include_adult=false";
    Context context;
    String query;
    String type;

    ImageView ivProfile;
    TextView tvNotAvailable;

    RecyclerView rvSearchResults;
    SearchResultsAdapter adapter;
    List<Title> allTitles;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = getApplicationContext();
        setToolBar();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_Search);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        tvNotAvailable = findViewById(R.id.tvNotAvailable_SearchResults);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        allTitles = new ArrayList<>();
        adapter = new SearchResultsAdapter(context, allTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSearchResults.setLayoutManager(linearLayoutManager);
        rvSearchResults.setAdapter(adapter);

        type = (String) getIntent().getStringExtra("type");
        query = (String) getIntent().getStringExtra("query");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performSearch(query);
            }
        }, 5000);
    }

    private void setToolBar() {
        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = findViewById(R.id.ivProfileIcon_Base);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_home, menu);

        // Allow user to search Titles
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Change appearance of EditText for the Search View
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHint("Search TV Shows and Movies");
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey_light));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search for Titles according to the query
                searchTitles(query);
                // Reset the Search View
                searchView.clearFocus();
                // searchView.setQuery("", false);
                // searchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void searchTitles(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    // Search TMDB for the titles that matches the query
    public void performSearch(String query) {
        // Different search depending on type
        // Types: "multi" - TV shows and movies / "tv" - TV shows / "movie" - movies
        String TITLES_SEARCH_URL = String.format(TITLES_SEARCH_URL_BASE, type, query);
        Log.i(TAG, "Search URL: " + TITLES_SEARCH_URL);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(TITLES_SEARCH_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to display titles");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    List<Title> newTitles = Title.fromJsonArray(results);
                    //updateParseServer(newTitles); TODO
                    allTitles.addAll(newTitles);
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Titles: " + allTitles.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (allTitles.isEmpty()) { // If not titles match the query
                    tvNotAvailable.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to display titles / Response: " + response + " / Error: " + throwable);
            }
        });
    }
}