package com.example.platform.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.fragments.CatalogFragment;
import com.example.platform.fragments.ConversationsFragment;
import com.example.platform.fragments.CommunitiesFragment;
import com.example.platform.fragments.HomeFragment;
import com.example.platform.fragments.InboxFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

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
                        fragment = new ConversationsFragment();
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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "The message token is: " + token;
                        Log.d(TAG, msg);
                        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        // todo: Subscribe to topics
//        FirebaseMessaging.getInstance().subscribeToTopic("weather")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = "testing this new message";
//                        if (!task.isSuccessful()) {
//                            msg = "task is not successful";
//                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });
    }
}