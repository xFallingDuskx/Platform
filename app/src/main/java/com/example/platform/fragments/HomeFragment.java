package com.example.platform.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.TitlesAdapter;
import com.example.platform.models.Title;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    // TODO: HIDE API KEY IN STRING RESOURCES
//    public String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + etActivity().getResources().getString(R.string.tmdbApiKey);
    public String POPULAR_TV_SHOWS_URL = "https://api.themoviedb.org/3/tv/popular?api_key=e2b0127db9175584999a612837ae77b1";
    RecyclerView rvTitles;
    List<Title> allTitles;
    Set<Integer> savedTitles;
    TitlesAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTitles = view.findViewById(R.id.rvTitles);
        allTitles = new ArrayList<>();
        savedTitles = new HashSet<>();
        adapter = new TitlesAdapter(getContext(), allTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTitles.setLayoutManager(linearLayoutManager);
        rvTitles.setAdapter(adapter);
        
        displayTitles();
    }

    private void displayTitles() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(POPULAR_TV_SHOWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess to display titles");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    List<Title> newTitles = Title.fromJsonArray(results);
                    updateParseServer(newTitles);
                    allTitles.addAll(newTitles);
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Titles: " + allTitles.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to display titles / Response: " + response + " / Error: " + throwable);
            }
        });
    }

    private void updateParseServer(List<Title> newTitles) {
        // First check what title already exist on the server using their unique TMDb ID #
        checkExistingTitles();
        // Then checking the titles that are being added to the RecyclerView
        // Save any titles on the server if they are not their
        checkNewTitles(newTitles);
    }

    private void checkExistingTitles() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> titles, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting Title objects");
                    return;
                }
                Log.i(TAG, "checkTitles - entering the for loop");
                for (ParseObject title : titles) {
                    savedTitles.add(title.getInt(Title.KEY_TMDB_ID));
                }
            }
        });
    }

    private void checkNewTitles(List<Title> newTitles) {
        for (Title title : newTitles) {
            Integer titleID = title.getId();
            if (! savedTitles.contains(titleID)) {
                saveTitle(title);
            }
        }
    }

    private void saveTitle(Title title) {
        Title newTitle = new Title();
        newTitle.put(Title.KEY_TMDB_ID, title.getId());
        newTitle.put(Title.KEY_NAME, title.getName());
        newTitle.put(Title.KEY_COVER_PATH, title.getPosterPath());
        newTitle.put(Title.KEY_TYPE, title.getType());
        newTitle.put(Title.KEY_DESCRIPTION, title.getDescription());
        // TODO: Genres
        // TODO: Actors
        newTitle.put(Title.KEY_RELEASE_DATE, title.getReleaseDate());
        // TODO: Available on
        newTitle.put(Title.KEY_LIKES, title.getLikes());
        // TODO: Comments
        newTitle.put(Title.KEY_SHARES, title.getShare());
        // TODO: Seasons
        // TODO: Episodes

        newTitle.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + title.getName() + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + title.getName());
            }
        });
    }
}
