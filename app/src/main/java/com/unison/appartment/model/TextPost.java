package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post testuale nella bacheca
 */
public class TextPost extends Post {

    private String message;

    public TextPost(String sender, Date date, String message) {
        super(sender, date);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getType() {
        return Post.TEXT_POST;
    }
}
