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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Parcel (analyze = Title.class)
@ParseClassName("Title")
public class Title extends ParseObject implements Serializable {

    private static final String TAG = "Title";

    String backdropPath;
    String posterPath;
    String name;
    String description;
    Integer id;
    String releaseDate;
    String type;

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
    ParseObject parseObject;

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
        posterPath = String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("poster_path"));

        // If there is no description
        description = jsonObject.getString("overview");
        if (description.isEmpty()) {
            description = "Sorry! No overview is available for the title.";
        }

        id = jsonObject.getInt("id");

        // Check if Title is a Movie, TV Show, or Episode
        if (jsonObject.has("release_date")) { // Movie
            name = jsonObject.getString("title");
            releaseDate = jsonObject.getString("release_date");
            releaseDate = convertDate(releaseDate);
            type = "Movie";
        } else if (jsonObject.has("first_air_date")) { // TV Show
            name = jsonObject.getString("name");
            releaseDate = jsonObject.getString("first_air_date");
            releaseDate = convertDate(releaseDate);
            type = "TV Show";
        } else if (jsonObject.has("air_date")) { // Episode
            releaseDate = jsonObject.getString("air_date");
            releaseDate = convertDate(releaseDate);
            type = "Episode";
        }

        // To read Parse object for data
        ParseQuery<ParseObject> updateQuery = query.whereEqualTo(KEY_TMDB_ID, id);
        try {
            parseObject = updateQuery.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "Issue reading Parse Object");
            e.printStackTrace();
        }
    }

    // Convert JSONArray into List<Titles>
    public static List<Title> fromJsonArray(JSONArray titleJsonArray) throws JSONException {
        List<Title> titles = new ArrayList<>();
        for(int i = 0; i < titleJsonArray.length(); i++) {
            Title title = new Title(titleJsonArray.getJSONObject(i));
            titles.add(title);
        }
        return titles;
    }

    // Convert JSONArray into List<Titles>
    public static List<List<String>> getStringFormattedData(List<Title> newTitles) {
        List<List<String>> allTitleInformation = new ArrayList<>();
        for(Title title : newTitles) { // Add each piece of information of the Title in String format
            List<String> titleInformation = new ArrayList<>();
            titleInformation.add(String.valueOf(title.id)); // Index 0
            titleInformation.add(title.name); // Index 1
            titleInformation.add(title.posterPath); // Index 2
            titleInformation.add(title.type); // Index 3
            titleInformation.add(title.description); // Index 4
            titleInformation.add(title.releaseDate); // Index 5
            allTitleInformation.add(titleInformation); // Index 6
        }
        return allTitleInformation;
    }

    // Convert JSONObject into a List<String> contained additional Title information
    public static List<String> getAdditionalTvInformation(JSONObject jsonObject) {
        List<String> additionalInfo = new ArrayList<>();

        return additionalInfo;
    }

    // Getters and setters method for each key value
    public Integer getId() {
        Log.i(TAG, "Getting... Title: " + getName() + " / TMDB ID: " + parseObject.getInt(KEY_TMDB_ID) + " / Object ID: " + parseObject.getObjectId());
        return parseObject.getInt(KEY_TMDB_ID);
    }

    public void setId(Integer tmdbID) {
        this.id = tmdbID;
        this.put(KEY_TMDB_ID, tmdbID);
    }

//    public String getBackdropPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);}

    public String getCoverPath() {
        return parseObject.getString(KEY_COVER_PATH);
    }

    public void setCoverPath(String posterPath) {
        this.put(KEY_COVER_PATH, posterPath);
    }

    public String getName() {
        return parseObject.getString(KEY_NAME);
    }

    public void setName(String name) {
        this.put(KEY_NAME, name);
    }

    public String getDescription() {
        return parseObject.getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        this.put(KEY_DESCRIPTION, description);
    }

    // Change date format from YYYY-DD-MM to DD/MM/YYYY
    public String getReleaseDate() {
        return parseObject.getString(KEY_RELEASE_DATE);
    }

    public void setReleaseDate(String releaseDate) {
        this.put(KEY_RELEASE_DATE, releaseDate);
    }

    public String convertDate(String currentForm) {
        if (currentForm.isEmpty()) {
            return "No Date Provided";
        }
        String[] dateArray = currentForm.split("-");
        List<String> dateList = new ArrayList<>(Arrays.asList(dateArray));
        String year = dateList.remove(0);
        dateList.add(year); // Move year of title to the end
        Log.i(TAG, "The new form is: " + String.valueOf(dateList));
        String newForm = dateList.get(0) + "/" + dateList.get(1) + "/" + dateList.get(2);
        return newForm;
    }

    public String getType() {
        return parseObject.getString(KEY_TYPE);
    }

    public void setType(String type) {
        this.put(KEY_TYPE, type);
    }

    public Integer getLikes() {
        Log.i(TAG, "Title: " + getName() + " / Likes: " + getInt(KEY_LIKES));
        return parseObject.getInt(KEY_LIKES);
    }

    public void setLikes() {
        this.put(KEY_LIKES, 0);
    }

    public Integer getShare() {
        Log.i(TAG, "Title: " + getName() + " / Shares: " + getInt(KEY_SHARES));
        return parseObject.getInt(KEY_SHARES);
    }

    public void setShares() {
        this.put(KEY_SHARES, 0);
    }

    // TODO: MAKE SURE TO DO BOTH GETTERS AND SETTERS
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