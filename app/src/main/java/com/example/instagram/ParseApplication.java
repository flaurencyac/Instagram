package com.example.instagram;

import android.app.Application;

import com.example.instagram.models.Comment;
import com.example.instagram.models.Like;
import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Like.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("OMzOIzdYwwdPkIkjDBzgSG5c54GhZ4uCzGukAQNK")
                .clientKey("EZQNxeFHw5OQx6y8JoNGhY1J8ru8NJu1XFRnKxZl")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
