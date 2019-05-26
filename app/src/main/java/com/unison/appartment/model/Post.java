package com.unison.appartment.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta un post della bacheca
 * Un post può essere del testo, un'immagine o un audio
 */
public class Post {
    public static final int TEXT_POST = 0;
    public static final int IMAGE_POST = 1;
    public static final int AUDIO_POST = 2;

    @Exclude
    private String id;
    private String storagePath; // Percorso per rimuovere il file dallo storage quando si elimina un post
    private int type;
    private String content;
    private String author;
    private long timestamp;

    public Post() {}

    public Post(int type, String content, String author, long timestamp) {
        this.type = type;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

    public Post(String storagePath, int type, String content, String author, long timestamp) {
        this.storagePath = storagePath;
        this.type = type;
        this.content = content;
        this.author = author;
        this.timestamp = timestamp;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return type == post.type &&
                timestamp == post.timestamp &&
                content.equals(post.content) &&
                author.equals(post.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content, author, timestamp);
    }
}
