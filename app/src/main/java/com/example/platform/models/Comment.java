package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.Date;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String TAG = "Comment";

    public static final String KEY_USER = "user";
    public static final String KEY_TIMESTAMP = "createdAt";
    public static final String KEY_TEXT = "text";
    public static final String KEY_TMDB_ID = "tmdbId";
    public static final String KEY_IN_REPLY_TO = "repliedCommentId";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_REPLIES = "replies";

    public Comment() {}

    // Timestamp
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

    // Getters and setters for each value
    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(String user) {
        put(KEY_USER, user);
    }

    public String getTimestamp() {
        return calculateTimeAgo(getCreatedAt());
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public Integer getTmdbId() {
        return getInt(KEY_TMDB_ID);
    }

    public void setTmdbId(Integer tmdbId) {
        put(KEY_TMDB_ID, tmdbId);
    }

    public String getInReplyTo() {
        return getString(KEY_IN_REPLY_TO);
    }

    public void setInReplyTo(String comment) {
        put(KEY_IN_REPLY_TO, comment);
    }

    public Integer getLikes() {
        return getInt(KEY_LIKES);
    }

    public void setLikes(Integer likes) {
        put(KEY_LIKES, likes);
    }

    public Integer getReplies() {
        return getInt(KEY_REPLIES);
    }

    public void setReplies(Integer replies) {
        put(KEY_REPLIES, replies);
    }
}
