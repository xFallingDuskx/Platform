package com.example.platform.models;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;

public class Conversation {

    public static final String TAG = "Conversation";
    public static final String KEY_DATA = "data";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";

    public String id;
    public String name;
    public String description;
    public String timetoken;

    public static String getCurrentTimetoken() {
        long unixTime = System.currentTimeMillis() / 1000L;
        long timetoken = unixTime * 10000000;
        return String.valueOf(timetoken);
    }

    public static String convertToLocalTime(long timetoken) {
        long timetokenInMillis = timetoken / 10_000L;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetokenInMillis);
        String localDateTime = sdf.format(calendar.getTimeInMillis());
        return localDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
