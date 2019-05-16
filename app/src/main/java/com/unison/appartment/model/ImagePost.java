package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post con immagine nella bacheca
 */
public class ImagePost extends Post {

    public ImagePost(String filename, String author, long timestamp) {
        super(Post.IMAGE_POST, filename, author, timestamp);
    }
}
