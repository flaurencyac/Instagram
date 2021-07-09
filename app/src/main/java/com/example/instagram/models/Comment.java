package com.example.instagram.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_COMMENTER = "commenter";
    public static final String KEY_COMMENTARY = "commentary";
    public static final String KEY_POST = "post";

    public String getCommentary() {return getString(KEY_COMMENTARY);}

    public void setCommentary(String commentary) {put(KEY_COMMENTARY, commentary); }

    public ParseUser getCommenter() { return getParseUser(KEY_COMMENTER); }

    public void setCommenter(ParseUser user) { put(KEY_COMMENTER, user); }

    public void setCommentsParentPost(ParseObject post) {put(KEY_POST, post); }

}
