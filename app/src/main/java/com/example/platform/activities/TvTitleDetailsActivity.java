package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Movie;
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

import org.parceler.Parcels;

public class TvTitleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TvTitleDetailsActivity";
    Integer titleTmdbID;
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
        tvEpisodes = findViewById(R.id.tvEpisodes_TV_Details);

        // Get Title
        try {
            getTitleObject();
        } catch (ParseException e) {
            Log.d(TAG, "Issue getting title / Message: " + e.getMessage());
        }

    }

    private void getTitleObject() throws ParseException {
        titleTmdbID = getIntent().getIntExtra(Title.class.getSimpleName(), 0);
        Log.i(TAG, "Title TMDB ID: " + titleTmdbID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
        query.whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID);
        ParseObject parseObject = query.getFirst();
        if (parseObject != null) {
            setView(parseObject);
        }
    }

    private void setView(ParseObject parseObject) {


        tvName.setText(parseObject.getString(Title.KEY_NAME));
        tvDescription.setText(parseObject.getString(Title.KEY_DESCRIPTION));
        tvStarring.setText("Actor, Actor, ..."); //todo
        tvReleaseDate.setText(parseObject.getString(Title.KEY_RELEASE_DATE));
        tvAvailableOn.setText("Provide, Provider, ..."); //todo
        tvSeasons.setText("2"); //todo
        tvEpisodes.setText("24"); //todo

        Glide.with(context)
                .load(parseObject.getString(Title.KEY_COVER_PATH))
                //.placeholder(placeholder)
                //.error(placeholder)
                .centerCrop() // scale image to fill the entire ImageView
                //.transform(new RoundedCornersTransformation(radius, margin))
                .into(ivCover);
    }
}