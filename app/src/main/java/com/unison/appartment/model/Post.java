package com.unison.appartment.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe astratta che rappresenta un post della bacheca
 * Un post può essere del testo, un'immagine o un audio
 */
public abstract class Post {
    public static final int TEXT_POST = 0;
    public static final int IMAGE_POST = 1;
    public static final int AUDIO_POST = 2;

    private static List<Post> postList = new ArrayList<>();

    private String content;
    private String author;
    private Date timestamp;

    public Post(String content, String author, Date timestamp) {
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

    public abstract int getType();

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
