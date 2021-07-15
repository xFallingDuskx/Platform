package com.example.platform.models;

import android.graphics.Movie;
import android.util.Log;
import android.widget.Toast;

import com.example.platform.activities.LoginActivity;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParseClassName("Title")
public class Title extends ParseObject {

    private static final String TAG = "Title";

    String backdropPath;
    String posterPath;
    String name;
    String description;
    Integer id;
    String releaseDate;
    String type;


    public static final String KEY_TMDB_ID = "tmdbID";
    public static final String KEY_NAME = "name";
    public static final String KEY_COVER_PATH = "coverPath";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_ACTORS = "actors";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_AVAILABLE_ON = "availableOn";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_SHARES = "shares";
    public static final String KEY_SEASONS = "seasons";
    public static final String KEY_NUMBER_OF_EPISODES = "numberOfEpisodes";

    // Default, empty constructor
    public Title() {
    }

    public Title(JSONObject jsonObject) throws JSONException{
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");

        // If there is no description
        description = jsonObject.getString("overview");
        if (description.isEmpty()) {
            description = "Sorry! No overview is available for the title.";
        }

        id = jsonObject.getInt("id");
        Log.i(TAG, "Value of TMBD ID: " + id);

        // Check if Title is a Movie, TV Show, or Episode
        if (jsonObject.has("release_date")) { // Movie
            name = jsonObject.getString("title");
            releaseDate = jsonObject.getString("release_date");
            type = "Movie";
        } else if (jsonObject.has("first_air_date")) { // TV Show
            name = jsonObject.getString("name");
            releaseDate = jsonObject.getString("first_air_date");
            type = "TV Show";
        } else if (jsonObject.has("air_date")) { // Episode
            releaseDate = jsonObject.getString("air_date");
            type = "Episode";
        }
    }

    // Convert JSONArray into List<Titles>
    public static List<Title> fromJsonArray(JSONArray titleJsonArray) throws JSONException {
        List<Title> titles = new ArrayList<>();
        for(int i = 0; i < titleJsonArray.length(); i++) {
            titles.add(new Title(titleJsonArray.getJSONObject(i)));
        }
        return titles;
    }


    // TODO: Assign a getter and setter method for each key value
    public Integer getId() {return id;}

    public String getObjectId() {
        return getString("objectId");
    }

    public String getBackdropPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);}

    public String getPosterPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Change date format from YYYY-DD-MM to DD/MM/YYYY
    public String getReleaseDate() {
        String[] dateArray = releaseDate.split("-");
        List<String> dateList = new ArrayList<>(Arrays.asList(dateArray));
        String year = dateList.remove(0);
        dateList.add(year); // Move year of title to the end
        String date = dateList.get(0) + "/" + dateList.get(1) + "/" + dateList.get(2);
        return date;
    }

    public String getType() {return type;}

    public Integer getLikes() {return getInt(KEY_LIKES);}

    public Integer getShare() {return getInt(KEY_SHARES);}

//    public String getType() {
//        return getString(KEY_TYPE);
//    }

//    public Array getGenres() {
//        JSONArray genres = getJSONArray(KEY_GENRES);
//        return;
//    }
//
//    public Array getActors() {
//        JSONArray actors = getJSONArray(KEY_ACTORS);
//        return;
//    }
//
//    public Array getAvailableOn() {
//        JSONArray providers = getJSONArray(KEY_AVAILABLE_ON);
//        return;
//    }
//
//    public int getLikes() {
//        return getInt(KEY_LIKES);
//    }
//
//    public Array getComments() {
//        JSONArray comments = getJSONArray(KEY_COMMENTS);
//        return;
//    }
//
//    public int getShares() {
//        return getInt(KEY_SHARES);
//    }
//
//    public Array getSeasons() {
//        JSONArray seasons = getJSONArray(KEY_SEASONS);
//        return;
//    }
//
//    public int getNumberOfEpisodes() {
//        return getInt(KEY_NUMBER_OF_EPISODES);
//    }
}