package com.example.platform.models;

import android.graphics.Movie;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Title")
public class Title extends ParseObject {

    String backdropPath;
    String posterPath;
    String name;
    String description;
    Integer id;
    String releaseDate;

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
        name = jsonObject.getString("name");
        description = jsonObject.getString("overview");
        id = jsonObject.getInt("id");
        releaseDate = jsonObject.getString("first_air_date");

        // TODO: create method to create Title Parse object
        // ParseObject title = new Title();

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
    public String getBackdropPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);}

    public String getPosterPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);}

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (description.isEmpty()) {
            description = "Sorry! No overview is available for the title.";
        }
        return description;
    }

    public Integer getId() {return id;}

    public String getReleaseDate() {return releaseDate;}

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