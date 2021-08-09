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
import android.widget.TextView;

import com.example.platform.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LaunchActivity";
    private Button btnSignup;
    private Button btnLogin;
    private RelativeLayout rlAllContent;
    private TextView tvLogoTitle;
    private TextView tvLogoQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        rlAllContent = findViewById(R.id.rlAllContent);
        tvLogoTitle = findViewById(R.id.tvLogoTitle_Launch);
        tvLogoQuote = findViewById(R.id.tvLogoQuote_Launch);
        btnSignup = findViewById(R.id.btnSignup_Launch);
        btnLogin = findViewById(R.id.btnLogin_Launch);

        // If animation should take place - only coming from launch screen
        boolean fade = getIntent().getBooleanExtra("fadeIn", false);
        if (fade) {
            Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//            rlAllContent.startAnimation(fadeIn);
            btnLogin.startAnimation(fadeIn);
            btnSignup.startAnimation(fadeIn);
            Animation leftToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right);
            tvLogoTitle.startAnimation(leftToRight);
            Animation rightToLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left);
            tvLogoQuote.startAnimation(rightToLeft);
        }

        // If user has just logged out
        boolean loggedOut = getIntent().getBooleanExtra("loggedOut", false);
        if (loggedOut) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LaunchActivity.this);
            sweetAlertDialog.setTitleText(getString(R.string.successful_logout));
            sweetAlertDialog.show();
        }

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
}