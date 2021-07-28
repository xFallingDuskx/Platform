package com.example.platform.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    String originalCountry;


    public static final String KEY_TMDB_ID = "tmdbID";
    public static final String KEY_TMDBID_LOWER = "tmdbId";
    public static final String KEY_NAME = "name";
    public static final String KEY_POSTERPATH = "posterPath";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RELEASE_DATE = "releaseDate";
    public static final String KEY_KEYWORDS = "keywords";

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
        return id;
    }

    public void setId(Integer tmdbID) {
        this.id = tmdbID;
        put(KEY_TMDB_ID, tmdbID);
    }

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
}