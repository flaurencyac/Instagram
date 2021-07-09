package com.example.instagram.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Like")
public class Like extends ParseObject {
    public static final String KEY_LIKER = "user";
    public static final String KEY_POST = "post";

    public void setLikeParentPost(ParseObject post) {put(KEY_POST, post); }

    public void setLiker(ParseUser user) { put(KEY_LIKER, user); }

    public static void destroyLike(Post post) {
        ParseQuery<Like> query = new ParseQuery<Like>(Like.class);
        query.include(KEY_LIKER);
        query.include(KEY_POST);
        query.whereEqualTo(KEY_POST, post);
        query.whereEqualTo(KEY_LIKER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException ex) {
                if (ex != null) {
                    Log.e("Like class", "Failed to find like", ex);
                }
                // destroy like object
                ParseObject like = objects.get(0);
                like.deleteInBackground();
            }
        });
    }

}
