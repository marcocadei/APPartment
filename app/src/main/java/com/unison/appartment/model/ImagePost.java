package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post con immagine nella bacheca
 */
public class ImagePost extends Post {

    public ImagePost(String filename, String author, Date timestamp) {
        super(filename, author, timestamp);
    }

    @Override
    public int getType() {
        return Post.IMAGE_POST;
    }
}
