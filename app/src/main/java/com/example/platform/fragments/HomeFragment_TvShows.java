package com.example.platform.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.EndlessRecyclerViewScrollListener;
import com.example.platform.R;
import com.example.platform.adapters.TitlesAdapter;
import com.example.platform.models.Title;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment_TvShows extends Fragment {

    public static final String TAG = "HomeFragment_TvShows";
    public String TRENDING_TV_SHOWS_URL_BASE = "https://api.themoviedb.org/3/trending/tv/day?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&page=%d";
    int desiredPage = 0;
    int startingPosition;

    RecyclerView rvTitles;
    List<Title> allTitles;
    TitlesAdapter adapter;
    EndlessRecyclerViewScrollListener scrollListener;
    ShimmerFrameLayout shimmerFrameLayout;

    public HomeFragment_TvShows() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home__tv_shows, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout_TV);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        rvTitles = view.findViewById(R.id.rvTitles_TV_Shows);
        allTitles = new ArrayList<>();
        adapter = new TitlesAdapter(getContext(), allTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTitles.setLayoutManager(linearLayoutManager);
        rvTitles.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadTitles();
            }
        }, 10000);

        // Endless Scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadTitles();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTitles.addOnScrollListener(scrollListener);
    }

    private void loadTitles() {
        desiredPage++;
        startingPosition = (desiredPage - 1) * 20;

        // Load more titles
        String TRENDING_TV_SHOWS_URL = String.format(TRENDING_TV_SHOWS_URL_BASE, desiredPage);
        Log.i(TAG, "Trending TV shows URL at starting position " + startingPosition + ": " + TRENDING_TV_SHOWS_URL);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(TRENDING_TV_SHOWS_URL, new JsonHttpResponseHandler() {
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
                    adapter.notifyItemRangeInserted(startingPosition, 20);
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
}