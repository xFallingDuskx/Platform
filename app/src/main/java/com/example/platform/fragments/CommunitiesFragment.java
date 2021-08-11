package com.example.platform.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.activities.CommunitiesActivity_ByGenre;
import com.example.platform.activities.CommunitiesActivity_Create;
import com.example.platform.activities.CommunitiesActivity_Display;
import com.example.platform.activities.ProfileActivity;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CommunitiesFragment extends Fragment {

    public static final String TAG = "CommunitiesFragment";
    private ImageView ivProfile;
    RelativeLayout rlAll, rlPopular, rlByGenres, rlUserCommunities;
    EditText etSearchInput;

    public CommunitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_communities, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_communities, menu);

        // Allow user to start new chats
        MenuItem createItem = menu.findItem(R.id.miCreate);
        createItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i(TAG, "onMenuItemClick to create a new community");
                Intent intent = new Intent(getContext(), CommunitiesActivity_Create.class);
                startActivity(intent);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_Communities);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = getActivity().findViewById(R.id.ivProfileIcon_Communities);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Handle searches
        // Source: https://www.bragitoff.com/2017/04/trigger-button-click-press-doneenter-key-keyboard-solved/
        etSearchInput = (EditText) view.findViewById(R.id.etSearchInput);
        etSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getContext(), CommunitiesActivity_Display.class);
                    String search = etSearchInput.getText().toString();
                    if (search.isEmpty()) {
                        Toast.makeText(getContext(), "Search cannot be empty", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    intent.putExtra("objective", "search");
                    intent.putExtra("query", search);
                    startActivity(intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            etSearchInput.setText("");
                        }
                    }, 2000);
                    handled = true;
                }
                return handled;
            }
        });

        // Set up views and onClick listeners for objectiveives
        rlAll = view.findViewById(R.id.rlViewAll);
        rlAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommunitiesActivity_Display.class);
                intent.putExtra("objective", "all");
                startActivity(intent);
            }
        });

        rlPopular = view.findViewById(R.id.rlPopular);
        rlPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommunitiesActivity_Display.class);
                intent.putExtra("objective", "popular");
                startActivity(intent);
            }
        });

        rlByGenres = view.findViewById(R.id.rlByGenres);
        rlByGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommunitiesActivity_ByGenre.class);
                startActivity(intent);
            }
        });

        rlUserCommunities = view.findViewById(R.id.rlUserCommunities);
        rlUserCommunities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CommunitiesActivity_Display.class);
                intent.putExtra("objective", "user");
                startActivity(intent);
            }
        });
    }
}