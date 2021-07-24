package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.models.User;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    TextView tvUsername;
    TextView tvCreatedAt;
    RelativeLayout rlGeneral;
    RelativeLayout rlSecurity;
    RelativeLayout rlFollowing;
    RelativeLayout rlComments;
    RelativeLayout rlLikes;
    Button btnLogout;
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ParseUser currentUser = ParseUser.getCurrentUser();
        tvUsername = findViewById(R.id.tvUsername_Profile);
        tvUsername.setText(currentUser.getUsername());
        tvCreatedAt = findViewById(R.id.tvCreatedAt_Profile);
        tvCreatedAt.setText("Platform member since " + User.getTime(currentUser.getCreatedAt()));

        rlGeneral = findViewById(R.id.rlProfileOption_General);
        rlGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileGeneralActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rlSecurity = findViewById(R.id.rlProfileOption_Security);
        rlSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileSecurityActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rlFollowing = findViewById(R.id.rlProfileOption_Following);
        rlFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileFollowingActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rlComments = findViewById(R.id.rlProfileOption_Comments);
        rlComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileCommentsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rlLikes = findViewById(R.id.rlProfileOption_Likes);
        rlLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileLikesActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnLogout = findViewById(R.id.btnLogout_Profile);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser(); // Set user as null
                Intent intent = new Intent(ProfileActivity.this, LaunchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Takes user back to activities to Launch screen
                startActivity(intent);
                Toast.makeText(ProfileActivity.this, "You've been logged out", Toast.LENGTH_SHORT).show();
            }
        });

        ivClose = findViewById(R.id.ivClose_Profile);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}