package com.example.platform.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.platform.R;
import com.example.platform.adapters.GenreAdapter;
import com.example.platform.adapters.ProviderAdapter;
import com.example.platform.models.Genre;
import com.example.platform.models.Provider;
import com.example.platform.models.SharedCatalogViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CatalogFragment_ByProvider extends Fragment {

    public static final String TAG = "CatalogFragment_ByProvider";
    private SharedCatalogViewModel sharedCatalogViewModel;
    private RecyclerView rvListedPlatforms;
    FragmentManager fragmentManager;
    String mediaType;
    List<Provider> allProvider;
    ProviderAdapter adapter;

    public CatalogFragment_ByProvider() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog__by_provider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Handle information shared between fragments
        sharedCatalogViewModel = new ViewModelProvider(requireActivity()).get(SharedCatalogViewModel.class);
        mediaType = sharedCatalogViewModel.getMediaType();
        Log.i(TAG, "The value received is " + mediaType);

        // Create List<Genre> depending on the type of title
        allProvider = new ArrayList<>();
        if (mediaType.equals("tv")) {
            initializeTvPlatforms();
        } else {
            initializeMoviePlatforms();
        }

        // Set up RecyclerView
        rvListedPlatforms = view.findViewById(R.id.rvListedProviders);
        adapter = new ProviderAdapter(getContext(), allProvider);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvListedPlatforms.setLayoutManager(linearLayoutManager);
        rvListedPlatforms.setAdapter(adapter);
    }

    public void initializeTvPlatforms() {
        allProvider.add(new Provider("Amazon Prime Videos", "119|9|10", mediaType));
        allProvider.add(new Provider("Apple", "2|350", mediaType));
        allProvider.add(new Provider("Crackle", "12", mediaType));
        allProvider.add(new Provider("Crunchyroll", "283", mediaType));
        allProvider.add(new Provider("CW Seed", "206", mediaType));
        allProvider.add(new Provider("Disney Plus", "337|390|520", mediaType));
        allProvider.add(new Provider("HBO", "280|118|384|425|31|27", mediaType));
        allProvider.add(new Provider("Hulu", "15", mediaType));
        allProvider.add(new Provider("Netflix", "8|175", mediaType));
        allProvider.add(new Provider("Paramount+", "531|", mediaType));
        allProvider.add(new Provider("Peacock", "386|387", mediaType));
        allProvider.add(new Provider("The CW", "83", mediaType));
        allProvider.add(new Provider("Tubi TV", "73", mediaType));
        allProvider.add(new Provider("Vudu", "7|332", mediaType));
    }

    public void initializeMoviePlatforms() {
        allProvider.add(new Provider("Amazon Prime Videos", "119|9|10", mediaType));
        allProvider.add(new Provider("Apple", "2|350", mediaType));
        allProvider.add(new Provider("Crackle", "12", mediaType));
        allProvider.add(new Provider("Crunchyroll", "283", mediaType));
        allProvider.add(new Provider("Disney Plus", "337|390|520", mediaType));
        allProvider.add(new Provider("HBO", "280|118|384|425|31|27", mediaType));
        allProvider.add(new Provider("Hulu", "15", mediaType));
        allProvider.add(new Provider("Netflix", "8|175", mediaType));
        allProvider.add(new Provider("Paramount+", "531|", mediaType));
        allProvider.add(new Provider("Peacock", "386|387", mediaType));
        allProvider.add(new Provider("Tubi TV", "73", mediaType));
        allProvider.add(new Provider("Vudu", "7|332", mediaType));
    }
}