package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post testuale nella bacheca
 */
public class TextPost extends Post {

    public TextPost(String message, String author, long timestamp) {
        super(Post.TEXT_POST, message, author, timestamp);
    }

}
