package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    TextView tvCreatedAt;
    TextView tvUsername;
    TextView tvFullname;
    TextView tvEmail;
    Button btnLogout;
    RecyclerView rvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
    }
}