package com.example.instagram;

import com.example.instagram.models.Post;

import org.parceler.Parcel;

@Parcel
public class ParcelableObject {
    Post post;
    //Comment comment;
    public ParcelableObject(){}

    public void setPost(Post post){
        this.post = post;
    }
    public Post getPost(){
        return post;
    }
}
