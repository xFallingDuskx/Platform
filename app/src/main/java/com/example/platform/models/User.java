package com.example.platform.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String getKeyTitleLikes() {
        return getString(KEY_TITLE_LIKES);
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