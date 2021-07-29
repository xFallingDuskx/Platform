package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.platform.R;
import com.example.platform.adapters.ProfileCommentsAdapter;
import com.example.platform.adapters.ProfileFollowingsAdapter;
import com.example.platform.adapters.SearchResultsAdapter;
import com.example.platform.models.Comment;
import com.example.platform.models.Title;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileFollowingActivity extends AppCompatActivity {

    public static final String TAG = "ProfileFollowingActivity";
    Context context;
    ImageView ivBack;
    TextView tvNotAvailable;

    RecyclerView rvSearchResults;
    ProfileFollowingsAdapter adapter;
    List<Title> allTitles;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_following);
        context = getApplicationContext();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_ProfileFollowing);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ProfileFollowing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_ProfileFollowing);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNotAvailable = findViewById(R.id.tvNotAvailable_ProfileFollowing);

        // Set up RecyclerView
        rvSearchResults = findViewById(R.id.rvComments_ProfileFollowing);
        allTitles = new ArrayList<>();
        adapter = new ProfileFollowingsAdapter(context, allTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSearchResults.setLayoutManager(linearLayoutManager);
        rvSearchResults.setAdapter(adapter);

        // Add titles to RV to be displayed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    displayComments();
                } catch (JSONException e) {
                    Log.d(TAG, "Issue loading the titles being followed by the user");
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    public void displayComments() throws JSONException {
        ParseUser currentUser = ParseUser.getCurrentUser();
        JSONArray userFollowingTitles = currentUser.getJSONArray("following");

        if (userFollowingTitles == null) { // If the user is not currently following any titles
            Log.i(TAG, "The user is not currently following any titles");
            tvNotAvailable.setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < userFollowingTitles.length(); i++) {
            JSONObject jsonObject = new JSONObject(userFollowingTitles.getString(i));
            Title title = new Title();
            title.setName(jsonObject.getString(Title.KEY_NAME));
            title.setId(jsonObject.getInt(Title.KEY_TMDBID_LOWER));
            title.setPosterPath(jsonObject.getString(Title.KEY_POSTERPATH));
            title.setType(jsonObject.getString(Title.KEY_TYPE));
            title.setDescription(jsonObject.getString(Title.KEY_DESCRIPTION));
            title.setReleaseDate(jsonObject.getString(Title.KEY_RELEASE_DATE));
            allTitles.add(title);
        }
        adapter.notifyDataSetChanged();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }
}