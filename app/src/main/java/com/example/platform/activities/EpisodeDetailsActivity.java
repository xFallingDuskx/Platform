package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.adapters.CommentsAdapter;
import com.example.platform.adapters.KeywordsAdapter;
import com.example.platform.models.Comment;
import com.example.platform.models.Keyword;
import com.example.platform.models.Title;
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

public class EpisodeDetailsActivity extends AppCompatActivity {

    public static final String TAG = "EpisodeDetailsActivity";
    Context context;
    Intent intent;

    String episodeCover;
    String episodeName;
    String episodeReleaseDate;
    String episodeDescription;
    Integer episodeTmdbId;

    ImageView ivCover;
    TextView tvName;
    TextView tvDescription;
    TextView tvReleaseDate;
    ImageView ivFollowingStatus;
    TextView tvFollowingStatus;
    ImageView ivShare;

    EditText etCommentInput;
    ImageView ivPostComment;
    RecyclerView rvComments;
    List<Comment> allComments;
    CommentsAdapter commentsAdapter;

    ParseUser currentUser = ParseUser.getCurrentUser();
    JSONArray userFollowingTitles;
    int jsonPosition;
    boolean currentlyFollowing;

    ShimmerFrameLayout shimmerFrameLayout;
    ScrollView svEntireScreen;

    HashMap<String, Integer> episodeKeywordsMap;
    ParseObject episodeParseObject;
    RecyclerView  rvCommentKeywords;
    List<Keyword> allKeywords;
    KeywordsAdapter keywordsAdapter;
    TextView tvNoComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_details);
        context = getApplicationContext();
        intent = getIntent();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayoutEpisode);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }
        svEntireScreen = findViewById(R.id.svEpisodeDetails);
        svEntireScreen.setVisibility(View.INVISIBLE);

        ivCover = findViewById(R.id.ivCover_Episode_Details);
        tvName = findViewById(R.id.tvName_Episode_Details);
        tvDescription = findViewById(R.id.tvDescription_Episode_Details);
        tvReleaseDate = findViewById(R.id.tvReleaseDate_Episode_Details);
        ivFollowingStatus = findViewById(R.id.ivFollowingStatus_Episode_Details);
        tvFollowingStatus = findViewById(R.id.tvFollowingStatusText_Episode_Details);
        ivShare = findViewById(R.id.ivShare_Episode_Details);
        etCommentInput = findViewById(R.id.etCommentInput_Episode);
        ivPostComment = findViewById(R.id.ivPostComment_Episode);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get Episode information
                getEpisodeInformation();

                // Set Episode information within the view
                setEpisodeInformation();

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

    }

    private void getEpisodeInformation() {
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting episode information...");
        episodeCover = (String) intent.getStringExtra("cover");
        episodeName = (String) intent.getStringExtra("name");
        episodeReleaseDate = (String) intent.getStringExtra("releaseDate");
        episodeDescription = (String) intent.getStringExtra("description");
        episodeTmdbId = (Integer) intent.getIntExtra("id", 0);
        Log.i(TAG, "Opening the Episode " + episodeName + " and TMDB ID: " + episodeTmdbId + " in Episode Details");

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        svEntireScreen.setVisibility(View.VISIBLE);
    }

    public void setEpisodeInformation() {
        tvName.setText(episodeName);
        tvDescription.setText(episodeDescription);
        tvReleaseDate.setText(episodeReleaseDate);

        Glide.with(context)
                .load(episodeCover)
                .centerCrop()
                .into(ivCover);
    }

    // Set up view for displaying comments
    public void displayComments() throws ParseException {
        rvComments = findViewById(R.id.rvComments_Episode);
        allComments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(context, allComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(commentsAdapter);

        // Specify that we want Comments from the server
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Only want Comments that match the current title
        query.whereEqualTo(Comment.KEY_TMDB_ID, episodeTmdbId);
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
                saveComment(commentText, currentUser, episodeTmdbId);
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
                }
            }
        });
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
                Integer objectTmdbId = jsonObject.getInt("tmdbId");
                Log.d(TAG, "The jsonobject returned is: " + jsonObject.toString() + " with a TMDB id of " + objectTmdbId);
                if (objectTmdbId.equals(episodeTmdbId)) { // if the current title is currently liked by the user
                    jsonPosition = i;
                    currentlyFollowing = true;
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
                    userFollowingTitles.remove(jsonPosition);
                    Log.i(TAG, "User desires to unfollow the title");
                    currentlyFollowing = false;
                } else {
                    Log.i(TAG, "User desires to follow the title");
                    // If the user is not following the title and wish to follow it
                    // Create a new JSONObject for that title and add it to the jsonArray
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("tmdbId", episodeTmdbId);
                        jsonObject.put("name", episodeName);
                        jsonObject.put("posterPath", episodeCover);
                        jsonObject.put("type", "Episode");
                        jsonObject.put("description", episodeDescription);
                        jsonObject.put("releaseDate", episodeReleaseDate);
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
                        } else {
                            if (currentlyFollowing) { // If the user is now following the item
                                ivFollowingStatus.setImageResource(R.drawable.ic_following_true);
                                tvFollowingStatus.setText(R.string.following);
                                Toast.makeText(context, getString(R.string.now_following_episode) + episodeName, Toast.LENGTH_SHORT).show();
                            } else { // If the user is no longer following the title
                                ivFollowingStatus.setImageResource(R.drawable.ic_following_false);
                                tvFollowingStatus.setText(R.string.not_following);
                                Toast.makeText(context, getString(R.string.now_unfollowing_episode) + episodeName, Toast.LENGTH_SHORT).show();
                            }
                            Log.i(TAG, "Success saving following action by user");
                        }
                    }
                });
            }
        });
    }

    // User clicks on share icon
    // Source: https://www.geeksforgeeks.org/how-to-share-image-of-your-app-with-another-app-in-android/
    public void handleShareAction() {
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = getString(R.string.episode_share_message) + System.lineSeparator() + System.lineSeparator() + episodeName + System.lineSeparator() + System.lineSeparator() + episodeDescription;

                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivCover.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap, shareText);
            }
        });
    }

    public void handleCommentKeywords() throws ParseException {
        allKeywords = new ArrayList<>();
        tvNoComments = findViewById(R.id.tvNoComments_Episode);
        rvCommentKeywords = findViewById(R.id.rvCommentKeywords_Episode);
        keywordsAdapter = new KeywordsAdapter(context, allKeywords);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false);
        rvCommentKeywords.setLayoutManager(gridLayoutManager);
        rvCommentKeywords.setAdapter(keywordsAdapter);


        episodeParseObject = ParseQuery.getQuery("Episode").include(Title.KEY_KEYWORDS).whereEqualTo(Title.KEY_TMDB_ID, episodeTmdbId).getFirst();
        JSONObject jsonObject = episodeParseObject.getJSONObject(Title.KEY_KEYWORDS);
        if (jsonObject == null) { // If the user has liked no titles
            Log.i(TAG, "No keywords currently exist for the episode");
            episodeKeywordsMap = new HashMap<>();
        } else {
            String json = jsonObject.toString();
            Log.i(TAG, "String format of the json Map Object: " + json);
            ObjectMapper mapper = new ObjectMapper();

            // Clear List<Keyword> and notify keywordAdapter - Needed just in case to avoid duplication as a user makes a new comment and this method runs
            allKeywords.clear();
            keywordsAdapter.notifyDataSetChanged();

            //Convert Map to JSON and update the RecyclerView
            try {
                episodeKeywordsMap = mapper.readValue(json, new TypeReference<HashMap<String, Integer>>() {});
                Log.i(TAG, "The current keywords for the title are: " + episodeKeywordsMap.toString());
                for (Map.Entry<String, Integer> entry : episodeKeywordsMap.entrySet()) {
                    if (entry.getValue() > 1) {
                        Log.i(TAG, "The keyword within the initial keyword hashmap: " + entry.getKey());
                    }
                }
                List<String> orderedKeywords = Comment.getWordsToDisplay(episodeKeywordsMap); // Get the chosen keywords to display to users

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
                Log.d(TAG, "Issue accessing keywords for the episode");
                e.printStackTrace();
            }
        }
    }

    public void updateTitleKeywords(String comment) throws ParseException {
        HashSet<String> commentKeywords = Comment.getKeywords(comment);

        for (String keyword : commentKeywords) {
            if (! episodeKeywordsMap.containsKey(keyword)) { // If the comment keyword is not within the title keywords
                episodeKeywordsMap.put(keyword, 1);
            } else {
                int currentValue = episodeKeywordsMap.get(keyword);
                Log.i(TAG, "The keyword " + keyword + " has a value of " + currentValue + " for the title w/ Object ID: " + episodeParseObject.getObjectId());
                episodeKeywordsMap.put(keyword, currentValue + 1);
            }
        }
        Log.i(TAG, "The title keywords are " + episodeKeywordsMap.toString());
        episodeParseObject.put(Title.KEY_KEYWORDS, episodeKeywordsMap);

        episodeParseObject.saveInBackground(new SaveCallback() {
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