package com.example.platform.models;

import android.util.Log;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Post {

    public static final String TAG = "Post";
    public static final String KEY_USER = "user";
    public static final String KEY_TEXT = "text";
    public static final String KEY_TYPE_SENT = "sent";
    public static final String KEY_TYPE_RECEIVED = "received";

    String user;
    String text;
    String timestamp;

    public Post(String user, String text, String timestamp) {
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Message Json object to be sent through PubNub
    public static JsonObject createPostObject(String user, String text) {
        JsonObject messagePayload = new JsonObject();
        messagePayload.addProperty(KEY_USER, user);
        messagePayload.addProperty(KEY_TEXT, text);
        return messagePayload;
    }

    // Timestamp
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;
        int MONTH_MILLIS = 30 * DAY_MILLIS;
        int YEAR_MILLIS = 365 * DAY_MILLIS;

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
            } else if ((diff / DAY_MILLIS) < 30){
                return diff / DAY_MILLIS + " days ago";
            } else if ((diff / MONTH_MILLIS) < 11) {
                return diff / MONTH_MILLIS + " months ago";
            } else {
                return diff / YEAR_MILLIS + " years ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getLocalDateTime() {
        return timestamp;
    }
}
