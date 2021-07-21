package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.EpisodesAdapter;
import com.example.platform.adapters.SimilarTitlesAdapter;
import com.example.platform.models.Episode;
import com.example.platform.models.Title;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TvTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TvTitleDetailsActivity";
    public String TV_SHOW_DETAILS_URL;
    public String SEASON_DETAILS_URL;
    public Integer seasonNumberAccessible;
    Context context;
    Intent intent;

    Integer titleTmdbID;
    String titleName;
    String titleCoverPath;
    String titleType;
    String titleDescription;
    String titleReleaseDate;
    Integer numberOfSeasons;
    Integer numberOfEpisodes;
    String genres;
    String networks;
    String actors; // First 3 actors for a TV show

    ImageView ivCover;
    TextView tvName;
    TextView tvDescription;
    TextView tvStarring;
    TextView tvReleaseDate;
    TextView tvGenres;
    TextView tvAvailableOn;
    ImageView ivFollowingStatus;
    TextView tvFollowingStatus;
    ImageView ivShare;
    TextView tvShare;
    ImageView ivLikeStatus;
    TextView tvLikeStatus;
    ImageView ivComment;
    TextView tvComment;
    TextView tvSeasons;
    TextView tvSeasonsText;
    TextView tvEpisodes;
    TextView tvEpisodesText;
    ProgressBar progressBar;

    RecyclerView rvEpisodesDisplay;
    List<Episode> allEpisodes;
    EpisodesAdapter adapter;

    RecyclerView rvSimilarTitlesDisplay;
    List<Title> similarTitles;
    SimilarTitlesAdapter titlesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_title_details);
        context = getApplicationContext();
        intent = getIntent();

        ivCover = findViewById(R.id.ivCover_TV_Details);
        tvName = findViewById(R.id.tvName_TV_Details);
        tvDescription = findViewById(R.id.tvDescription_TV_Details);
        tvStarring = findViewById(R.id.tvStarring_TV_Details);
        tvReleaseDate = findViewById(R.id.tvRelease_Date_TV_Details);
        tvGenres = findViewById(R.id.tvGenres_TV_Details);
        tvAvailableOn = findViewById(R.id.tvAvailable_On_TV_Details);
        ivFollowingStatus = findViewById(R.id.ivFollowingStatus_TV_Details);
        tvFollowingStatus = findViewById(R.id.tvFollowingStatusText_TV_Details);
        ivShare = findViewById(R.id.ivShare_TV_Details);
        tvShare = findViewById(R.id.tvShareText_TV_Details);
        ivLikeStatus = findViewById(R.id.ivLikeStatus_TV_Details);
        tvLikeStatus = findViewById(R.id.tvLikeStatusText_TV_Details);
        ivComment = findViewById(R.id.ivComment_TV_Details);
        tvComment = findViewById(R.id.tvCommentText_TV_Details);
        tvSeasons = findViewById(R.id.tvSeasons_TV_Details);
        tvSeasonsText = findViewById(R.id.tvSeasonsText_TV_Details);
        tvEpisodes = findViewById(R.id.tvEpisodes_TV_Details);
        tvEpisodesText = findViewById(R.id.tvEpisodesText_TV_Details);
        progressBar = findViewById(R.id.pbDetails_TV);

        // Get Title information
        getTitleInformation();

        // Set up the Recycler View for episodes display
        setEpisodeDisplay();
    }

    private void getTitleInformation() {
        showProgressBar();
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting title information...");
        titleName = (String) intent.getStringExtra("name");
        titleTmdbID = (Integer) intent.getIntExtra("id", 0);
        titleCoverPath = (String) intent.getStringExtra("posterPath");
        titleType = (String) intent.getStringExtra("type");
        titleDescription = (String) intent.getStringExtra("description");
        titleReleaseDate = (String) intent.getStringExtra("releaseDate");
        Log.i(TAG, "Opening the Title " + titleName + " with type: " + titleType + " and TMDB ID: " + titleTmdbID + " in TV Details");

        // Then get additional information from TMDB API
        TV_SHOW_DETAILS_URL = "https://api.themoviedb.org/3/tv/" + titleTmdbID + "?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&append_to_response=similar,credits";
        Log.i(TAG, "Title Details URL: " + TV_SHOW_DETAILS_URL);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(TV_SHOW_DETAILS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to getting additional title information");
                JSONObject jsonObject = json.jsonObject;

                try {
                    numberOfSeasons = jsonObject.getInt("number_of_seasons");
                    numberOfEpisodes = jsonObject.getInt("number_of_episodes");
                    genres = "- " + getGenresFormatted(jsonObject.getJSONArray("genres"));
                    networks = getNetworksFormatted(jsonObject.getJSONArray("networks"));
                    similarTitles = Title.fromJsonArray(jsonObject.getJSONObject("similar").getJSONArray("results"));
                    actors = getActorsFormatted(jsonObject.getJSONObject("credits").getJSONArray("cast"));
                    updateParseServerTitles(similarTitles);

                    // Now set Title information
                    setTitleInformation();

                    // Display Episodes in RecyclerView
                    displayEpisodesInDisplay();

                    // Display similar Titles in RecyclerView
                    displaySimilarTitlesInDisplay();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception for TV show details" + " Exception: " + e);
                    e.printStackTrace();
                } catch (ParseException e) {
                    Log.i(TAG, "Issue saving similar TV Show titles to parse server");
                    e.printStackTrace();
                }
                Log.i(TAG, "Successfully received additional information / Title: " + titleName + " / Episodes: " + numberOfEpisodes + " / Seasons: " + numberOfSeasons + " / Genres: " + genres + " / Networks: " + networks);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to get additional title information / Response: " + response + " / Error: " + throwable);
            }
        });
    }

    // First check if Title already exist in the Parse Server
    // Requires making a query for Titles within the server that contain the same unique TMDB ID #
    private void updateParseServerTitles(List<Title> newTitles) throws ParseException {
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
//        title.setLikes(0);
//        title.setShares(0);

        title.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + title.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + title.getName());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
                ParseQuery<ParseObject> updateQuery = query.whereEqualTo(Title.KEY_TMDB_ID, title.getId());
                try {
                    ParseObject parseObject = updateQuery.getFirst();
                    title.setLikes(3);
                    title.setParseObject(parseObject);
                    adapter.notifyDataSetChanged();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });

    }

    // Format the network JSON data into a String to be displayed in the app
    private String getNetworksFormatted(JSONArray networks) throws JSONException {
        StringBuilder formattedNetworks = new StringBuilder();

        for (int i = 0; i < networks.length(); i++) {
            JSONObject networkObject = networks.getJSONObject(i);
            String network = networkObject.getString("name");
            formattedNetworks.append(network);
            if (i != networks.length() - 1) { // Custom version String.join method as SDK is too low for the actual method
                formattedNetworks.append(", ");
            }
        }
        return formattedNetworks.toString();
    }

    // Format the genre JSON data into a String to be displayed in the app
    private String getGenresFormatted(JSONArray genres) throws JSONException {
        StringBuilder formattedGenres = new StringBuilder();

        for (int i = 0; i < genres.length(); i++) {
            JSONObject genreObject = genres.getJSONObject(i);
            String genre = genreObject.getString("name");
            formattedGenres.append(genre);
            if (i != genres.length() - 1) { // Custom version String.join method as SDK is too low for the actual method
                formattedGenres.append(", ");
            }
        }
        return formattedGenres.toString();
    }

    // Formatted the actors (credits) JSON data into a String to be displayed in the app
    private String getActorsFormatted(JSONArray actors) throws JSONException {
        if (actors.isNull(0)) { // If there is no cast information for the title
            return "No cast available";
        }

        StringBuilder formattedActors = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            JSONObject actorObject = actors.getJSONObject(i);
            String actor = actorObject.getString("name");
            formattedActors.append(actor);
            if (i != 2) {
                formattedActors.append(", ");
            }
        }
        return formattedActors.toString();
    }

    // Populate the view with Title data
    private void setTitleInformation() {
        tvName.setText(titleName);
        tvDescription.setText(titleDescription);
        tvStarring.setText(actors);
        tvGenres.setText(genres);
        tvReleaseDate.setText(titleReleaseDate);
        tvAvailableOn.setText(networks);
        tvSeasons.setText(String.valueOf(numberOfSeasons));
        tvEpisodes.setText(String.valueOf(numberOfEpisodes));

        if (numberOfSeasons == 1) {
            tvSeasonsText.setText("season");
        }
        if (numberOfEpisodes == 1) {
            tvEpisodesText.setText("spisode");
        }

        Glide.with(context)
                .load(titleCoverPath)
                //.placeholder(placeholder)
                //.error(placeholder)
                .centerCrop() // scale image to fill the entire ImageView
                //.transform(new RoundedCornersTransformation(radius, margin))
                .into(ivCover);

        hideProgressBar();
    }

    // Set up the RecyclerView to display the episodes for the title
    private void setEpisodeDisplay() {
        rvEpisodesDisplay = findViewById(R.id.rvEpisodesDisplay);
        allEpisodes = new ArrayList<>();
        adapter = new EpisodesAdapter(context, allEpisodes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvEpisodesDisplay.setLayoutManager(linearLayoutManager);
        rvEpisodesDisplay.setAdapter(adapter);
    }

    // Obtain episodes data by accessing each season of the title
    private void displayEpisodesInDisplay() {
        Log.i(TAG, "Title: " + titleName + "/Number of seasons: " + numberOfSeasons);
        for(int seasonNumber = 1; seasonNumber <= numberOfSeasons; seasonNumber++) { // for each season in the title
            SEASON_DETAILS_URL = "https://api.themoviedb.org/3/tv/" + titleTmdbID + "/season/" + seasonNumber + "?api_key=e2b0127db9175584999a612837ae77b1&language=en-US";
            Log.i(TAG, "Season Details URL: " + SEASON_DETAILS_URL);
            AsyncHttpClient seasonClient = new AsyncHttpClient();
            seasonNumberAccessible = seasonNumber; // seasonNumber cannot be accessed by inner for-loop so this is necessary

            seasonClient.get(SEASON_DETAILS_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "onSuccess to get season details");
                    JSONObject seasonJsonObject = json.jsonObject;
                    try {
                        JSONArray seasonEpisodes = seasonJsonObject.getJSONArray("episodes");
                        Log.i(TAG, "Episodes: " + seasonEpisodes.toString());
                        List<Episode> newEpisodes = Episode.fromJsonArray(seasonEpisodes); // TODO: only first 5 episodes are returned for now
                        updateParseServerEpisodes(newEpisodes);
                        allEpisodes.addAll(newEpisodes);
                        adapter.notifyDataSetChanged();
                        Log.i(TAG, "Episodes: " + allEpisodes.size());
                    } catch (JSONException e) {
                        Log.e(TAG, "Hit json exception for season details" + " Exception: " + e);
                        e.printStackTrace();
                    } catch (ParseException e) {
                        Log.i(TAG, "Issue saving Episode to parse server");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d(TAG, "onFailure to get season / Response: " + response + " / Error: " + throwable);
                }
            });
        }
    }

    // First check if Title already exist in the Parse Server
    // Requires making a query for Titles within the server that contain the same unique TMDB ID #
    private void updateParseServerEpisodes(List<Episode> newEpisodes) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Episode");

        for (Episode episode : newEpisodes) {
            query.whereEqualTo(Episode.KEY_TMDB_ID, episode.getId());
            if (query.count() == 0) {
                saveTitle(episode);
            }
        }
    }

    // Save Title in the Parse Server if it does not exist
    private void saveTitle(Episode episode) {
        episode.setId(episode.getId());
//        title.setLikes(0);
//        title.setShares(0);

        episode.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + episode.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + episode.getName());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
                ParseQuery<ParseObject> updateQuery = query.whereEqualTo(Episode.KEY_TMDB_ID, episode.getId());
                try {
                    ParseObject parseObject = updateQuery.getFirst();
                    episode.setParseObject(parseObject);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }

    public void displaySimilarTitlesInDisplay() {
        rvSimilarTitlesDisplay = findViewById(R.id.rvSimilarTitlesDisplay_TV);
        titlesAdapter = new SimilarTitlesAdapter(context, similarTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvSimilarTitlesDisplay.setLayoutManager(linearLayoutManager);
        rvSimilarTitlesDisplay.setAdapter(titlesAdapter);
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}