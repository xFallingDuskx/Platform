package com.example.platform.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.uuid.PNSetUUIDMetadataResult;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Headers;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    public String EMAIL_VERIFY_URL_BASE = "https://emailverification.whoisxmlapi.com/api/v1?apiKey=at_q49YobOASp4bKDhec8CdDDqfApDef&emailAddress=%s";
    private EditText etEmail;
    private EditText etFullName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirmation;
    private Button btnSignup;
    SweetAlertDialog sweetAlertDialog;
    String email;
    String fullName;
    String username;
    String password;

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
                sweetAlertDialog = new SweetAlertDialog(SignupActivity.this);
                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                sweetAlertDialog.setTitleText("Signing Up...");
                sweetAlertDialog.setContentText(getString(R.string.signing_up_text));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        email = etEmail.getText().toString();
                        fullName = etFullName.getText().toString();
                        username = etUsername.getText().toString();
                        password = etPassword.getText().toString();
                        String passwordConfirmation = etPasswordConfirmation.getText().toString();

                        // Confirm password
                        if (! password.equals(passwordConfirmation)) {
                            Log.i(TAG, "Password entered in by user are not the same");
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setTitleText("Error");
                            sweetAlertDialog.setContentText(getString(R.string.unmatched_passwords));
                            return;
                        }

                        // Verify the email address
                        verifyEmail(email);
                    }
                }, 3000);
            }
        });
    }

    private void verifyEmail(String email) {
        String EMAIL_VERIFY_URL = String.format(EMAIL_VERIFY_URL_BASE, email);
        Log.i(TAG, "Email Verify URL: " + EMAIL_VERIFY_URL);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(EMAIL_VERIFY_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to display titles");
                JSONObject jsonObject = json.jsonObject;
                try {
                    String formatCheck = jsonObject.getString("formatCheck"); // Checks the syntax of the email address - Expect true
                    String smtpCheck = jsonObject.getString("smtpCheck"); // Check if the email address exist and can receive emails - Expect true
                    String dnsCheck = jsonObject.getString("dnsCheck"); // Checks that the domain of the email address is valid - Expect true
                    String disposableCheck = jsonObject.getString("disposableCheck"); // Checks if the email address is disposable (abuseable) - Expect false

                    // If the email is not valid for any of the above 4 reasons
                    if (formatCheck.equals("false") || smtpCheck.equals("false") || dnsCheck.equals("false") || disposableCheck.equals("true")) {
                        Log.i(TAG, "Email address entered in by user is not valid");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitleText("Error");
                        sweetAlertDialog.setContentText(getString(R.string.invalid_email));
                    } else { // If the email is valid
                        signupUser(email, fullName, username, password);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to verify email address / Response: " + response + " / Error: " + throwable);
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
                saveUserToPubnub(username, email);
            }
        });
    }

    public void saveUserToPubnub(String username, String email) {
        // First establish a connection to PubNub
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(ParseUser.getCurrentUser().getObjectId()); // Set the PubNub unique user ID as the User's Object ID in the Parse server
        PubNub pubnub = new PubNub(pnConfiguration);

        // Create user in PubNub with their given information
        pubnub.setUUIDMetadata()
                .uuid(ParseUser.getCurrentUser().getObjectId())
                .name(username)
                .email(email)
                .async(new PNCallback<PNSetUUIDMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetUUIDMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Issue create new Pubnub user object /Error: " + status.toString());
                            Toast.makeText(SignupActivity.this, getString(R.string.unsuccessful_signup), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i(TAG, "Success creating new Pubnub user object");
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog.setTitleText("Success");
                            sweetAlertDialog.setContentText(getString(R.string.successful_signup));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    goMainActivity();
                                }
                            }, 1000);
                        }
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