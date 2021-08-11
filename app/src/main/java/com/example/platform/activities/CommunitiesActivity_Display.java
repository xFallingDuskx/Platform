package com.example.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.adapters.CommunitiesAdapter;
import com.example.platform.adapters.TitlesSimpleAdapter;
import com.example.platform.models.Community;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommunitiesActivity_Display extends AppCompatActivity {

    public static final String TAG = "CommunitiesActivity_Display";
    Context context;
    Intent intent;
    String objective;
    String query;
    String genreName;
    TextView tvNotAvailable;
    Button btnCreateCommunity;

    RecyclerView rvCommunities;
    List<Community> allCommunities;
    CommunitiesAdapter adapter;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onResume() {
        super.onResume();
        rvCommunities.notifyAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_display);
        context = getApplicationContext();
        intent = getIntent();
        objective = intent.getStringExtra("objective");

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_Communities);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Set up view
        tvNotAvailable = findViewById(R.id.tvNotAvailable_Communities);
        btnCreateCommunity = findViewById(R.id.btnCreateCommunity);
        rvCommunities = findViewById(R.id.rvCommunities);
        allCommunities = new ArrayList<>();
        adapter = new CommunitiesAdapter(context, allCommunities);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        rvCommunities.setLayoutManager(gridLayoutManager);
        rvCommunities.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Add community data based on the objective
                if (objective.equals("search")) {
                    query = intent.getStringExtra("query");
                    handleSearch(query);
                } else if (objective.equals("all")) {
                    handleAll();
                } else if (objective.equals("popular")) {
                    handlePopular();
                } else if (objective.equals("genre")) {
                    genreName = intent.getStringExtra("genreName");
                    handleByGenre(genreName);
                } else { // objective == user (communities)
                    handleUserCommunities();
                }
            }
        }, 5000);

        // If user chooses to create a community
        btnCreateCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Create.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public void handleSearch(String query) {
        Log.i(TAG, "Entered handleSearch / Query: " + query);
        String queryStandardCasing = query.substring(0,1).toUpperCase() + query.substring(1).toLowerCase(); // Capitalize first letter
        String queryLowerCase = query.toLowerCase();
        String queryUpperCase = query.toUpperCase();

        ParseQuery<Community> nameQuery = ParseQuery.getQuery(Community.class);
        nameQuery.whereContains(Community.KEY_NAME, query);
        ParseQuery<Community> nameQueryStandard = ParseQuery.getQuery(Community.class);
        nameQueryStandard.whereContains(Community.KEY_NAME, queryStandardCasing);
        ParseQuery<Community> nameQueryLower = ParseQuery.getQuery(Community.class);
        nameQueryLower.whereContains(Community.KEY_NAME, queryLowerCase);
        ParseQuery<Community> nameQueryUpper = ParseQuery.getQuery(Community.class);
        nameQueryUpper.whereContains(Community.KEY_NAME, queryUpperCase);

        //Todo: consider removing queries for description
        ParseQuery<Community> descriptionQuery = ParseQuery.getQuery(Community.class);
        descriptionQuery.whereContains(Community.KEY_DESCRIPTION, query);
        ParseQuery<Community> descriptionQueryStandard = ParseQuery.getQuery(Community.class);
        descriptionQueryStandard.whereContains(Community.KEY_DESCRIPTION, queryStandardCasing);
        ParseQuery<Community> descriptionQueryLower = ParseQuery.getQuery(Community.class);
        descriptionQueryLower.whereContains(Community.KEY_DESCRIPTION, queryLowerCase);
        ParseQuery<Community> descriptionQueryUpper = ParseQuery.getQuery(Community.class);
        descriptionQueryUpper.whereContains(Community.KEY_DESCRIPTION, queryUpperCase);

        ParseQuery<Community> genreQuery = ParseQuery.getQuery(Community.class);
        genreQuery.whereEqualTo(Community.KEY_GENRES, queryStandardCasing);

        ParseQuery<Community> keywordQuery = ParseQuery.getQuery(Community.class);
        keywordQuery.whereEqualTo(Community.KEY_KEYWORDS, queryLowerCase);

        List<ParseQuery<Community>> allQueries = new ArrayList<ParseQuery<Community>>();
        allQueries.add(nameQuery);
        allQueries.add(nameQueryStandard);
        allQueries.add(nameQueryLower);
        allQueries.add(nameQueryUpper);
        allQueries.add(descriptionQuery);
        allQueries.add(descriptionQueryStandard);
        allQueries.add(descriptionQueryLower);
        allQueries.add(descriptionQueryUpper);
        allQueries.add(genreQuery);
        allQueries.add(keywordQuery);

        ParseQuery<Community> mainQuery = ParseQuery.or(allQueries);
        mainQuery.include(Community.KEY_NAME);
        mainQuery.include(Community.KEY_DESCRIPTION);

        mainQuery.addDescendingOrder(Community.KEY_NUMBER_OF_MEMBERS);
        mainQuery.findInBackground((communities, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue getting communities", e);
                return;
            }
            Log.i(TAG, "Success getting communities for the query / Communities: " + communities.toString());
            // For debugging
            for (Community community : communities) {
                Log.i(TAG, "Community: " + community.getName() + " / Description: " + community.getDescription());
            }
            allCommunities.addAll(communities);
            adapter.notifyDataSetChanged();
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);

            // if there are no fitting communities
            if (allCommunities.isEmpty()) {
                tvNotAvailable.setText(R.string.no_communities_search);
                tvNotAvailable.setVisibility(View.VISIBLE);
                btnCreateCommunity.setVisibility(View.VISIBLE);
            }
        });
    }

    public void handleAll() {
        ParseQuery<Community> parseQuery = ParseQuery.getQuery(Community.class);
        parseQuery.include(Community.KEY_NAME);
        parseQuery.include(Community.KEY_DESCRIPTION);
        parseQuery.addDescendingOrder("createdAt");
        parseQuery.findInBackground(new FindCallback<Community>() {
            @Override
            public void done(List<Community> communities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts", e);
                    return;
                }
                // For debugging
                for (Community community : communities) {
                    Log.i(TAG, "Community: " + community.getName() + " / Description: " + community.getDescription());
                }
                allCommunities.addAll(communities);
                adapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    public void handlePopular() {
        ParseQuery<Community> parseQuery = ParseQuery.getQuery(Community.class);
        parseQuery.include(Community.KEY_NAME);
        parseQuery.include(Community.KEY_DESCRIPTION);
        parseQuery.addDescendingOrder(Community.KEY_NUMBER_OF_MEMBERS);
        parseQuery.findInBackground(new FindCallback<Community>() {
            @Override
            public void done(List<Community> communities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts", e);
                    return;
                }
                // For debugging
                for (Community community : communities) {
                    Log.i(TAG, "Community: " + community.getName() + " / Description: " + community.getDescription());
                }
                allCommunities.addAll(communities);
                adapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    public void handleByGenre(String genre) {
        String genreStandardCase = genre.substring(0,1).toUpperCase() + genre.substring(1).toLowerCase(); // Capitalize first letter
        ParseQuery<Community> parseQuery = ParseQuery.getQuery(Community.class);
        parseQuery.whereEqualTo(Community.KEY_GENRES, genreStandardCase);
        parseQuery.include(Community.KEY_NAME);
        parseQuery.include(Community.KEY_DESCRIPTION);
        parseQuery.addDescendingOrder(Community.KEY_NUMBER_OF_MEMBERS);
        parseQuery.findInBackground(new FindCallback<Community>() {
            @Override
            public void done(List<Community> communities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts", e);
                    return;
                }
                // For debugging
                for (Community community : communities) {
                    Log.i(TAG, "Community: " + community.getName() + " / Description: " + community.getDescription());
                }
                allCommunities.addAll(communities);
                adapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                // if there are no fitting communities
                if (allCommunities.isEmpty()) {
                    tvNotAvailable.setText(R.string.no_communities_genre);
                    tvNotAvailable.setVisibility(View.VISIBLE);
                    btnCreateCommunity.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void handleUserCommunities() {
        ParseQuery<Community> parseQuery = ParseQuery.getQuery(Community.class);
        parseQuery.whereEqualTo(Community.KEY_MEMBERS, ParseUser.getCurrentUser().getUsername());
        parseQuery.include(Community.KEY_NAME);
        parseQuery.include(Community.KEY_DESCRIPTION);
        parseQuery.addDescendingOrder("updatedAt");
        parseQuery.findInBackground(new FindCallback<Community>() {
            @Override
            public void done(List<Community> communities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting posts", e);
                    return;
                }
                // For debugging
                for (Community community : communities) {
                    Log.i(TAG, "Community: " + community.getName() + " / Description: " + community.getDescription());
                }
                allCommunities.addAll(communities);
                adapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                // if there are no fitting communities
                if (allCommunities.isEmpty()) {
                    tvNotAvailable.setText(R.string.no_communities_user);
                    tvNotAvailable.setVisibility(View.VISIBLE);
                    btnCreateCommunity.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}