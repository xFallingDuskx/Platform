package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.platform.R;

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

        // Get Episode information
        getEpisodeInformation();

        // Set Episode information within the view
        setEpisodeInformation();
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

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}