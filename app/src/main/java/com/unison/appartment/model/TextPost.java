package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post testuale nella bacheca
 */
public class TextPost extends Post {

    public TextPost(String message, String author, Date timestamp) {
        super(message, author, timestamp);
    }

    @Override
    public int getType() {
        return Post.TEXT_POST;
    }
}
