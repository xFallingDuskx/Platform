package com.example.platform.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.EndlessRecyclerViewScrollListener;
import com.example.platform.R;
import com.example.platform.adapters.CommentsAdapter;
import com.example.platform.adapters.EpisodesAdapter;
import com.example.platform.adapters.KeywordsAdapter;
import com.example.platform.adapters.SimilarTitlesAdapter;
import com.example.platform.models.Comment;
import com.example.platform.models.Episode;
import com.example.platform.models.Keyword;
import com.example.platform.models.Title;
import com.example.platform.models.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class TvTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TvTitleDetailsActivity";
    public String TV_SHOW_DETAILS_URL_BASE = "https://api.themoviedb.org/3/tv/%d?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&append_to_response=similar,credits";
    public String SEASON_DETAILS_URL_BASE = "https://api.themoviedb.org/3/tv/%d/season/%d?api_key=e2b0127db9175584999a612837ae77b1&language=en-US";
    int seasonNumber = 0;
    Context context;

    Integer titleTmdbID;
    String titleName;
    String titleCoverPath;
    String titleType;
    String titleDescription;
    String titleReleaseDate;
    Boolean titleLiked;
    HashMap<String, String> userLikedTitles;
    Integer numberOfSeasons;
    Integer numberOfEpisodes;
    String genres;
    String networks;
    String actors;
    Boolean scrollToComments;

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

    RecyclerView rvSimilarTitlesDisplay;
    List<Title> similarTitles;
    SimilarTitlesAdapter titlesAdapter;

    EditText etCommentInput;
    ImageView ivPostComment;
    RecyclerView rvComments;
    List<Comment> allComments;
    CommentsAdapter commentsAdapter;

    ParseUser currentUser = ParseUser.getCurrentUser();
    JSONArray userFollowingTitles;
    int jsonPosition;
    JSONObject currentJsonObject;
    boolean currentlyFollowing;

    ShimmerFrameLayout shimmerFrameLayout;
    ScrollView svEntireScreen;

    HashMap<String, Integer> titleKeywordsMap;
    ParseObject titleParseObject;
    RecyclerView  rvCommentKeywords;
    List<Keyword> allKeywords;
    KeywordsAdapter keywordsAdapter;
    TextView tvNoComments;

    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_title_details);
        context = getApplicationContext();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }
        svEntireScreen = findViewById(R.id.svTVDetails);
        svEntireScreen.setVisibility(View.INVISIBLE);


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
        etCommentInput = findViewById(R.id.etCommentInput_TV);
        ivPostComment = findViewById(R.id.ivPostComment_TV);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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

                // Handle and display keywords for comment section
                try {
                    handleCommentKeywords();
                } catch (ParseException e) {
                    Log.d(TAG, "Issue handling the comment keywords /Error: " + e.getMessage());
                    e.printStackTrace();
                }

                // Handle comment posting by user
                handleComment();

                // Handle the user clicking on the comment icon
                handleCommentScrollAction();

                // Handle the current liked status of the title for the user
                handleLikeStatus();

                // Handle the user changing their liked status
                handleLikeAction();

                // Handle the user can share content
                handleShareAction();

                // Handle following status (whether a user is currently following a title or not)
                try {
                    handleFollowingStatus();
                } catch (JSONException e) {
                    Log.d(TAG, "Issue handling the following status for the user /Error: " + e.getMessage());
                    e.printStackTrace();
                }

                // Handle following action (when a user clicks on the following icon)
                try {
                    handleFollowingAction();
                } catch (JSONException e) {
                    Log.d(TAG, "Issue handling user following action /Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 5000);

        // If user clicked on comment icon on home feed
        // Prevent scroll behavior from happening too early
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollToComments) {
                    scrollBehavior();
                }
            }
        }, 8500);
    }

    private void getTitleInformation() {
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting title information...");
        Intent intent = getIntent();
        titleName = (String) intent.getStringExtra("name");
        titleTmdbID = (Integer) intent.getIntExtra("id", 0);
        titleCoverPath = (String) intent.getStringExtra("posterPath");
        titleType = (String) intent.getStringExtra("type");
        titleDescription = (String) intent.getStringExtra("description");
        titleReleaseDate = (String) intent.getStringExtra("releaseDate");
        titleLiked = (Boolean) intent.getBooleanExtra("titleLiked", false);
        scrollToComments = (Boolean) intent.getBooleanExtra("scrollToComments", false);

        Log.i(TAG, "Opening the Title " + titleName + " with type: " + titleType + " and TMDB ID: " + titleTmdbID + " in TV Details");

        // Then get additional information from TMDB API
        String TV_SHOW_DETAILS_URL = String.format(TV_SHOW_DETAILS_URL_BASE, titleTmdbID);
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
                    loadEpisodesInDisplay();

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

        for (int i = 0; i < actors.length(); i++) {
            // Only display top 3 actors (if there are 3)
            if (i == 3) {
                break;
            }

            JSONObject actorObject = actors.getJSONObject(i);
            String actor = actorObject.getString("name");
            formattedActors.append(actor);
            if (i < actors.length() - 1) {
                if (i <= 1) {
                    formattedActors.append(", ");
                }
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
                .centerCrop() // scale image to fill the entire ImageView
                .into(ivCover);

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        svEntireScreen.setVisibility(View.VISIBLE);
    }

    // Set up the RecyclerView to display the episodes for the title
    private void setEpisodeDisplay() {
        rvEpisodesDisplay = findViewById(R.id.rvEpisodesDisplay);
        allEpisodes = new ArrayList<>();
        adapter = new EpisodesAdapter(context, allEpisodes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvEpisodesDisplay.setLayoutManager(linearLayoutManager);
        rvEpisodesDisplay.setAdapter(adapter);

        // Endless Scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadEpisodesInDisplay();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvEpisodesDisplay.addOnScrollListener(scrollListener);
    }

    // Obtain episodes data by accessing each season of the title
    private void loadEpisodesInDisplay() {
        Log.i(TAG, "Title: " + titleName + "/Number of seasons: " + numberOfSeasons);
        seasonNumber++;
        String SEASON_DETAILS_URL = String.format(SEASON_DETAILS_URL_BASE, titleTmdbID, seasonNumber);
        Log.i(TAG, "Season Details URL: " + SEASON_DETAILS_URL);
        AsyncHttpClient seasonClient = new AsyncHttpClient();

        seasonClient.get(SEASON_DETAILS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to get season details");
                JSONObject seasonJsonObject = json.jsonObject;
                try {
                    JSONArray seasonEpisodes = seasonJsonObject.getJSONArray("episodes");
                    Log.i(TAG, "Episodes: " + seasonEpisodes.toString());
                    List<Episode> newEpisodes = Episode.fromJsonArray(titleCoverPath, seasonEpisodes);
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
                    Toast.makeText(context, getString(R.string.comment_is_empty), Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentUser = ParseUser.getCurrentUser().getUsername();
                saveComment(commentText, currentUser, titleTmdbID, titleCoverPath, titleType, titleName, titleDescription, titleReleaseDate);
            }
        });
    }

    // Handles taking the user to the comment section if they press the comment icon
    public void handleCommentScrollAction() {
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollBehavior();
            }
        });
    }

    // Separating methods is need if user clicks on Comment icon on home feed
    public void scrollBehavior() {
        svEntireScreen.post(new Runnable() {
            @Override
            public void run() {
                svEntireScreen.smoothScrollTo(0, rvComments.getTop());
            }
        });
    }

    // Save and post the comment
    public void saveComment(String commentText, String currentUser, Integer tmdbId, String titleCoverPath, String titleType, String titleName, String titleDescription, String titleReleaseDate) {
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setUser(currentUser);
        comment.setTmdbId(tmdbId);
        comment.setCoverPath(titleCoverPath);
        comment.setType(titleType);
        comment.setName(titleName);
        comment.setDescription(titleDescription);
        comment.setReleaseDate(titleReleaseDate);
        comment.saveInBackground(new SaveCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    Toast.makeText(context, getString(R.string.cannot_save_comment), Toast.LENGTH_SHORT).show();
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
                    // update keywords for the title
                    try {
                        updateTitleKeywords(commentText);
                    } catch (ParseException parseException) {
                        Log.d(TAG, "Issue updating the keywords for the title");
                        parseException.printStackTrace();
                    }

                    // Close the keyboard
                    // Source: https://gist.github.com/lopspower/6e20680305ddfcb11e1e
                    View view = findViewById(android.R.id.content);
                    InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    // Send notification
                    sendNotification(tmdbId, titleName, currentUser, commentText);
                }
            }
        });
    }

    // Handle sending notification for the new comment
    // Source: https://guides.codepath.com/android/Notifications
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification(Integer tmdbId, String titleName, String currentUser, String commentText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(String.valueOf(tmdbId));

        // If the channel does not yet exist for the title
        if (notificationChannel == null) {
            createNotificationChannel();
        }

        // If the user taps on the notification
        Intent intent = new Intent(this, TvTitleDetailsActivity.class);
        intent.putExtra("id", titleTmdbID);
        intent.putExtra("name", titleName);
        intent.putExtra("posterPath", titleCoverPath);
        intent.putExtra("type", "tv");
        intent.putExtra("description", titleDescription);
        intent.putExtra("releaseDate", titleReleaseDate);
        intent.putExtra("titleLiked", titleLiked);
        intent.putExtra("userLikedTitles", userLikedTitles);
        intent.putExtra("scrollToComments", scrollToComments);
        // Next, let's turn this into a PendingIntent using
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, flags);

        Notification notification = new NotificationCompat.Builder(
                this, String.valueOf(tmdbId)).setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle("New comment for the title " + titleName)
                .setContentText("Made by the user " + currentUser + " - Click to view")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // Hides the notification after its been selected
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, notification);
    }

    // Create a notification channel for this title if it does not exist
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = titleName;
            String description = "Notification channel for the title " + titleName;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(titleTmdbID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Handles the Following status for the title
    public void handleFollowingStatus() throws JSONException {
        // First check if the user is following the current title
        userFollowingTitles = currentUser.getJSONArray("following");
        currentlyFollowing = false;

        if (userFollowingTitles == null) { // if the user is not currently following any titles
            userFollowingTitles = new JSONArray();
            Log.i(TAG, "The user is not currently following any titles");
        } else { // the user is currently following at least one title
            for (int i = 0; i < userFollowingTitles.length(); i++) {
                JSONObject jsonObject = new JSONObject(userFollowingTitles.getString(i));
                Integer objectTmdbId = jsonObject.getInt(Title.KEY_TMDBID_LOWER);
                Log.d(TAG, "The jsonobject returned is: " + jsonObject.toString() + " with a TMDB id of " + objectTmdbId);
                if (objectTmdbId.equals(titleTmdbID)) { // if the current title is currently liked by the user
                    jsonPosition = i;
                    currentlyFollowing = true;
                    currentJsonObject = jsonObject;
                    break;
                }
            }
        }

        // If the user is following the title
        if (currentlyFollowing) {
            ivFollowingStatus.setImageResource(R.drawable.ic_following_true);
            tvFollowingStatus.setText("Following");
        }
    }

    // If the user selects the following status icon
    public void handleFollowingAction() throws JSONException {
        ivFollowingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // If the user is following the title and wishes to unfollow it
                if (currentlyFollowing) {
                    ivFollowingStatus.setImageResource(R.drawable.ic_following_false);
                    tvFollowingStatus.setText(R.string.not_following);
                    Toast.makeText(context, getString(R.string.now_unfollowing_title) + titleName, Toast.LENGTH_SHORT).show();

                    userFollowingTitles.remove(jsonPosition);
                    Log.i(TAG, "User desires to unfollow the title");
                    currentlyFollowing = false;
                } else {
                    ivFollowingStatus.setImageResource(R.drawable.ic_following_true);
                    tvFollowingStatus.setText(R.string.following);
                    Toast.makeText(context, getString(R.string.now_following_title) + titleName, Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "User desires to follow the title");
                    // If the user is not following the title and wish to follow it
                    // Create a new JSONObject for that title and add it to the jsonArray
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Title.KEY_TMDBID_LOWER, titleTmdbID);
                        jsonObject.put(Title.KEY_NAME, titleName);
                        jsonObject.put(Title.KEY_POSTERPATH, titleCoverPath);
                        jsonObject.put(Title.KEY_TYPE, titleType);
                        jsonObject.put(Title.KEY_DESCRIPTION, titleDescription);
                        jsonObject.put(Title.KEY_RELEASE_DATE, titleReleaseDate);
                    } catch (JSONException e) {
                        Log.d(TAG, "Issue creating new JSONObject for user following titles");
                        e.printStackTrace();
                    }
                    userFollowingTitles.put(jsonObject.toString());
                    currentlyFollowing = true;
                }

                // Change and update the user following titles within the parse server
                currentUser.put("following", userFollowingTitles);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Issue saving following action by user");

                            // In case saving the user new following status fails
                            if (currentlyFollowing) { // User wanted to follow the title, but it failed -- Must revert back to 'Not Following' status
                                ivFollowingStatus.setImageResource(R.drawable.ic_following_false);
                                tvFollowingStatus.setText(R.string.not_following);
                                userFollowingTitles.remove(userFollowingTitles.length() - 1);
                                currentlyFollowing = false;
                            } else {
                                ivFollowingStatus.setImageResource(R.drawable.ic_following_true);
                                tvFollowingStatus.setText(R.string.following);
                                userFollowingTitles.put(currentJsonObject.toString());
                                currentlyFollowing = true;
                            }
                            Toast.makeText(context, getString(R.string.follow_action_failed), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, "Success saving following action by user");
                        }
                    }
                });
            }
        });
    }

    public void handleLikeStatus() {
        if (titleLiked) {
            ivLikeStatus.setImageResource(R.drawable.ic_heart_filled);
            tvLikeStatus.setText("Liked");
        }

        // Gain access to titles liked by user which will be needed later if user changes their like status
        JSONObject jsonObject = currentUser.getJSONObject(User.KEY_LIKED_TITLES);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No titles currently liked by the user");
            userLikedTitles = new HashMap<>();
        } else {
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            //Convert Map to JSON
            try {
                userLikedTitles = mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
                });
            } catch (JsonProcessingException e) {
                Log.d(TAG, "Issue accessing tiles liked by user");
                e.printStackTrace();
            }
        }
    }

    public void handleLikeAction() {
        // If the Title is currently liked by the User and they desire to unlike it
        ivLikeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleLiked) {
                    ivLikeStatus.setImageResource(R.drawable.ic_heart_empty); // Change to empty heart
                    tvLikeStatus.setText("Not liked");
                    userLikedTitles.remove(String.valueOf(titleTmdbID)); // Remove title based on its TMDB ID #
                    currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
                    titleLiked = false; // Title is no longer liked by the user
                    Toast.makeText(context, getString(R.string.unliked_title) + titleName, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "User " + currentUser.getUsername() + " has disliked the title: " + titleName);
                } else {  // Title is currently not liked by the User and they desire to like it
                    ivLikeStatus.setImageResource(R.drawable.ic_heart_filled); // Change to filled-in heart
                    tvLikeStatus.setText("Liked");
                    userLikedTitles.put(String.valueOf(titleTmdbID), titleType); // Add title based on its TMDB ID #
                    currentUser.put(User.KEY_LIKED_TITLES, userLikedTitles); // Update the Parse Server with this change
                    titleLiked = true; // Title is now liked by the user
                    Toast.makeText(context, getString(R.string.liked_title) + titleName, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "User " + currentUser.getUsername() + " has liked the title: " + titleName);
                }

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "User: Issue saving like action by user " + e.getMessage());

                            // In case saving the user like fails
                            if (titleLiked) { // User wanted to like the title, but it failed -- Must revert back to unliked status
                                ivLikeStatus.setImageResource(R.drawable.ic_heart_empty);
                                tvLikeStatus.setText("Not liked");
                                userLikedTitles.remove(String.valueOf(titleTmdbID));
                                titleLiked = false;
                            } else {
                                ivLikeStatus.setImageResource(R.drawable.ic_heart_filled);
                                tvLikeStatus.setText("Liked");
                                userLikedTitles.put(String.valueOf(titleTmdbID), titleType);
                                titleLiked = true;
                            }
                            Toast.makeText(context, getString(R.string.like_action_failed), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i(TAG, "User: Success saving like action by user");
                        }
                    }
                });
                Log.i(TAG, "Title currently liked by the user after clicking are: " + currentUser.getMap(User.KEY_LIKED_TITLES));
            }
        });
    }

    // User clicks on share icon
    // Source: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
    public void handleShareAction() {
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = getString(R.string.title_share_message) + System.lineSeparator() + System.lineSeparator() +  titleName + System.lineSeparator() + System.lineSeparator() + titleDescription;

                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivCover.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap, shareText);
            }
        });
    }

    public void handleCommentKeywords() throws ParseException {
        allKeywords = new ArrayList<>();
        tvNoComments = findViewById(R.id.tvNoComments_TV);
        rvCommentKeywords = findViewById(R.id.rvCommentKeywords_TV);
        keywordsAdapter = new KeywordsAdapter(context, allKeywords);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false);
        rvCommentKeywords.setLayoutManager(gridLayoutManager);
        rvCommentKeywords.setAdapter(keywordsAdapter);


        titleParseObject = ParseQuery.getQuery("Title").include(Title.KEY_KEYWORDS).whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID).getFirst();
        JSONObject jsonObject = titleParseObject.getJSONObject(Title.KEY_KEYWORDS);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No keywords currently exist for the title");
            titleKeywordsMap = new HashMap<>();
        } else {
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            // Clear List<Keyword> and notify keywordAdapter - Needed just in case to avoid duplication as a user makes a new comment and this method runs
            allKeywords.clear();
            keywordsAdapter.notifyDataSetChanged();

            //Convert Map to JSON and update the RecyclerView
            try {
                titleKeywordsMap = mapper.readValue(json, new TypeReference<HashMap<String, Integer>>() {});
                Log.i(TAG, "The current keywords for the title are: " + titleKeywordsMap.toString());
                for (Map.Entry<String, Integer> entry : titleKeywordsMap.entrySet()) {
                    if (entry.getValue() > 1) {
                        Log.i(TAG, "The keyword within the initial keyword hashmap: " + entry.getKey());
                    }
                }
                List<String> orderedKeywords = Comment.getWordsToDisplay(titleKeywordsMap); // Get the chosen keywords to display to users

                if (! orderedKeywords.isEmpty()) { // hide message if comment keywords do exist
                    tvNoComments.setVisibility(View.INVISIBLE);
                }

                for (int i = 0; i < orderedKeywords.size(); i++) {
                    String keyword = orderedKeywords.get(i);
                    Log.i(TAG, "Keyword is: " + keyword);
                    Keyword keywordObject = new Keyword(keyword);
                    allKeywords.add(keywordObject);
                }
                keywordsAdapter.notifyDataSetChanged();
            } catch (JsonProcessingException e) {
                Log.d(TAG, "Issue accessing keywords for the title");
                e.printStackTrace();
            }
        }
    }

    public void updateTitleKeywords(String comment) throws ParseException {
        HashSet<String> commentKeywords = Comment.getKeywords(comment);

        for (String keyword : commentKeywords) {
            if (! titleKeywordsMap.containsKey(keyword)) { // If the comment keyword is not within the title keywords
                titleKeywordsMap.put(keyword, 1);
            } else {
                int currentValue = titleKeywordsMap.get(keyword);
                Log.i(TAG, "The keyword " + keyword + " has a value of " + currentValue + " for the title w/ Object ID: " + titleParseObject.getObjectId());
                titleKeywordsMap.put(keyword, currentValue + 1);
            }
        }
        Log.i(TAG, "The title keywords are " + titleKeywordsMap.toString());
        titleParseObject.put(Title.KEY_KEYWORDS, titleKeywordsMap);

        titleParseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Issue saving title keyword update /Error: " + e.getMessage());
                } else {
                    Log.i(TAG, "Success saving title keywords update");
                    // Update the comment keyword section
                    try {
                        handleCommentKeywords();
                    } catch (ParseException parseException) {
                        Log.d(TAG, "Issue handle comment keywords after successfully title keyword update");
                        parseException.printStackTrace();
                    }
                }
            }
        });
    }

    // To share image with text
    private void shareImageandText(Bitmap bitmap, String shareText) {
        Uri uri = getmageToShare(bitmap);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, "Sharing information for this title"));
    }

    // Retrieving the url to share
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Log.d(TAG, "Issue retrieving the url to share");
        }
        return uri;
    }
}