package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.EndlessRecyclerViewScrollListener;
import com.example.platform.R;
import com.example.platform.adapters.TitlesSimpleAdapter;
import com.example.platform.models.Title;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CatalogActivity_TitleDisplay extends AppCompatActivity {

    private static final String TAG = "CatalogActivity_TitleDisplay";
    public String PLATFORM_DISCOVER_URL_BASE = "https://api.themoviedb.org/3/discover/%s?api_key=e2b0127db9175584999a612837ae77b1&with_watch_providers=%s&watch_region=US&language=en-US&sort_by=popularity.desc&page=%d";
    public String GENRE_DISCOVER_URL_BASE = "https://api.themoviedb.org/3/discover/%s?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&sort_by=popularity.desc&page=%d&with_genres=%s&with_original_language=en&with_watch_monetization_types=flatrate";
    int page = 0;
    Context context;

    RecyclerView rvGeneralTitles;
    TitlesSimpleAdapter adapter;
    List<Title> allTitles;
    TextView tvNoTitlesMessage;

    ShimmerFrameLayout shimmerFrameLayout;
    EndlessRecyclerViewScrollListener scrollListener;

    String objective;
    String type;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_title_display);
        context = getApplicationContext();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_Catalog_General);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Get necessary information for the view
        getInformation();

        // Set up RecyclerView for titles
        establishView();

        // Add titles to display
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadTitles();
            }
        }, 5000);
    }

    public void getInformation() {
        Log.i(TAG, "Getting necessary information...");
        Intent intent = getIntent();
        objective = (String) intent.getStringExtra("objective");
        id = (String) intent.getStringExtra("id");
        type = (String) intent.getStringExtra("type");

    }

    public void establishView() {
        tvNoTitlesMessage = findViewById(R.id.tvListedProviders_No_Match);
        rvGeneralTitles = findViewById(R.id.rvGeneralTitles);
        allTitles = new ArrayList<>();
        adapter = new TitlesSimpleAdapter(context, allTitles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        rvGeneralTitles.setLayoutManager(gridLayoutManager);
        rvGeneralTitles.setAdapter(adapter);

        // Endless Scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadTitles();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvGeneralTitles.addOnScrollListener(scrollListener);
    }

    public void loadTitles() {
        page++;
        String DISCOVER_URL;
        // Whether we are looking for titles based on Genre or Platform
        if (objective.equals("genre")) {
            // Whether we are looking to display all TV show titles or Movie titles based on Genre
            DISCOVER_URL = String.format(GENRE_DISCOVER_URL_BASE, type, page, id);
        } else {
            // Whether we are looking to display all TV show titles or Movie titles based on Platform
            DISCOVER_URL = String.format(PLATFORM_DISCOVER_URL_BASE, type, id, page);
        }

        Log.i(TAG, "The Discover Titles URL by " + objective + ": " + DISCOVER_URL);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(DISCOVER_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to display titles");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    List<Title> newTitles = Title.fromJsonArray(results);
                    updateParseServer(newTitles);
                    allTitles.addAll(newTitles);
                    adapter.notifyDataSetChanged();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Log.i(TAG, "Titles: " + allTitles.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                } catch (ParseException e) {
                    Log.e(TAG, "Issue updating Parse Server");
                    e.printStackTrace();
                }
                // Check if there are matches
                if (allTitles.isEmpty()) {
                    tvNoTitlesMessage.setVisibility(View.VISIBLE);
                    Log.i(TAG, "No Discover titles to display with the objective " +  objective + "/ Media type is " + type + " / Genre ID is " + id);
                } else {
                    Log.i(TAG, "Displaying Discover titles with the objective " +  objective + "/ Media type is " + type + " / Genre ID is " + id);

                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to display titles / Response: " + response + " / Error: " + throwable);
            }
        });
    }

    // First check if Title already exist in the Parse Server
    // Requires making a query for Titles within the server that contain the same unique TMDB ID #
    private void updateParseServer(List<Title> newTitles) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");

        for (Title title : newTitles) {
            query.whereEqualTo(Title.KEY_TMDB_ID, title.getId());
            if (query.count() == 0) {
                saveTitle(title);
            }
        }
    }

    // Save Title in the Parse Server if it does not exist
    private void saveTitle(Title title) {
        title.setId(title.getId());

        title.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + title.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + title.getName());
            }
        });
    }
}