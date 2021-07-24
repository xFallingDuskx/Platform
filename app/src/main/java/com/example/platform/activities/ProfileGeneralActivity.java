package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.platform.R;
import com.example.platform.models.User;
import com.parse.ParseUser;

public class ProfileGeneralActivity extends AppCompatActivity {

    public static final String TAG = "ProfileGeneralActivity";
    ImageView ivBack;
    TextView tvFullname;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_general);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ProfileGeneral);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivBack = findViewById(R.id.ivBack_ProfileGeneral);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();

        tvFullname = findViewById(R.id.tvProfileGeneral_Fullname_Text);
        tvFullname.setText("Fullname: " + currentUser.getString(User.KEY_FULLNAME));

        tvUsername = findViewById(R.id.tvProfileGeneral_Username_Text);
        tvUsername.setText("Username: " + currentUser.getUsername());

        tvEmail = findViewById(R.id.tvProfileGeneral_Email_Text);
        tvEmail.setText("Email: " + currentUser.getEmail());

//        tvPassword = findViewById(R.id.tvProfileGeneral_Password_Text);
//        tvPassword.setText(currentUser.getString(User.KEY_PASSWORD));
//        Log.i(TAG, "The password is: " + currentUser.getString(User.KEY_PASSWORD));
    }
}