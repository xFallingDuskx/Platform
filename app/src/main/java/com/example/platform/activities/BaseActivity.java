package com.example.platform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.platform.R;
import com.example.platform.fragments.CatalogFragment;
import com.example.platform.fragments.ChatsFragment;
import com.example.platform.fragments.CommunitiesFragment;
import com.example.platform.fragments.HomeFragment;
import com.example.platform.fragments.InboxFragment;
import com.example.platform.models.Conversation;
import com.example.platform.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_communities:e:
                        fragment = new CommunitiesFragment();
                        Log.i(TAG, "Heading to Communities Fragment");
                        break;
                    case R.id.action_catalog:
                        fragment = new CatalogFragment();
                        Log.i(TAG, "Heading to Catalog Fragment");
                        break;
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        Log.i(TAG, "Heading to Home Fragment");
                        break;
                    case R.id.action_chats:
                        Log.i(TAG, "Heading to Chats Fragment");
                        fragment = new ChatsFragment();
                        break;
                    case R.id.action_inbox:
                        Log.i(TAG, "Heading to Inbox Fragment");
                        fragment = new InboxFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                // Complete the transaction to the fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection - only for Home
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}