package com.example.platform.fragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.platform.R;
import com.example.platform.adapters.TitlesAdapter;
import com.example.platform.models.Title;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
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
 * Use the {@link HomeFragment_TvShows#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment_TvShows extends Fragment {

    public static final String TAG = "HomeFragment_TvShows";
    public String POPULAR_TV_SHOWS_URL = "https://api.themoviedb.org/3/tv/popular?api_key=e2b0127db9175584999a612837ae77b1";

    RecyclerView rvTitles;
    List<Title> allTitles;
    Set<Integer> savedTitles;
    TitlesAdapter adapter;
    ProgressBar progressBar;
    TextView tvLoadingMessage;

    public HomeFragment_TvShows() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment_TvShows.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment_TvShows newInstance(int page) {
        Bundle args = new Bundle();
        HomeFragment_TvShows fragment = new HomeFragment_TvShows();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home__tv_shows, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTitles = view.findViewById(R.id.rvTitles_TV_Shows);
        allTitles = new ArrayList<>();
        savedTitles = new HashSet<>();
        adapter = new TitlesAdapter(getContext(), allTitles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTitles.setLayoutManager(linearLayoutManager);
        rvTitles.setAdapter(adapter);
        progressBar = view.findViewById(R.id.pbHome_TV_Shows);
        tvLoadingMessage = view.findViewById(R.id.tvLoadingMessage_TV_Shows);

        displayTitles();
    }

    private void displayTitles() {
        showProgressBar(); // Make progressBar visible
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
                    List<List<String>> newTitleInformation = Title.getStringFormattedData(newTitles); // Purpose of saving to Parse
                    updateParseServer(newTitleInformation); // Purpose of saving to Parse
                    allTitles.addAll(Title.fromJsonArray(results));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Titles: " + allTitles.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception" + " Exception: " + e);
                    e.printStackTrace();
                } catch (ParseException e) {
                    Log.e(TAG, "Hit ParseException while attempting to updateParseServer / Message: " + e.getMessage());
                    e.printStackTrace();
                }
                hideProgressBar(); // Make progressBar invisible
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure to display titles / Response: " + response + " / Error: " + throwable);
            }
        });
    }

    // First check if Title already exist in the Parse Server
    // Requires making a query for Titles within the server that contain the same unique TMDB ID #
    private void updateParseServer(List<List<String>> newTitles) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");

        for (List<String> titleInfo : newTitles) {
            Integer titleID = Integer.valueOf(titleInfo.get(0));
            query.whereEqualTo(Title.KEY_TMDB_ID, titleID);
            if (query.count() == 0) {
                savedTitles.add(titleID);
                saveTitle(titleInfo);
            }
        }
    }

    // Save Title in the Parse Server if it does not exist
    private void saveTitle(List<String> titleInfo) {
        Title newTitle = new Title();
        newTitle.put(Title.KEY_TMDB_ID, Integer.valueOf(titleInfo.get(0)));
        newTitle.put(Title.KEY_NAME, titleInfo.get(1));
        newTitle.put(Title.KEY_COVER_PATH, titleInfo.get(2));
        newTitle.put(Title.KEY_TYPE, titleInfo.get(3));
        newTitle.put(Title.KEY_DESCRIPTION, titleInfo.get(4));
        // TODO: Genres
        // TODO: Actors
        newTitle.put(Title.KEY_RELEASE_DATE, titleInfo.get(5));
        // TODO: Available on
        newTitle.put(Title.KEY_LIKES, 0);
        // TODO: Comments
        newTitle.put(Title.KEY_SHARES, 0);
        // TODO: Seasons
        // TODO: Episodes

        newTitle.saveInBackground(e -> {
            if (e != null){
                Log.e(TAG, "Issue saving title / Title: " + titleInfo.get(1) + " / Message: " + e.getMessage());
            } else {
                Log.i(TAG, "Success saving the title: " + titleInfo.get(1));
            }
        });
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoadingMessage.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        tvLoadingMessage.setVisibility(View.INVISIBLE);
    }
}