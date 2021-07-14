package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.platform.R;
import com.parse.ParseUser;

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LaunchActivity";
    private Button btnSignup;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Checking if user is alreaady logged into the app
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        btnSignup = findViewById(R.id.btnSignup_Launch);
        btnLogin = findViewById(R.id.btnLogin_Launch);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "Removing past activities from the stack to prevent user from going back");
        Intent intent = new Intent(LaunchActivity.this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}