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
import com.example.platform.R;
import com.example.platform.adapters.CommentsAdapter;
import com.example.platform.models.Comment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

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
    ProgressBar progressBar;

    EditText etCommentInput;
    ImageView ivPostComment;
    RecyclerView rvComments;
    List<Comment> allComments;
    CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_details);
        context = getApplicationContext();
        intent = getIntent();

        ivCover = findViewById(R.id.ivCover_Episode_Details);
        tvName = findViewById(R.id.tvName_Episode_Details);
        tvDescription = findViewById(R.id.tvDescription_Episode_Details);
        tvReleaseDate = findViewById(R.id.tvReleaseDate_Episode_Details);
        progressBar = findViewById(R.id.pbDetails_Episode);
        etCommentInput = findViewById(R.id.etCommentInput_Episode);
        ivPostComment = findViewById(R.id.ivPostComment_Episode);

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

        // Handle comment posting by user
        handleComment();
    }

    private void getEpisodeInformation() {
        showProgressBar();
        // First get information that was sent from previous activity
        Log.i(TAG, "Getting episode information...");
        episodeCover = (String) intent.getStringExtra("cover");
        episodeName = (String) intent.getStringExtra("name");
        episodeReleaseDate = (String) intent.getStringExtra("releaseDate");
        episodeDescription = (String) intent.getStringExtra("description");
        episodeTmdbId = (Integer) intent.getIntExtra("id", 0);
        Log.i(TAG, "Opening the Episode " + episodeName + " and TMDB ID: " + episodeTmdbId + " in Episode Details");

        hideProgressBar();
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
                    Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
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