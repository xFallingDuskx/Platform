package com.example.platform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.platform.R;
import com.example.platform.fragments.CatalogFragment;
import com.example.platform.fragments.ChatsFragment;
import com.example.platform.fragments.CommunitiesFragment;
import com.example.platform.fragments.HomeFragment;
import com.example.platform.fragments.InboxFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    //TODO: private String toolbarTitle;

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
                        Log.i(TAG, "Heading to Inbix Fragment");
                        fragment = new InboxFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection - only for Home
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}