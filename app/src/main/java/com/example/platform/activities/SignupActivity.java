package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.platform.R;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText etEmail;
    private EditText etFullName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirmation;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etFullName = findViewById(R.id.etFullname);
        etUsername = findViewById(R.id.etUsername_Signup);
        etPassword = findViewById(R.id.etPassword_Signup);
        etPasswordConfirmation = findViewById(R.id.etPassword_Confirmation);
        btnSignup = findViewById(R.id.btnSignup_Signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String email = etEmail.getText().toString();
                String fullName = etFullName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String passwordConfirmation = etPasswordConfirmation.getText().toString();

                // Confirm password
                if (! password.equals(passwordConfirmation)) {
                    Log.i(TAG, "Password entered in by user are not the same");
                    Toast.makeText(SignupActivity.this, "Passwords are not the same", Toast.LENGTH_LONG).show();
                    return;
                }
                signupUser(email, fullName, username, password);
            }
        });
    }

    private void signupUser(String email, String fullName, String username, String password) {
        Log.i(TAG, "Attempting to signup user " + username);
        ParseUser user = new ParseUser();
        user.setEmail(email);
        user.put("fullName", fullName);
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with signup", e);
                    return;
                }
                goMainActivity();
                Toast.makeText(SignupActivity.this, "Successfully signed up!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goMainActivity() {
        Log.i(TAG, "Removing past activities from the stack to prevent user from going back");
        Intent intent = new Intent(SignupActivity.this, BaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}