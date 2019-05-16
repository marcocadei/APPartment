package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post audio nella bacheca
 */
public class AudioPost extends Post {

    public static final int PERMISSION_REQUEST_RECORDER = 1;

    public AudioPost(String filename, String author, Date timestamp) {
        super(filename, author, timestamp);
    }

    @Override
    public int getType() {
        return Post.AUDIO_POST;
    }
}
