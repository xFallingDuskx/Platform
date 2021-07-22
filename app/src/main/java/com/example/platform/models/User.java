package com.example.platform.models;

import android.util.Log;

import com.facebook.stetho.json.ObjectMapper;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String TAG = "User";

    public User() {
    }

    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TITLE_LIKES = "titleLikes";
    public static final String KEY_LIKED_TITLES = "likedTitles";
    public static final String KEY_COMMENT_LIKES = "commentLikes";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_CHATS = "chats";
    public static final String KEY_COMMUNITIES = "communities";

    //TODO
    public Map<String, Object> getLikedTitles() {
        return getMap(KEY_LIKED_TITLES);
    }

    public void setLikedTitles(Map<String, Object> likedTitles) {
        JSONObject jsonMap = new ObjectMapper().convertValue(likedTitles, JSONObject.class);
        put(KEY_LIKED_TITLES, jsonMap);
    }

    // Get Titles liked by User based on the Titles' TMDB ID
//    public List<Integer> getTitleLikes() throws JSONException {
//        List<Integer> titles = new ArrayList<>();
//        JSONArray jsonArray = getJSONArray(KEY_TITLE_LIKES);
//
//        if (jsonArray != null) {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                Integer titleTmdbID = jsonArray.getJSONObject(i).getInt(Title.KEY_TMDB_ID);
//                titles.add(titleTmdbID);
//            }
//        }
//        return titles;
//    }
//
//    public void addTitleLike(Integer titleTmdbID) throws ParseException {
//        // First get the Title
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
//        query.whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID);
//        ParseObject parseObject = query.getFirst();
//        // Then save the like to the User profile
//        try {
//            handleLikeForUser(parseObject);
//        } catch (JSONException e) {
//            Log.i(TAG, "Issue handling likes for user");
//        }
//    }
//
//    public void handleLikeForTitle(ParseObject parseObject) {
//        Integer currentLikes = parseObject.getInt(Title.KEY_LIKES);
//        parseObject.put(Title.KEY_LIKES, currentLikes + 1);
//        parseObject.saveInBackground();
//        Log.i(TAG, "Like has been handled for Title");
//    }
//
//    public void handleLikeForUser(ParseObject parseObject) throws JSONException {
//        List<Integer> likedTitles = new ArrayList<>();
//        Integer toAddTmdbID = parseObject.getInt(Title.KEY_TMDB_ID);
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        JSONArray jsonArray = currentUser.getJSONArray(KEY_TITLE_LIKES);
//
//        for(int i = 0; i < jsonArray.length(); i++) {
//            Integer titleTmdbID = jsonArray.getInt(i);
//                if (titleTmdbID != toAddTmdbID) {
//                    likedTitles.add(titleTmdbID);
//                }
//        }
//
//        likedTitles.add(toAddTmdbID);
//        currentUser.put(KEY_TITLE_LIKES, likedTitles);
//        currentUser.saveInBackground();
//        Log.i(TAG, "Like has been handled for User");
//    }
//
//    public void removeTitleLike(Integer titleTmdbID) throws ParseException {
//        // First get the Title
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
//        query.whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID);
//        ParseObject parseObject = query.getFirst();
//        // Then decrease the likes for the Title by 1
//        handleUnlikeForTitle(parseObject);
//        // Finally remove the like from the User profile
//        try {
//            handleUnlikeForUser(parseObject);
//        } catch (JSONException e) {
//            Log.i(TAG, "Issue handling likes for user");
//        }
//    }
//
//    public void handleUnlikeForTitle(ParseObject parseObject) {
//        Integer currentLikes = parseObject.getInt(Title.KEY_LIKES);
//        parseObject.put(Title.KEY_LIKES, currentLikes - 1);
//        parseObject.saveInBackground();
//        Log.i(TAG, "Unlike has been handled for Title");
//    }
//
//    public void handleUnlikeForUser(ParseObject parseObject) throws JSONException {
//        List<Integer> likedTitles = new ArrayList<>();
//        ParseUser currentUser = ParseUser.getCurrentUser();
//        JSONArray jsonArray = currentUser.getJSONArray(KEY_TITLE_LIKES);
//
//        if (jsonArray != null) {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                Integer titleTmdbID = jsonArray.getInt(i);
//                if (titleTmdbID != parseObject.getInt(Title.KEY_TMDB_ID)) {
//                    likedTitles.add(titleTmdbID);
//                }
//            }
//        }
//
//        currentUser.put(KEY_TITLE_LIKES, likedTitles);
//        currentUser.saveInBackground();
//        Log.i(TAG, "Unlike has been handled for User");
//    }

    // Getter methods for each value
    public String getFullname() {
        return getString(KEY_FULLNAME);
    }

    public String getUsername() {return getString(KEY_USERNAME);}

    public Date getCreatedAt() {return getDate(KEY_CREATED_AT);}

    public String getEmail() {return getString(KEY_EMAIL);}

    public String getKeyCommentLikes() {
        return getString(KEY_COMMENT_LIKES);
    }

    public String getKeyFollowing() {
        return getString(KEY_FOLLOWING);
    }

    public String getKeyChats() {
        return getString(KEY_CHATS);
    }

    public String getKeyCommunities() {
        return getString(KEY_COMMUNITIES);
    }

    public String getTime() {
        Date createdAt = getCreatedAt();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(createdAt);
        return strDate;
    }
}