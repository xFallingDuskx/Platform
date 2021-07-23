package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
//    ParseObject parseObject;

    public static final String KEY_TMDB_ID = "tmdbID";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_SHARES = "shares";

    // Default, empty constructor
    public Title() {}

    public Title(JSONObject jsonObject) throws JSONException{
        backdropPath = String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("backdrop_path"));
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

//        try {
//            parseObject = ParseQuery.getQuery("Title").whereEqualTo(KEY_TMDB_ID, id).getFirst();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    // Convert JSONArray into List<Titles>
    public static List<Title> fromJsonArray(JSONArray titleJsonArray) throws JSONException {
        List<Title> titles = new ArrayList<>();
        for(int i = 0; i < titleJsonArray.length(); i++) {
            JSONObject jsonObject = titleJsonArray.getJSONObject(i);
            if (jsonObject.has("media_type")) { // ignore Person media types when searching for Titles to only get TV Shows and Movies
                if (jsonObject.getString("media_type").equals("person")) {
                    continue;
                }
            }
            Title title = new Title(titleJsonArray.getJSONObject(i));
            titles.add(title);
        }
        return titles;
    }

    public String convertDate(String currentForm) {
        if (currentForm.isEmpty()) {
            return "No Date Provided";
        }
        String[] dateArray = currentForm.split("-");
        List<String> dateList = new ArrayList<>(Arrays.asList(dateArray));
        String year = dateList.remove(0);
        dateList.add(year); // Move year of title to the end
        String newForm = dateList.get(0) + "/" + dateList.get(1) + "/" + dateList.get(2);
        return newForm;
    }

    // Getters and setters method for each key value
    public Integer getId() {
        Log.d(TAG, "Getting the ID");
        return id;
    }

    public void setId(Integer tmdbID) {
        Log.d(TAG, "Setting the ID");
        this.id = tmdbID;
        put(KEY_TMDB_ID, tmdbID);
    }

    public String getObjectID() {
        return getObjectId();
    }

//    public String getBackdropPath() {return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);}

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Change date format from YYYY-DD-MM to DD/MM/YYYY
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLikes() {
        Log.d(TAG, "Getting the likes for the title " + name + ": " + getInt(KEY_LIKES));
        return getInt(KEY_LIKES);
    }

    public void setLikes(Integer integer) {
        Log.d(TAG, "Saving the likes for the title " + name + ": " + getInt(KEY_LIKES));
        put(KEY_LIKES, integer);
    }

    public Integer getShare() {
        return getInt(KEY_SHARES);
    }

    public void setShares(Integer integer) {
        put(KEY_SHARES, integer);
    }

//    public void setParseObject(ParseObject parseObject) {
//        this.parseObject = parseObject;
//    }
//
//    public ParseObject getParseObject() {
//        return parseObject;
//    }

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