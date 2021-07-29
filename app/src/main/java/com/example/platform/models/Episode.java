package com.example.platform.models;

import com.parse.ParseClassName;
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
    String titleName;
    Integer id;
    Integer seasonNumber;
    Integer episodeNumber;
    String description;
    String releaseDate;
    String titleCoverPath;
    ParseObject parseObject;

    public static final String KEY_TMDB_ID = "tmdbID";

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

        // Finish setting up the required data for the title
        parseSetUp();
        // Get the title as it exist in the Parse Server using its TMDB ID
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Episode");
        ParseQuery<ParseObject> updatedQuery = query.whereEqualTo(KEY_TMDB_ID, id);
        parseObject = updatedQuery.getFirstInBackground().getResult();
    }

    // Convert JSONArray into List<Titles>
    public static List<Episode> fromJsonArray(String titleCoverPath, JSONArray episodeJsonArray) throws JSONException {
        List<Episode> episodes = new ArrayList<>();
        for(int i = 0; i < episodeJsonArray.length(); i++) {
            Episode episode = new Episode(episodeJsonArray.getJSONObject(i));
            episode.setTitleCoverPath(titleCoverPath);
            episodes.add(episode);
        }
        return episodes;
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

    public void parseSetUp() {
        setId(getId());
    }

    // Getters and setters

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleName() {
        return "Name";
    }

    public void setTitleName(String title) {
        this.titleName = title ;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        put(KEY_TMDB_ID, id);
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setParseObject(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public String getTitleCoverPath() {
        return titleCoverPath;
    }

    public void setTitleCoverPath(String titleCoverPath) {
        this.titleCoverPath = titleCoverPath;
    }
}
