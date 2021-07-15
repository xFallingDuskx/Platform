package com.example.platform.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("User")
public class User extends ParseObject {
    // TODO: Ensure that your subclass has a public default constructor
    public User() {
    }

    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TITLE_LIKES = "titleLikes";
    public static final String KEY_COMMENT_LIKES = "commentLikes";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_CHATS = "chats";
    public static final String KEY_COMMUNITIES = "communities";

    // Getter methods for each value
    public String getFullname() {
        return getString(KEY_FULLNAME);
    }

    public String getUsername() {return getString(KEY_USERNAME);}

    public Date getCreatedAt() {return getDate(KEY_CREATED_AT);}

    public String getEmail() {return getString(KEY_EMAIL);}

    // Get Titles liked by User based on the Titles' TMDB ID
    public List<Integer> getKeyTitleLikes() throws JSONException {
        List<Integer> titles = new ArrayList<>();
        JSONArray jsonArray = getJSONArray(KEY_TITLE_LIKES);

        for(int i = 0; i < jsonArray.length(); i++) {
            Integer titleTmdbID = jsonArray.getJSONObject(i).getInt(Title.KEY_TMDB_ID);
            titles.add(titleTmdbID);
        }
        return titles;
    }

    // Update the likes for a
    public void addTitleLike(Integer titleTmdbID) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Title");
        query.whereEqualTo(Title.KEY_TMDB_ID, titleTmdbID);
        ParseObject parseObject = query.getFirst();
        Integer currentLikes = parseObject.getInt(Title.KEY_LIKES);
        parseObject.put(Title.KEY_LIKES, currentLikes + 1);
    }

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