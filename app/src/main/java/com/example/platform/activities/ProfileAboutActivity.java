package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.platform.R;

public class ProfileAboutActivity extends AppCompatActivity {

    public static final String TAG = "ProfileAboutActivity";
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_about);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ProfileAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_ProfileAbout);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}