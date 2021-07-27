package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    // Get keywords from each comment
    // There is a more efficient way of going about this (such as using an API)
    public static HashSet<String> getKeywords(String comment) {
        HashSet<String> keywords = new HashSet<>();
        // Set of words (articles and common/undesired prepositions, verbs) and pronouns to avoid
        HashSet<String> avoid = new HashSet<>(Arrays.asList("this", "the", "a", "an", "for", "comment", "above", "across", "against", "along", "among", "around", "at", "by", "from", "in", "into", "near", "of", "on", "to", "toward", "upon", "with", "within", "am", "is", "are", "they", "their", "them", "she", "her", "hers", "him", "he", "his", "i", "you", "we"));

        String[] commentSentences = comment.split("[.!?]+\\s*"); // Split comment by end punctuations (.!?) to get sentences
        for (String sentence : commentSentences) {
            sentence = sentence.replaceAll("[#$%()*+,/:;<=>@^|~]+\\s*", ""); // Remove any additional punctuations that may exist
            String[] sentencesWords = sentence.split(" "); // Get all the words from the sentence

            // Add the one-word 'clusters'
            // Check if they should be avoided using the set of words to avoid
            for (String word : sentencesWords) {
                word = word.toLowerCase();
                if (!avoid.contains(word)) {
                    keywords.add(word);
                }
            }

            // Get and add two-word clusters
            int iterations = sentencesWords.length - 1; // always iterate one-less of the total word count for 2-word clusters
            int thirdWordLimit = sentencesWords.length - 2; // at this index, a third word does not exist
            for (int i = 0; i < iterations; i++) {
                String[] wordCluster = Arrays.copyOfRange(sentencesWords, i, i + 2);
                String firstWord = sentencesWords[i];
                String secondWord = sentencesWords[i + 1];
                String thirdWord = ""; // make third word empty by default

                // Check first word - skip this cluster if it fails the test
                if (avoid.contains(firstWord)) {
                    continue;
                }

                // Avoid having word clusters with the same word
                if (firstWord.equals(secondWord)) {
                    continue;
                }

                // Check second word - attempt to make a 3-word cluster if it fails the test
                if (avoid.contains(secondWord)) {
                    // Only add a 3rd to the cluster (making it a 3-word cluster) if it exist
                    // If this word does not exist, do not add this word cluster to the set of keywords
                    if (! (i == (thirdWordLimit))) {
                        thirdWord = sentencesWords[i + 2];

                        // If the third word happens to also be one that should be avoided, continue on to next cluster
                        if(avoid.contains(thirdWord)) {
                            continue;
                        }
                    }
                }

                // Custom String.join() method
                String wordClusterString = firstWord + " " + secondWord;
                if (! thirdWord.isEmpty()) { // If the third word is not empty (meaning it exists)
                    wordClusterString += " " + thirdWord;
                }
                keywords.add(wordClusterString);
            }
        }
        return keywords;
    }

    // Decide which words to display to other users depending on all keywords available
    // Source: https://mkyong.com/java8/java-8-how-to-sort-a-map/
    public static List<String> getWordsToDisplay(HashMap<String, Integer> allKeywords) {
        // Ensure keywords have been mention by more than once
        HashMap<String, Integer> checkedKeywords = new HashMap<>();
        for (Map.Entry<String, Integer> entry : allKeywords.entrySet()) {
            if (entry.getValue() > 1) {
                checkedKeywords.put(entry.getKey(), entry.getValue());
            }
        }

        List<String> chosenKeywords = new ArrayList<>();

        checkedKeywords.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> chosenKeywords.add(x.getKey()));

        return chosenKeywords;
    }

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
