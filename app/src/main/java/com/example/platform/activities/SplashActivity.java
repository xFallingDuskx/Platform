package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.platform.R;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    TextView tvLogoTitle;
    TextView tvLogoQuote;
    FrameLayout flWhiteBackground;
    FrameLayout flBlackBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tvLogoTitle = findViewById(R.id.tvLogoTitle_Splash);
        tvLogoQuote = findViewById(R.id.tvLogoQuote_Splash);
        flWhiteBackground = findViewById(R.id.flWhiteBackground);
        flBlackBackground = findViewById(R.id.flBlackBackground);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Checking if user is already logged into the app
                if (ParseUser.getCurrentUser() != null) {
                    goBaseActivity();
                } else {
                    goLaunchActivity();
                }
            }
        }, 5000);
    }

    private void goBaseActivity() {
        Log.i(TAG, "Removing past activities from the stack to prevent user from going back");
        Intent intent = new Intent(SplashActivity.this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startSlidingAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 2000);
    }

    private void goLaunchActivity() {
        Log.i(TAG, "Removing past activities from the stack to prevent user from going back");
        Intent intent = new Intent(SplashActivity.this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fadeIn", true);

        startSlidingAnimation();
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        flWhiteBackground.setVisibility(View.VISIBLE);
        flWhiteBackground.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 2000);
    }

    private void startSlidingAnimation() {
        Animation slideRight = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_right_from_center);
        tvLogoTitle.startAnimation(slideRight);

        Animation slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left_from_center);
        tvLogoQuote.startAnimation(slideLeft);
    }
}