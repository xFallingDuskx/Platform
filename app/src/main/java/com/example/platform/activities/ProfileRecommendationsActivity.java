package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.EndlessRecyclerViewScrollListener;
import com.example.platform.R;
import com.example.platform.adapters.TitlesSimpleAdapter;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import cn.pedant.SweetAlert.SweetAlertDialog ;

public class ProfileRecommendationsActivity extends AppCompatActivity {

    public static final String TAG = "ProfileRecommendationsActivity";
    public String RECOMMENDATION_URL_BASE = "https://api.themoviedb.org/3/%s/%s/recommendations?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&page=1";
    int currentLikedTitlePosition = -1;
    ParseUser currentUser;
    Context context;
    ImageView ivBack;
    TextView tvNotAvailable;
    HashMap<String, String> userLikedTitles;
    List<String> likedTitlesIds;

    EndlessRecyclerViewScrollListener scrollListener;

    RecyclerView rvRecommendedTitles;
    TitlesSimpleAdapter adapter;
    List<Title> allTitles;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_recommendations);
        context = getApplicationContext();
        currentUser = ParseUser.getCurrentUser();

        // Alert users that the more they like, the more recommendations will appear
        // Only alert users who've never visited this activity before
        boolean visitedRecommendations = currentUser.getBoolean(User.KEY_VISITED_RECOMMENDATIONS);
        if (! visitedRecommendations) {
            new SweetAlertDialog(this).setTitleText("More Likes, More Recs").setContentText("Hey " + currentUser.getUsername() + ", the more TV shows and movies you like, the more recommendations we can give you!").show();
            currentUser.put(User.KEY_VISITED_RECOMMENDATIONS, true);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d(TAG, "Issue updating the boolean visitedRecommendations to true / Error: " + e.getMessage());
                    } else {
                        Log.i(TAG, "Success updating the boolean visitedRecommendations to true");
                    }
                }
            });
        }

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_Recommendations);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ProfileRecommendations);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_ProfileRecommendations);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNotAvailable = findViewById(R.id.tvNotAvailable_Recommendations);

        // Set up RecyclerView for titles
        rvRecommendedTitles = findViewById(R.id.rvRecommendations);
        allTitles = new ArrayList<>();
        adapter = new TitlesSimpleAdapter(context, allTitles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        rvRecommendedTitles.setLayoutManager(gridLayoutManager);
        rvRecommendedTitles.setAdapter(adapter);

        // Get titles liked by the user
        getLikedTitles();

        // Add titles to display
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.shuffle(likedTitlesIds);
                loadTitles();
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
        rvRecommendedTitles.addOnScrollListener(scrollListener);
    }

    public void getLikedTitles() {
        JSONObject jsonObject = currentUser.getJSONObject(User.KEY_LIKED_TITLES);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No titles currently liked by the user");
            tvNotAvailable.setVisibility(View.VISIBLE);
        } else {
            likedTitlesIds = new ArrayList<>();
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            //Convert Map to JSON
            try {
                userLikedTitles = mapper.readValue(json, new TypeReference<HashMap<String, String>>(){});
            } catch (JsonProcessingException e) {
                Log.d(TAG, "Issue accessing tiles liked by user");
                e.printStackTrace();
            }

            Log.i(TAG, "The returned query is: " + userLikedTitles.toString());

            for (Map.Entry<String, String> entry : userLikedTitles.entrySet()) {
                Log.i(TAG, "The title TMDB ID: " + entry.getKey());
                likedTitlesIds.add(entry.getKey());
            }
        }
    }

    public void loadTitles() {
        currentLikedTitlePosition++;
        String titleId = likedTitlesIds.get(currentLikedTitlePosition);
        String mediaType = userLikedTitles.get(titleId);
        String RECOMMENDATION_URL = String.format(RECOMMENDATION_URL_BASE, mediaType.toLowerCase(), titleId);
        Log.i(TAG, "Recommended Titles URL: " + RECOMMENDATION_URL);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(RECOMMENDATION_URL, new JsonHttpResponseHandler() {
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
        // Ensure there is another liked title to query for more recommendations
        if ((currentLikedTitlePosition + 1) == likedTitlesIds.size()) {
            Toast.makeText(context, "Approaching the end! You must like more titles for more recommendations", Toast.LENGTH_LONG).show();
            return;
        } else {
            loadTitles();
        }
    }
}