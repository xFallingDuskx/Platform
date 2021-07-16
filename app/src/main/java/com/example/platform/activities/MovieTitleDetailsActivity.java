package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.platform.R;
import com.example.platform.models.Title;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MovieTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MovieTitleDetailsActivity";

    Integer titleTmdbID;
    String titleName;
    String titleCoverPath;
    String titleType;
    String titleDescription;
    String titleReleaseDate;
    Context context;

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
    TextView tvEpisodes;

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
        tvAvailableOn = findViewById(R.id.tvAvailable_On_Movie_Details);
        ivFollowingStatus = findViewById(R.id.ivFollowingStatus_Movie_Details);
        tvFollowingStatus = findViewById(R.id.tvFollowingStatusText_Movie_Details);
        ivShare = findViewById(R.id.ivShare_Movie_Details);
        tvShare = findViewById(R.id.tvShareText_Movie_Details);
        ivLikeStatus = findViewById(R.id.ivLikeStatus_Movie_Details);
        tvLikeStatus = findViewById(R.id.tvLikeStatusText_Movie_Details);
        ivComment = findViewById(R.id.ivComment_Movie_Details);
        tvComment = findViewById(R.id.tvCommentText_Movie_Details);
        tvSeasons = findViewById(R.id.tvSeasons_Movie_Details);
        tvEpisodes = findViewById(R.id.tvEpisodes_Movie_Details);

        // Get Title information
        getTitleInformation();

        // Set Title information
        setTitleInformation();

    }

    private void getTitleInformation() {
        Log.i(TAG, "Getting title information...");
        titleName = (String) getIntent().getStringExtra(Title.KEY_NAME);
        titleTmdbID = (Integer) getIntent().getIntExtra( Title.KEY_TMDB_ID, 0);
        titleCoverPath = (String) getIntent().getStringExtra(Title.KEY_COVER_PATH);
        titleType = (String) getIntent().getStringExtra(Title.KEY_TYPE);
        titleDescription = (String) getIntent().getStringExtra(Title.KEY_DESCRIPTION);
        titleReleaseDate = (String) getIntent().getStringExtra(Title.KEY_RELEASE_DATE);
        Log.i(TAG, "Opening the Title " + titleName + " with type: " + titleType + " in Movie Details");
    }

    private void setTitleInformation() {
        tvName.setText(titleName);
        tvDescription.setText(titleDescription);
        tvStarring.setText("Actor, Actor, ..."); //todo
        tvReleaseDate.setText(titleReleaseDate);
        tvAvailableOn.setText("Provide, Provider, ..."); //todo
        tvSeasons.setText("2"); //todo
        tvEpisodes.setText("24"); //todo

        Glide.with(context)
                .load(titleCoverPath)
                //.placeholder(placeholder)
                //.error(placeholder)
                .centerCrop() // scale image to fill the entire ImageView
                //.transform(new RoundedCornersTransformation(radius, margin))
                .into(ivCover);
    }
}