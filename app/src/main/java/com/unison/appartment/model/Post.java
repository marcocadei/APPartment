package com.unison.appartment.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Post {
    public static final int TEXT_POST = 0;
    public static final int IMAGE_POST = 1;
    public static final int AUDIO_POST = 2;

    private static List<Post> postList = new ArrayList<>();

    private String sender;

    public static void addPost(int position, Post post) {
        postList.add(position, post);
    }

    public static void removePost(int position) {
        postList.remove(position);
    }

    public static Post getPost(int position) {
        return postList.get(position);
    }

    public static List<Post> getPostList() {
        return postList;
    }

    public abstract int getType();

    public Post(String sender) {
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public void setSendet(String sender) {
        this.sender = sender;
    }
}
