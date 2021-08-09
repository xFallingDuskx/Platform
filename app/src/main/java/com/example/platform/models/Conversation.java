package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {

    public static final String TAG = "Conversation";
    public static final String KEY_NAME = "name";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_UPDATED_AT = "updatedAt";

    public String id;
    public String name;
    public String visibleName;
    public String description;
    public String timestamp;
    public Date updatedAtDate;

    // Default, empty constructor
    public Conversation() {}

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

    // Chat names come formatted as "User&User"
    // Convert this to get the chat name to be displayed to user
    public static String convertToVisible(String chatName, String currentUserUsername) {
        String[] usernames = chatName.split("&");
        if (! usernames[0].equals(currentUserUsername)) {
            return usernames[0];
        } else {
            return usernames[1];
        }
    }

    // Timestamp
    public static String calculateTimestamp(Date createdAt) {

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
            } else if ((diff / DAY_MILLIS) < 7) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                return simpleDateFormat.format(createdAt);
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
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
        put(Conversation.KEY_NAME, name);
        this.name = name;
    }

    public String getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONArray getMembers() {
        return getJSONArray(Conversation.KEY_MEMBERS);
    }

    public void setMembers(List<String> members) {
        put(Conversation.KEY_MEMBERS, members);
    }

    public String getLastMessage() {
        return getString(Conversation.KEY_LAST_MESSAGE);
    }

    public void setLastMessage(String lastMessage) {
        put(Conversation.KEY_LAST_MESSAGE, lastMessage);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date updatedAt) {
        this.timestamp = calculateTimestamp(updatedAt);
    }

    public Date getUpdatedAtDate() {
        return updatedAtDate;
    }

    public void setUpdatedAtDate(Date updatedAtDate) {
        this.updatedAtDate = updatedAtDate;
    }
}
