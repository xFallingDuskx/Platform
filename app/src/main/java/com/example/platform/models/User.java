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

    public static String getTime(Date createdAt) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM YYYY");
        String strDate = dateFormat.format(createdAt);
        String[] strDateSplit = strDate.split("-");
//        String month = strDateSplit[0];
//        String year = strDateSplit[2];
//
////        if(month.equals("01")) {
//            month = "January";
//        } else if (month.equals("02")) {
//            month = "February";
//        } else if (month.equals("03")) {
//            month = "March";
//        } else if ()

        return strDate;
    }
}