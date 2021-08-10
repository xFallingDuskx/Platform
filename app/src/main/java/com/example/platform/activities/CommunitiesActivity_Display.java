package com.example.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.platform.R;
import com.example.platform.adapters.CommunitiesAdapter;
import com.example.platform.adapters.TitlesSimpleAdapter;
import com.example.platform.models.Community;
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

    RecyclerView rvCommunities;
    List<Community> allCommunities;
    CommunitiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_display);
        context = getApplicationContext();
        intent = getIntent();
        objective = intent.getStringExtra("objective");

        // Set up view
        rvCommunities = findViewById(R.id.rvCommunities);
        allCommunities = new ArrayList<>();
        adapter = new CommunitiesAdapter(context, allCommunities);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
        rvCommunities.setLayoutManager(gridLayoutManager);
        rvCommunities.setAdapter(adapter);

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

    public void handleSearch(String query) {
        Log.i(TAG, "Entered handleSearch / Query: " + query);
        String queryStandardCasing = query.substring(0,1).toUpperCase() + query.substring(1).toLowerCase(); // Capitalize first letter
        String queryLowerCase = query.toLowerCase();
        String queryUpperCase = query.toUpperCase();

        ParseQuery<Community> nameQuery = ParseQuery.getQuery(Community.class);
        nameQuery.whereFullText(Community.KEY_NAME, query);
        ParseQuery<Community> nameQueryStandard = ParseQuery.getQuery(Community.class);
        nameQueryStandard.whereFullText(Community.KEY_NAME, queryStandardCasing);
        ParseQuery<Community> nameQueryLower = ParseQuery.getQuery(Community.class);
        nameQueryLower.whereFullText(Community.KEY_NAME, queryLowerCase);
        ParseQuery<Community> nameQueryUpper = ParseQuery.getQuery(Community.class);
        nameQueryUpper.whereFullText(Community.KEY_NAME, queryUpperCase);

//        ParseQuery<Community> nameStartsWithQuery = ParseQuery.getQuery(Community.class);
//        nameStartsWithQuery.whereStartsWith(Community.KEY_NAME, query);

        ParseQuery<Community> descriptionQuery = ParseQuery.getQuery(Community.class);
        descriptionQuery.whereFullText(Community.KEY_DESCRIPTION, query);

        ParseQuery<Community> genreQuery = ParseQuery.getQuery(Community.class);
        genreQuery.whereEqualTo(Community.KEY_GENRES, Arrays.asList(query, queryStandardCasing, queryLowerCase, queryUpperCase));

        ParseQuery<Community> keywordQuery = ParseQuery.getQuery(Community.class);
        keywordQuery.whereEqualTo(Community.KEY_KEYWORDS, Arrays.asList(query, queryStandardCasing, queryLowerCase, queryUpperCase));

        List<ParseQuery<Community>> allQueries = new ArrayList<ParseQuery<Community>>();
        allQueries.add(nameQuery);
        allQueries.add(nameQueryStandard);
        allQueries.add(nameQueryLower);
        allQueries.add(nameQueryUpper);
//        allQueries.add(nameStartsWithQuery);
        allQueries.add(descriptionQuery);
        allQueries.add(genreQuery);
        allQueries.add(keywordQuery);

        ParseQuery<Community> mainQuery = ParseQuery.or(allQueries);
        mainQuery.include(Community.KEY_NAME);
        mainQuery.include(Community.KEY_DESCRIPTION);

        mainQuery.addDescendingOrder("createdAt");
        mainQuery.findInBackground(new FindCallback<Community>() {
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
            }
        });
    }
}