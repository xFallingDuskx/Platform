package com.example.platform.models;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {

    public static final String TAG = "Message";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_TEXT = "text";
    public static final String KEY_SENT_DATE = "sentDate";
    public static final String KEY_TYPE_SENT = "sent";
    public static final String KEY_TYPE_RECEIVED = "received";

    String sender;
    String type;
    String text;
    Date sentDate;
    String sentDateString;

    public Message(String sender, String type, String text, Date sentDate) {
        this.sender = sender;
        this.type = type;
        this.text = text;
        this.sentDate = sentDate;
    }

    public Message(String sender, String type, String text, String sentDateString) {
        this.sender = sender;
        this.type = type;
        this.text = text;
        this.sentDateString = sentDateString;
    }

    // Message Json object to be sent through PubNub
    public static JsonObject createMessageObject(String sender, String text, Date sentDate) {
        JsonObject messagePayload = new JsonObject();
        messagePayload.addProperty(KEY_SENDER, sender);
        messagePayload.addProperty(KEY_TEXT, text);
        messagePayload.addProperty(KEY_SENT_DATE, sentDate.toString());
        return messagePayload;
    }

    // Revert the Data.toString() method
    // Source: https://stackoverflow.com/questions/11239814/parsing-a-java-date-back-from-tostring
    public static Date revertDateToString(String dateStringFormatted) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM HH:mm:ss z yyyy");
        return sdf.parse(dateStringFormatted);
    }

    // Get the local date time of a message using its timetoken
    public static String getLocalDateTime(long timetoken) {
        timetoken = timetoken / 10_000L;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetoken);
        String localDateTime = sdf.format(calendar.getTimeInMillis());
        return localDateTime;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Date getSentDate() {
        return sentDate;
    }
}
