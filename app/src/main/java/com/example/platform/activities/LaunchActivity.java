package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.platform.R;
import com.parse.ParseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LaunchActivity";
    private Button btnSignup;
    private Button btnLogin;
    private RelativeLayout rlAllContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // If fadeIn animation should take place - only coming from launch screen
        rlAllContent = findViewById(R.id.rlAllContent);
        boolean fade = getIntent().getBooleanExtra("fadeIn", false);
        if (fade) {
            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            rlAllContent.startAnimation(fadeIn);
        }

        // If user has just logged out
        boolean loggedOut = getIntent().getBooleanExtra("loggedOut", false);
        if (loggedOut) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LaunchActivity.this);
            sweetAlertDialog.setTitleText(getString(R.string.successful_logout));
            sweetAlertDialog.show();
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