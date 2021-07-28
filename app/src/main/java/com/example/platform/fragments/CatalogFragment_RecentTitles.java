package com.example.platform.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.EndlessRecyclerViewScrollListener;
import com.example.platform.R;
import com.example.platform.activities.ProfileActivity;
import com.example.platform.activities.SearchActivity;
import com.example.platform.adapters.TitlesSimpleAdapter;
import com.example.platform.models.SharedCatalogViewModel;
import com.example.platform.models.Title;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CatalogFragment_RecentTitles extends Fragment {

    public static final String TAG = "CatalogFragment_RecentTitles";
    public String ALL_TITLES_ULR;
    private SharedCatalogViewModel sharedCatalogViewModel;
    EndlessRecyclerViewScrollListener scrollListener;

    String value;

    RecyclerView rvRecentTitles;
    TitlesSimpleAdapter adapter;
    List<Title> allTitles;

    EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    String dateFormatted;
    int tvPage = 1;
    int moviePage = 1;

    ShimmerFrameLayout shimmerFrameLayout;

    public CatalogFragment_RecentTitles() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog__recent_titles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout_Catalog_Recent);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Handle information shared between fragments
        sharedCatalogViewModel = new ViewModelProvider(requireActivity()).get(SharedCatalogViewModel.class);
        value = sharedCatalogViewModel.getValue();
        Log.i(TAG, "The value received is " + value);

        // Set up RecyclerView for titles
        rvRecentTitles = view.findViewById(R.id.rvRecentTitles);
        allTitles = new ArrayList<>();
        adapter = new TitlesSimpleAdapter(getContext(), allTitles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        rvRecentTitles.setLayoutManager(gridLayoutManager);
        rvRecentTitles.setAdapter(adapter);

        // Add titles to display
        // Current data is needed to avoid getting titles that have not yet released
        Date currentTime = Calendar.getInstance().getTime();
        dateFormatted = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayTitles();
            }
        }, 5000);

        // Endless Scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvRecentTitles.addOnScrollListener(scrollListener);
    }

    public void displayTitles() {
        // Whether we are looking to display all TV show titles or Movie titles
        if (value.equals("tv")) {
            ALL_TITLES_ULR = "https://api.themoviedb.org/3/discover/tv?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&sort_by=first_air_date.desc&first_air_date.lte=" + dateFormatted + "&page=" + tvPage + " &include_null_first_air_dates=false&with_original_language=en&with_watch_monetization_types=flatrate";
        } else {
            ALL_TITLES_ULR = "https://api.themoviedb.org/3/discover/movie?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&sort_by=primary_release_date.desc&include_adult=false&include_video=false&page=" + moviePage + "&release_date.lte=" + dateFormatted + "&with_original_language=en&with_watch_monetization_types=flatrate";
        }
        Log.i(TAG, "The All Titles URL: " + ALL_TITLES_ULR);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(ALL_TITLES_ULR, new JsonHttpResponseHandler() {
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

    // Endless Scrolling
    // Append the next page of data into the adapter
    public void loadNextDataFromApi() {
        // Whether we are looking to display all TV show titles or Movie titles
        if (value.equals("tv")) {
            tvPage++;
            ALL_TITLES_ULR = "https://api.themoviedb.org/3/discover/tv?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&sort_by=first_air_date.desc&first_air_date.lte=" + dateFormatted + "&page=" + tvPage + " &include_null_first_air_dates=false&with_original_language=en&with_watch_monetization_types=flatrate";
        } else {
            moviePage++;
            ALL_TITLES_ULR = "https://api.themoviedb.org/3/discover/movie?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&sort_by=primary_release_date.desc&include_adult=false&include_video=false&page=" + moviePage + "&release_date.lte=" + dateFormatted + "&with_original_language=en&with_watch_monetization_types=flatrate";
        }
        Log.i(TAG, "The All Titles URL: " + ALL_TITLES_ULR);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(ALL_TITLES_ULR, new JsonHttpResponseHandler() {
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
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to display titles / Response: " + response + " / Error: " + throwable);
            }
        });
    }
}