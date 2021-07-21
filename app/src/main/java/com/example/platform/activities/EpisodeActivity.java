package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.platform.R;

public class EpisodeActivity extends AppCompatActivity {

    public static final String TAG = "EpisodeActivity";
    Context context;

    ImageView ivCover;
    TextView tvName;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        context = getApplicationContext();

        ivCover = findViewById(R.id.ivCover_Episode_Details);
        tvName = findViewById(R.id.tvName_Episode_Details);
        tvDescription = findViewById(R.id.tvDescription_Episode_Details);
    }
}