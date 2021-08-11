package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.platform.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    SweetAlertDialog sweetAlertDialog;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    RelativeLayout rlUsernameUnderline;
    RelativeLayout rlPasswordUnderline;
    ImageView ivUsernameIcon;
    ImageView ivPasswordIcon;
    ImageView ivPasswordVisibilityIcon;
    boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername_Login);
        etPassword = findViewById(R.id.etPassword_Login);
        btnLogin = findViewById(R.id.btnLogin_Login);
        rlUsernameUnderline = findViewById(R.id.rlUnderlineUsername_Login);
        rlPasswordUnderline = findViewById(R.id.rlUnderlinePassword_Login);
        ivUsernameIcon = findViewById(R.id.ivIconUsername_Login);
        ivPasswordIcon = findViewById(R.id.ivIconPassword_Login);
        ivPasswordVisibilityIcon = findViewById(R.id.ivIconPasswordVisibility_Login);
        visible = false;

        // onClick effects for when user is typing in the EditText views
        setOnClickEffects();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                sweetAlertDialog = new SweetAlertDialog(LoginActivity.this);
                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                sweetAlertDialog.setTitleText("Logging in...");
                sweetAlertDialog.setContentText(getString(R.string.logging_in_text));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();
                        loginUser(username, password);
                    }
                }, 4000);
            }
        });
    }

    // Change the color of the edit text underline and associate icon
    // Source: https://stackoverflow.com/questions/33797431/how-to-determine-if-someone-is-typing-on-edittext/33797614
    private void setOnClickEffects() {
        etUsername.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (! etUsername.getText().toString().isEmpty()) {
                    rlUsernameUnderline.setBackgroundColor(getResources().getColor(R.color.black_lighter, getTheme()));
                    ivUsernameIcon.setImageResource(R.drawable.ic_username_active);
                } else {
                    rlUsernameUnderline.setBackgroundColor(getResources().getColor(R.color.grey_lighter, getTheme()));
                    ivUsernameIcon.setImageResource(R.drawable.ic_username);
                    }
                }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (! etPassword.getText().toString().isEmpty()) {
                    rlPasswordUnderline.setBackgroundColor(getResources().getColor(R.color.black_lighter, getTheme()));
                    ivPasswordIcon.setImageResource(R.drawable.ic_password_active);
                    ivPasswordVisibilityIcon.setImageResource(R.drawable.ic_password_eye_active);
                } else {
                    rlPasswordUnderline.setBackgroundColor(getResources().getColor(R.color.grey_lighter, getTheme()));
                    ivPasswordIcon.setImageResource(R.drawable.ic_password);
                    ivPasswordVisibilityIcon.setImageResource(R.drawable.ic_password_eye);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.redhattext_thinner);
        ivPasswordVisibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visible) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setTypeface(typeface);
                    visible = true;
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setTypeface(typeface);
                    visible = false;
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("Error");
                    sweetAlertDialog.setContentText(getString(R.string.incorrect_login));
                    return;
                }

                SweetAlertDialog sweetAlertTemp = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertTemp.setTitleText("Success")
                        .setContentText(getString(R.string.successful_login))
                        .show();
                sweetAlertDialog.cancel();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goMainActivity();
                    }
                }, 1000);
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "Removing past activities from the stack to prevent user from going back");
        Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}