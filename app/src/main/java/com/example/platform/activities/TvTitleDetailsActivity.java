package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.EpisodesAdapter;
import com.example.platform.adapters.TitlesAdapter;
import com.example.platform.models.Episode;
import com.example.platform.models.Title;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;

public class TvTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TvTitleDetailsActivity";
    public String TV_SHOW_DETAILS_URL;
    public String SEASON_DETAILS_URL;
    public Integer seasonNumberAccessible;
    public String EPISODE_DETAILS_URL;
    Context context;

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

    RecyclerView rvEpisodesDisplay;
    List<Episode> allEpisodes;
    EpisodesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_title_details);
        context = getApplicationContext();

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

        // Get Title information
        getTitleInformation();

        // Set up the Recycler View for episodes display
        setEpisodeDisplay();
    }

    private void getTitleInformation() {
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting title information...");
        titleName = (String) getIntent().getStringExtra(Title.KEY_NAME);
        titleTmdbID = (Integer) getIntent().getIntExtra( Title.KEY_TMDB_ID, 0);
        titleCoverPath = (String) getIntent().getStringExtra(Title.KEY_COVER_PATH);
        titleType = (String) getIntent().getStringExtra(Title.KEY_TYPE);
        titleDescription = (String) getIntent().getStringExtra(Title.KEY_DESCRIPTION);
        titleReleaseDate = (String) getIntent().getStringExtra(Title.KEY_RELEASE_DATE);
        Log.i(TAG, "Opening the Title " + titleName + " with type: " + titleType + " and TMDB ID: " + titleTmdbID + " in TV Details");

        // Then get additional information from TMDB API
        TV_SHOW_DETAILS_URL = "https://api.themoviedb.org/3/tv/" + titleTmdbID + "?api_key=e2b0127db9175584999a612837ae77b1&language=en-US";
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

                    // Now set Title information
                    setTitleInformation();

                    // Display Episodes in RecyclerView
                    displayEpisodesInDisplay();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
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

    private void setTitleInformation() {
        tvName.setText(titleName);
        tvDescription.setText(titleDescription);
        tvStarring.setText("Actor, Actor, ..."); //todo
        tvGenres.setText(genres);
        tvReleaseDate.setText(titleReleaseDate);
        tvAvailableOn.setText(networks); //todo
        tvSeasons.setText(String.valueOf(numberOfSeasons)); //todo
        tvEpisodes.setText(String.valueOf(numberOfEpisodes)); //todo

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
    }

    private void setEpisodeDisplay() {
        rvEpisodesDisplay = findViewById(R.id.rvEpisodesDisplay);
        allEpisodes = new ArrayList<>();
        adapter = new EpisodesAdapter(context, allEpisodes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvEpisodesDisplay.setLayoutManager(linearLayoutManager);
        rvEpisodesDisplay.setAdapter(adapter);
    }

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
                        List<Episode> newEpisodes = Episode.fromJsonArray(seasonEpisodes);
                        List<List<String>> newEpisodeInformation = Episode.getStringFormattedData(newEpisodes);
                        updateParseServer(newEpisodeInformation); // Purpose of saving to Parse
                        allEpisodes.addAll(newEpisodes);
                        adapter.notifyDataSetChanged();
                        Log.i(TAG, "Episodes: " + allEpisodes.size());
                    } catch (JSONException e) {
                        Log.e(TAG, "Hit json exception" + " Exception: " + e);
                        e.printStackTrace();
                    } catch (ParseException e) {
                        Log.e(TAG, "Hit ParseException while attempting to updateParseServer / Message: " + e.getMessage());
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

    // Check if Episode already exist in the Parse Server
    // Requires making a query for Episodes within the server that contain the same unique TMDB ID #
    private void updateParseServer(List<List<String>> newEpisodeInformation) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Episode");

        for (List<String> episodeInfo : newEpisodeInformation) {
            Integer titleID = Integer.valueOf(episodeInfo.get(0));
            query.whereEqualTo(Title.KEY_TMDB_ID, titleID);
            if (query.count() == 0) {
                saveEpisode(episodeInfo);
            }
        }
    }

    // Save Episode in the Parse Server if it does not exist
    private void saveEpisode(List<String> episodeInfo) {
        Episode newEpisode = new Episode();
        newEpisode.setID(Integer.valueOf(episodeInfo.get(0)));
        newEpisode.setName(episodeInfo.get(1));
        newEpisode.setTitleName(titleName);
        newEpisode.setStillPath(episodeInfo.get(2));
        newEpisode.setSeasonNumber(Integer.valueOf(episodeInfo.get(3)));
        newEpisode.setEpisodeNumber(Integer.valueOf(episodeInfo.get(4)));
        newEpisode.setDescription(episodeInfo.get(5));
        newEpisode.setReleaseDate(episodeInfo.get(6));

        newEpisode.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving episode / Episode: " + episodeInfo.get(1) + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the episode: " + episodeInfo.get(1));
            }
        });
    }


    // TODO: IS THIS NEEDED 4 LATER?
//    private void getTitleObject() throws ParseException {
//        titleTmdbID = (Integer) getIntent().getSerializableExtra(Title.class.getSimpleName());
//        Log.i(TAG, "Title TMDB ID: " + titleTmdbID);
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
//        query.whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID);
//        ParseObject parseObject = query.getFirst();
//        if (parseObject != null) {
//            setView(parseObject);
//        }
//    }
//
//    private void setView(ParseObject parseObject) {
//
//
//        tvName.setText(parseObject.getString(Title.KEY_NAME));
//        tvDescription.setText(parseObject.getString(Title.KEY_DESCRIPTION));
//        tvStarring.setText("Actor, Actor, ..."); //todo
//        tvReleaseDate.setText(parseObject.getString(Title.KEY_RELEASE_DATE));
//        tvAvailableOn.setText("Provide, Provider, ..."); //todo
//        tvSeasons.setText("2"); //todo
//        tvEpisodes.setText("24"); //todo
//
//        Glide.with(context)
//                .load(parseObject.getString(Title.KEY_COVER_PATH))
//                //.placeholder(placeholder)
//                //.error(placeholder)
//                .centerCrop() // scale image to fill the entire ImageView
//                //.transform(new RoundedCornersTransformation(radius, margin))
//                .into(ivCover);
//    }
}