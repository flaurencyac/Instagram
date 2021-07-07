package com.example.instagram;

import org.parceler.Parcel;
import org.w3c.dom.Comment;

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
