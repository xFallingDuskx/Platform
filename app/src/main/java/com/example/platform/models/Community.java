package com.example.platform.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Community")
public class Community extends ParseObject {

    public static final String TAG = "Community";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CREATOR = "creator";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_KEYWORDS = "keywords";
    public static final String KEY_NUMBER_OF_MEMBERS = "numberOfMembers";

    public String name;
    public String description;
    public String creator;
    public List<String> members;
    public List<String> genres;
    public List<String> keywords;

    // Default, empty constructor
    public Community() {}

    public void setName(String name) {
        put(Community.KEY_NAME, name);
    }

    public String getName() {
        return getString(Community.KEY_NAME);
    }

    public void setDescription(String description) {
        put(Community.KEY_DESCRIPTION, description);
    }

    public String getDescription() {
        return getString(Community.KEY_DESCRIPTION);
    }

    public void setCreator(String creator) {
        put(Community.KEY_CREATOR, creator);
    }

    public String getCreator() {
        return getString(Community.KEY_CREATOR);
    }

    public void setMembers(List<String> members) {
        put(Community.KEY_MEMBERS, members);
    }

    public List<String> getMembers() throws JSONException {
        JSONArray jsonArray = getJSONArray(Community.KEY_MEMBERS);
        List<String> members = new ArrayList<>();

        if (jsonArray == null) {
            return members;
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                members.add(jsonArray.getString(i));
            }
            return members;
        }
    }

    public void setGenres(List<String> genres) {
        put(Community.KEY_GENRES, genres);
        this.genres = genres;
    }

    public List<String> getGenres() throws JSONException {
        JSONArray jsonArray = getJSONArray(Community.KEY_GENRES);
        List<String> genres = new ArrayList<>();

        if (jsonArray == null) {
            return genres;
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                genres.add(jsonArray.getString(i));
            }
            return genres;
        }
    }

    public void setKeywords(List<String> keywords) {
        put(Community.KEY_KEYWORDS, keywords);
        this.keywords = keywords;
    }

    public List<String> getKeywords() throws JSONException {
        JSONArray jsonArray = getJSONArray(Community.KEY_GENRES);
        List<String> keywords = new ArrayList<>();

        if (jsonArray == null) {
            return keywords;
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                keywords.add(jsonArray.getString(i));
            }
            return keywords;
        }
    }

    public void setNumberOfMembers(int numberOfMembers) {
        put(KEY_NUMBER_OF_MEMBERS, numberOfMembers);
    }

    public int getNumberOfMembers() {
        return getInt(KEY_NUMBER_OF_MEMBERS);
    }
}
