package com.unison.appartment.model;

public class TextPost extends Post {

    private String message;

    public TextPost(String sender, String message) {
        super(sender);
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
