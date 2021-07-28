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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.platform.R;
import com.example.platform.activities.ProfileActivity;
import com.example.platform.activities.SearchActivity;
import com.example.platform.models.SharedCatalogViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CatalogFragment_Movie extends Fragment {

    public static final String TAG = "CatalogFragment_Movie";
    private ImageView ivProfile;
    private RelativeLayout rlRecentTitles;
    private RelativeLayout rlPopularTitles;
    private RelativeLayout rlByGenre;
    private RelativeLayout rlByPlatform;
    private SharedCatalogViewModel sharedCatalogViewModel;
    FragmentManager fragmentManager;

    public CatalogFragment_Movie() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alert view that menu items exist
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog__movie, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.toolbar_home, menu);

        // Allow user to search Titles
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Change appearance of EditText for the Search View
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHint("Search Movies");
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchTitles(String query) {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("query", query);
        intent.putExtra("type", "movie");
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Handle information shared between fragments
        sharedCatalogViewModel = new ViewModelProvider(requireActivity()).get(SharedCatalogViewModel.class);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_Catalog_Movie);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = getActivity().findViewById(R.id.ivProfileIcon_Catalog);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Take user to appropriate catalog option depending on their selection
        rlRecentTitles = getActivity().findViewById(R.id.rlCatalogOption_Movie_Recent);
        rlPopularTitles = getActivity().findViewById(R.id.rlCatalogOption_Movie_Popular);
        rlByGenre = getActivity().findViewById(R.id.rlCatalogOption_Movie_Genre);
        rlByPlatform = getActivity().findViewById(R.id.rlCatalogOption_Movie_Platform);

        fragmentManager = getActivity().getSupportFragmentManager();

        rlRecentTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.flContainer, CatalogFragment_RecentTitles.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                sharedCatalogViewModel.setMediaType("movie");
            }
        });

        rlPopularTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.flContainer, CatalogFragment_PopularTitles.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                sharedCatalogViewModel.setMediaType("movie");
            }
        });

        rlByGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.flContainer, CatalogFragment_ByGenre.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                sharedCatalogViewModel.setMediaType("movie");
            }
        });

        rlByPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.flContainer, CatalogFragment_ByPlatform.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                sharedCatalogViewModel.setMediaType("movie");
            }
        });
    }
}