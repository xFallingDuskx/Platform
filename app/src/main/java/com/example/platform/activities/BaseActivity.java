package com.example.platform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.platform.R;
import com.example.platform.fragments.CatalogFragment;
import com.example.platform.fragments.ChatsFragment;
import com.example.platform.fragments.CommunitiesFragment;
import com.example.platform.fragments.HomeFragment;
import com.example.platform.fragments.InboxFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    public String TITLES_SEARCH_URL;
    BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    //TODO: private String toolbarTitle;
    private ImageView ivProfile;

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
                // Complete the transaction to the fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection - only for Home
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = findViewById(R.id.ivProfileIcon_Base);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);

        // Allow user to search Titles
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Change appearance of EditText for the Search View
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHint("Search TV Shows and Movies");
        searchEditText.setHintTextColor(getResources().getColor(R.color.grey_light));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search for Titles according to the query
                searchTitles(query);
                // Reset the Search View and return to normal toolbar
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void searchTitles(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }
}