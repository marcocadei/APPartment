package com.unison.appartment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe astratta che rappresenta un post della bacheca
 * Un post può essere del testo, un'immagine o un audio
 */
public class Post {
    public static final int TEXT_POST = 0;
    public static final int IMAGE_POST = 1;
    public static final int AUDIO_POST = 2;

    private static List<Post> postList = new ArrayList<>();

    private int type;
    private String content;
    private String author;
    private long timestamp;

    public Post(int type, String content, String author, long timestamp) {
        this.type = type;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
