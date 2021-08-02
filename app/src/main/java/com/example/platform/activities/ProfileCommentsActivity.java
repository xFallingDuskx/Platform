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
import com.example.platform.adapters.CommentsAdapter;
import com.example.platform.adapters.ProfileCommentsAdapter;
import com.example.platform.models.Comment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileCommentsActivity extends AppCompatActivity {

    public static final String TAG = "ProfileCommentsActivity";
    Context context;
    ImageView ivBack;
    TextView tvNotAvailable;

    RecyclerView rvComments;
    List<Comment> allComments;
    ProfileCommentsAdapter commentsAdapter;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_comments);
        context = getApplicationContext();

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout_ProfileComments);
        if(!shimmerFrameLayout.isShimmerVisible()) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }
        if(!shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.startShimmer();
        }

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ProfileComments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_ProfileComments);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNotAvailable = findViewById(R.id.tvNotAvailable_ProfileComments);

        // Set up RecyclerView
        rvComments = findViewById(R.id.rvComments_ProfileComments);
        allComments = new ArrayList<>();
        commentsAdapter = new ProfileCommentsAdapter(ProfileCommentsActivity.this, allComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(commentsAdapter);

        // Add comments to RV to be displayed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayComments();
            }
        }, 3000);
    }

    public void displayComments() {
        // Specify that we want Comments from the server
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        String currentUser = ParseUser.getCurrentUser().getUsername();
        query.whereEqualTo(Comment.KEY_USER, currentUser);
        // Sort comments first by likes then by their creation date
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
                    Log.d(TAG, "The comment user is: " + comment.getUser() + " for the comment: " + comment.getText() + " belong to the title: " + comment.getTmdbId() + "with a cover path of: " + comment.getCoverPath());
                    Log.i(TAG, "Post: " + comment.getText() + "/ Username: " + comment.getUser());
                }
                allComments.addAll(comments);
                commentsAdapter.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if(allComments.isEmpty()) { // If there are no comments to display
                    tvNotAvailable.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}