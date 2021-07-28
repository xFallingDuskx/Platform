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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.example.platform.adapters.GenreAdapter;
import com.example.platform.adapters.TitlesAdapter;
import com.example.platform.models.Genre;
import com.example.platform.models.SharedCatalogViewModel;
import com.example.platform.models.Title;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CatalogFragment_ByGenre extends Fragment {

    public static final String TAG = "CatalogFragment_ByGenre";
    private SharedCatalogViewModel sharedCatalogViewModel;
    private RecyclerView rvListedGenres;
    FragmentManager fragmentManager;
    String mediaType;
    RecyclerView rvGenres;
    List<Genre> allGenres;
    GenreAdapter adapter;

    public CatalogFragment_ByGenre() {
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
        return inflater.inflate(R.layout.fragment_catalog__by_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Handle information shared between fragments
        sharedCatalogViewModel = new ViewModelProvider(requireActivity()).get(SharedCatalogViewModel.class);
        mediaType = sharedCatalogViewModel.getMediaType();
        Log.i(TAG, "The value received is " + mediaType);

        // Create List<Genre> depending on the type of title
        allGenres = new ArrayList<>();
        if (mediaType.equals("tv")) {
            initializeTvGenres();
        } else {
            initializeMovieGenres();
        }

        // Set up RecyclerView
        rvListedGenres = view.findViewById(R.id.rvListedGenres);
        adapter = new GenreAdapter(getContext(), allGenres);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvListedGenres.setLayoutManager(linearLayoutManager);
        rvListedGenres.setAdapter(adapter);
    }

    public void initializeTvGenres() {
        allGenres.add(new Genre("Action & Adventure", 10759, mediaType));
        allGenres.add(new Genre("Animation", 16, mediaType));
        allGenres.add(new Genre("Comedy", 35, mediaType));
        allGenres.add(new Genre("Crime", 80, mediaType));
        allGenres.add(new Genre("Documentary", 99, mediaType));
        allGenres.add(new Genre("Drama", 18, mediaType));
        allGenres.add(new Genre("Family", 10751, mediaType));
        allGenres.add(new Genre("Kids", 10762, mediaType));
        allGenres.add(new Genre("Mystery", 9648, mediaType));
        allGenres.add(new Genre("News", 10763, mediaType));
        allGenres.add(new Genre("Reality", 10, mediaType));
        allGenres.add(new Genre("Sci-Fi & Fantasy", 10765, mediaType));
        allGenres.add(new Genre("Soap", 10766, mediaType));
        allGenres.add(new Genre("Talk", 10767, mediaType));
        allGenres.add(new Genre("War & Politics", 10768, mediaType));
        allGenres.add(new Genre("Western", 37, mediaType));
    }

    public void initializeMovieGenres() {
        allGenres.add(new Genre("Action", 28, mediaType));
        allGenres.add(new Genre("Adventure", 12, mediaType));
        allGenres.add(new Genre("Animation", 16, mediaType));
        allGenres.add(new Genre("Comedy", 35, mediaType));
        allGenres.add(new Genre("Crime", 80, mediaType));
        allGenres.add(new Genre("Documentary", 99, mediaType));
        allGenres.add(new Genre("Drama", 18, mediaType));
        allGenres.add(new Genre("Family", 10751, mediaType));
        allGenres.add(new Genre("Fantasy", 14, mediaType));
        allGenres.add(new Genre("History", 36, mediaType));
        allGenres.add(new Genre("Horror", 27, mediaType));
        allGenres.add(new Genre("Music", 10402, mediaType));
        allGenres.add(new Genre("Mystery", 9648, mediaType));
        allGenres.add(new Genre("Romance", 10749, mediaType));
        allGenres.add(new Genre("Science Fiction", 878, mediaType));
        allGenres.add(new Genre("TV Movie", 10770, mediaType));
        allGenres.add(new Genre("Thriller", 53, mediaType));
        allGenres.add(new Genre("War", 10752, mediaType));
        allGenres.add(new Genre("Western", 37, mediaType));
    }
}