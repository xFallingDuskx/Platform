package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.platform.adapters.SimilarTitlesAdapter;
import com.example.platform.models.Comment;
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

public class MovieTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MovieTitleDetailsActivity";
    public String Movie_DETAILS_URL;
    Context context;

    Integer titleTmdbID;
    String titleName;
    String titleCoverPath;
    String titleType;
    String titleDescription;
    String titleReleaseDate;
    String runtime;
    String genres;
    String actors;

    ImageView ivCover;
    TextView tvName;
    TextView tvDescription;
    TextView tvStarring;
    TextView tvReleaseDate;
    TextView tvGenres;
    ImageView ivFollowingStatus;
    TextView tvFollowingStatus;
    ImageView ivShare;
    TextView tvShare;
    ImageView ivLikeStatus;
    TextView tvLikeStatus;
    ImageView ivComment;
    TextView tvComment;
    TextView tvRuntime;
    ProgressBar progressBar;

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
        setContentView(R.layout.activity_movie_title_details);
        context = getApplicationContext();

        ivCover = findViewById(R.id.ivCover_Movie_Details);
        tvName = findViewById(R.id.tvName_Movie_Details);
        tvDescription = findViewById(R.id.tvDescription_Movie_Details);
        tvStarring = findViewById(R.id.tvStarring_Movie_Details);
        tvReleaseDate = findViewById(R.id.tvRelease_Date_Movie_Details);
        tvGenres = findViewById(R.id.tvGenres_Movie_Details);
        ivFollowingStatus = findViewById(R.id.ivFollowingStatus_Movie_Details);
        tvFollowingStatus = findViewById(R.id.tvFollowingStatusText_Movie_Details);
        ivShare = findViewById(R.id.ivShare_Movie_Details);
        tvShare = findViewById(R.id.tvShareText_Movie_Details);
        ivLikeStatus = findViewById(R.id.ivLikeStatus_Movie_Details);
        tvLikeStatus = findViewById(R.id.tvLikeStatusText_Movie_Details);
        ivComment = findViewById(R.id.ivComment_Movie_Details);
        tvComment = findViewById(R.id.tvCommentText_Movie_Details);
        tvRuntime = findViewById(R.id.tvRuntime_Movie_Details);
        progressBar = findViewById(R.id.pbDetails_Movie);
        etCommentInput = findViewById(R.id.etCommentInput_Movie);
        ivPostComment = findViewById(R.id.ivPostComment_Movie);

        // Get Title information
        getTitleInformation();

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
        Log.i(TAG, "Opening the Title " + titleName + " with type: " + titleType + " in TV Details");

        // Then get additional information from TMDB API
        Movie_DETAILS_URL = "https://api.themoviedb.org/3/movie/" + titleTmdbID + "?api_key=e2b0127db9175584999a612837ae77b1&language=en-US&append_to_response=similar,credits";
        Log.i(TAG, "Movie Details URL: " + Movie_DETAILS_URL);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Movie_DETAILS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to getting additional title information");
                JSONObject jsonObject = json.jsonObject;

                try {
                    runtime = getRuntimeFormatted(jsonObject.getInt("runtime"));
                    genres = "- " + getGenresFormatted(jsonObject.getJSONArray("genres"));
                    similarTitles = Title.fromJsonArray(jsonObject.getJSONObject("similar").getJSONArray("results"));
                    actors = getActorsFormatted(jsonObject.getJSONObject("credits").getJSONArray("cast"));
                    updateParseServer(similarTitles);

                    // Now set Title information
                    setTitleInformation();

                    // Display similar Titles in RecyclerView
                    displaySimilarTitlesInDisplay();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                } catch (ParseException e) {
                    Log.i(TAG, "Issue saving similar Movie titles to parse server");
                    e.printStackTrace();
                }
                Log.i(TAG, "Successful receive information / Title: " + titleName + " / Runtime: " + runtime  + " / Genres: " + genres);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to get additional title information / Response: " + response + " / Error: " + throwable);
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
        title.setLikes(0);
        title.setShares(0);

        title.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + title.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + title.getName());
            }
        });
    }

    // Runtime given in total minutes
    // Convert to desired formated of X hr XX min
    private String getRuntimeFormatted(int runtime) {
        String formattedRuntime = "";
        String hourText = " hr ";
        String minuteText = " min ";

        int hours = runtime / 60;
        int minutes = runtime - (hours * 60);
        if (hours > 1) {
            hourText = " hrs ";
        }
        if (minutes > 0) {
            minuteText = " mins ";
        }

        formattedRuntime = formattedRuntime + hours + hourText + minutes + minuteText;
        return formattedRuntime;
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

    private void setTitleInformation() {
        tvName.setText(titleName);
        tvDescription.setText(titleDescription);
        tvStarring.setText(actors);
        tvGenres.setText(genres);
        tvReleaseDate.setText(titleReleaseDate);
        tvRuntime.setText(runtime);
        tvGenres.setText(genres);

        Glide.with(context)
                .load(titleCoverPath)
                //.placeholder(placeholder)
                //.error(placeholder)
                .centerCrop() // scale image to fill the entire ImageView
                //.transform(new RoundedCornersTransformation(radius, margin))
                .into(ivCover);
        hideProgressBar();
    }

    public void displaySimilarTitlesInDisplay() {
        rvSimilarTitlesDisplay = findViewById(R.id.rvSimilarTitlesDisplay_Movie);
        titlesAdapter = new SimilarTitlesAdapter(context, similarTitles);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false);
        rvSimilarTitlesDisplay.setLayoutManager(gridLayoutManager);
        rvSimilarTitlesDisplay.setAdapter(titlesAdapter);
    }

    // Set up view for displaying comments
    public void displayComments() throws ParseException {
        rvComments = findViewById(R.id.rvComments_Movie);
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