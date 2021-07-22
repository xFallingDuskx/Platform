package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.CommentsAdapter;
import com.example.platform.adapters.EpisodesAdapter;
import com.example.platform.adapters.SimilarTitlesAdapter;
import com.example.platform.models.Comment;
import com.example.platform.models.Episode;
import com.example.platform.models.Title;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

    EditText etCommentInput;
    ImageView ivPostComment;
    RecyclerView rvComments;
    List<Comment> allComments;
    CommentsAdapter commentsAdapter;

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
        progressBar = findViewById(R.id.pbDetails_TV);
        etCommentInput = findViewById(R.id.etCommentInput_TV);
        ivPostComment = findViewById(R.id.ivPostComment_TV);

        // Get Title information
        getTitleInformation();

        // Set up the Recycler View for episodes display
        setEpisodeDisplay();

        // Set up the comment section for the title
        try {
            displayComments();
        } catch (ParseException e) {
            Log.d(TAG, "Issue fetching and displaying comments");
            e.printStackTrace();
        }

        // Handle comment posting by user
        handleComment();
    }

    private void getTitleInformation() {
        showProgressBar();
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting title information...");
        Intent intent = getIntent();
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
        title.setLikes(2);
        title.setShares(0);

        title.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + title.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + title.getName());
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

    // Set up view for displaying comments
    public void displayComments() throws ParseException {
        rvComments = findViewById(R.id.rvComments_TV);
        allComments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(context, allComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(commentsAdapter);

        // Specify that we want Comments from the server
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Only want Comments that match the current title
        query.whereEqualTo(Comment.KEY_TMDB_ID, titleTmdbID);
        // Sort comments first by likes then by their creation date
        query.addDescendingOrder(Comment.KEY_LIKES); // Most likes closer to the top
        query.addDescendingOrder("createdAt"); // Newest comments to the top
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting comments", e);
                    return;
                }
                // For debugging
                Log.d(TAG, "Number of comment returned: " + comments.size());
                for (Comment comment : comments) {
                    Log.d(TAG, "The comment is " + comment.toString());
                    Log.d(TAG, "The comment user is: " + comment.getUser() + " for the comment: " + comment.getText() + " belong to the title: " + comment.getTmdbId());
                    Log.i(TAG, "Post: " + comment.getText() + "/ Username: " + comment.getUser());
                }
                allComments.addAll(comments);
                commentsAdapter.notifyDataSetChanged();
            }
        });
    }

    // Handles posting comment after user has pressed the submit button (image)
    public void handleComment() {
        ivPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = etCommentInput.getText().toString();
                if (commentText.isEmpty()) { // Prevent user from making a comment without text
                    Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUser = ParseUser.getCurrentUser().getUsername();
                saveComment(commentText, currentUser, titleTmdbID);
            }
        });
    }

    // Save and post the comment
    public void saveComment(String commentText, String currentUser, Integer tmdbId) {
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setUser(currentUser);
        comment.setTmdbId(tmdbId);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    Toast.makeText(context, "Unable to save", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Comment was saved successfully");
                    // Rest description and post image after post has been made by user
                    etCommentInput.setText("");
                    // modify data source of tweets
                    allComments.add(0, comment);
                    // update adapter
                    commentsAdapter.notifyItemInserted(0);
                    // want to scroll to the top of recyclerview after each new tweet
                    rvComments.smoothScrollToPosition(0);
                }
            }
        });
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}