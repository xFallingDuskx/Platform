package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParseClassName("Episode")
public class Episode extends ParseObject {

    public static final String TAG = "Episode";

    String stillPath;
    String name;
    Integer id;
    Integer seasonNumber;
    Integer episodeNumber;
    String description;
    String releaseDate;

    public static final String KEY_TMDB_ID = "tmdbID";
    public static final String KEY_TITLE_NAME = "titleName";
    public static final String KEY_NAME = "name";
    public static final String KEY_STILL_PATH = "stillPath";
    public static final String KEY_SEASON_NUMBER = "seasonNumber";
    public static final String KEY_EPISODE_NUMBER = "episodeNumber";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RELEASE_DATE = "releaseDate";

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Episode");
    ParseObject parseObject;

    // Empty default constructor
    public Episode() {}

    public Episode(JSONObject jsonObject) throws JSONException {
        stillPath = String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("still_path"));
        name = jsonObject.getString("name");
        id = jsonObject.getInt("id");
        seasonNumber = jsonObject.getInt("season_number");
        episodeNumber = jsonObject.getInt("episode_number");
        releaseDate = convertDate(jsonObject.getString("air_date"));

        // In case there is no description for a title
        description = jsonObject.getString("overview");
        if (description.isEmpty()) {
            description = "Sorry! There is no description available!";
        }

        // To read Parse object for data
        ParseQuery<ParseObject> updateQuery = query.whereEqualTo(KEY_TMDB_ID, id);
        try {
            parseObject = updateQuery.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "Issue reading Parse Object / Title: " + name + " / TMDB ID: " + id);
            e.printStackTrace();
        }
    }

    // Convert JSONArray into List<Titles>
    public static List<Episode> fromJsonArray(JSONArray episodeJsonArray) throws JSONException {
        List<Episode> episodes = new ArrayList<>();
        for(int i = 0; i < episodeJsonArray.length(); i++) {
            Episode episode = new Episode(episodeJsonArray.getJSONObject(i));
            episodes.add(episode);
            if (i == 5) { // TODO: handle episode breaks and add endless scrolling
                return episodes;
            }
        }
        return episodes;
    }

    // Convert JSONArray into List<Episode>
    public static List<List<String>> getStringFormattedData(List<Episode> newEpisodes) {
        List<List<String>> allEpisodeInformation = new ArrayList<>();
        for(Episode episode : newEpisodes) { // Add each piece of information of the Title in String format
            List<String> episodeInformation = new ArrayList<>();
            episodeInformation.add(String.valueOf(episode.id)); // Index 0
            episodeInformation.add(episode.name); // Index 1
            episodeInformation.add(episode.stillPath); // Index 2
            episodeInformation.add(String.valueOf(episode.seasonNumber)); // Index 3
            episodeInformation.add(String.valueOf(episode.episodeNumber)); // Index 4
            episodeInformation.add(episode.description); // Index 5
            episodeInformation.add(episode.releaseDate); // Index 6
            allEpisodeInformation.add(episodeInformation);
        }
        return allEpisodeInformation;
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

    // Getters and setters

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        put(KEY_STILL_PATH, stillPath);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getTitleName() {
        return parseObject.getString(KEY_TITLE_NAME);
    }

    public void setTitleName(String title) {
        put(KEY_TITLE_NAME, title);
    }

    public Integer getId() {
        return id;
    }

    public void setID(Integer id) {
        put(KEY_TMDB_ID, id);
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        put(KEY_SEASON_NUMBER, seasonNumber);
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        put(KEY_EPISODE_NUMBER, episodeNumber);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        put(KEY_RELEASE_DATE, releaseDate);
    }
}
