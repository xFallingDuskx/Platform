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
    String value;
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
        value = sharedCatalogViewModel.getValue();
        Log.i(TAG, "The value received is " + value);

        // Create TreeMap of TV show and Movies
        allGenres = new ArrayList<>();
        if (value.equals("tv")) {
            initializeTvGenres();
        } else {
            initializeMovieGenres();
        }

        // Set up view
        rvListedGenres = view.findViewById(R.id.rvListedGenres);
        adapter = new GenreAdapter(getContext(), allGenres);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvListedGenres.setLayoutManager(linearLayoutManager);
        rvListedGenres.setAdapter(adapter);
    }

    public void initializeTvGenres() {
        allGenres.add(new Genre("Action & Adventure", 10759, value));
        allGenres.add(new Genre("Animation", 16, value));
        allGenres.add(new Genre("Comedy", 35, value));
        allGenres.add(new Genre("Crime", 80, value));
        allGenres.add(new Genre("Documentary", 99, value));
        allGenres.add(new Genre("Drama", 18, value));
        allGenres.add(new Genre("Family", 10751, value));
        allGenres.add(new Genre("Kids", 10762, value));
        allGenres.add(new Genre("Mystery", 9648, value));
        allGenres.add(new Genre("News", 10763, value));
        allGenres.add(new Genre("Reality", 10, value));
        allGenres.add(new Genre("Sci-Fi & Fantasy", 10765, value));
        allGenres.add(new Genre("Soap", 10766, value));
        allGenres.add(new Genre("Talk", 10767, value));
        allGenres.add(new Genre("War & Politics", 10768, value));
        allGenres.add(new Genre("Western", 37, value));
    }

    public void initializeMovieGenres() {
        allGenres.add(new Genre("Action", 28, value));
        allGenres.add(new Genre("Adventure", 12, value));
        allGenres.add(new Genre("Animation", 16, value));
        allGenres.add(new Genre("Comedy", 35, value));
        allGenres.add(new Genre("Crime", 80, value));
        allGenres.add(new Genre("Documentary", 99, value));
        allGenres.add(new Genre("Drama", 18, value));
        allGenres.add(new Genre("Family", 10751, value));
        allGenres.add(new Genre("Fantasy", 14, value));
        allGenres.add(new Genre("History", 36, value));
        allGenres.add(new Genre("Horror", 27, value));
        allGenres.add(new Genre("Music", 10402, value));
        allGenres.add(new Genre("Mystery", 9648, value));
        allGenres.add(new Genre("Romance", 10749, value));
        allGenres.add(new Genre("Science Fiction", 878, value));
        allGenres.add(new Genre("TV Movie", 10770, value));
        allGenres.add(new Genre("Thriller", 53, value));
        allGenres.add(new Genre("War", 10752, value));
        allGenres.add(new Genre("Western", 37, value));
    }
}