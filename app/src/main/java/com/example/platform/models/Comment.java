package com.example.platform.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

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

    // Get keywords from the title
    // There is a more efficient way of going about this (such as using an API)
    public static HashSet<String> getKeywords(String comment) {
        HashSet<String> keywords = new HashSet<>();
        // Set of words (articles and common/undesired prepositions, verbs) and pronouns to avoid
        HashSet<String> avoid = new HashSet<>(Arrays.asList("the", "a", "an", "above", "across", "against", "along", "among", "around", "at", "by", "from", "in", "into", "near", "of", "on", "to", "toward", "upon", "with", "within", "am", "is", "are", "they", "their", "them", "she", "her", "hers", "him", "he", "his", "i", "you", "we"));

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
            int thirdWordLimit = sentencesWords.length - 2; // after this index, a third word does not exist
            for (int i = 0; i < iterations; i++) {
                String[] wordCluster = Arrays.copyOfRange(sentencesWords, i, i + 2);
                // Check if first word is an article or preposition by checking if it exist in the set of words to avoid
                // If the first word is an article or preposition, continue on to the next cluster
                String firstWord = wordCluster[0].toLowerCase();
                if (avoid.contains(firstWord)) {
                    continue;
                }

                // Run the same check on the second word to see if it is an article or preposition
                // If the second word is an article or preposition, attempt to add the following word to the cluster
                String secondWord = wordCluster[1].toLowerCase();
                if (avoid.contains(secondWord)) {
                    // Only add a 3rd to the cluster (making it a 3-word cluster) if it exist
                    // Will not exist if we are at the final cluster of the sentence
                    // If this word does not exist, do not add this word cluster to the set of keywords
                    if (! (i == (thirdWordLimit))) {
                        wordCluster = Arrays.copyOfRange(sentencesWords, i, i + 3);

                        // If the third word happens to also be one that should be avoided, continue on
                        if(avoid.contains(wordCluster[2])) {
                            continue;
                        }
                    }
                }

                // Custom String.join() method
                String twoWordClusterString = "";
                for (int j = 0; j < wordCluster.length; j++) {
                    twoWordClusterString += wordCluster[j];
                    if (j != wordCluster.length - 1) {
                        twoWordClusterString += " ";
                    }
                }

                keywords.add(twoWordClusterString);
            }
        }
        return keywords;
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
