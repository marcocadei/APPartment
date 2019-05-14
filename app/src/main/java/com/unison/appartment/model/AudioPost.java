package com.unison.appartment.model;

import java.util.Date;

/**
 * Classe che rappresenta un post audio nella bacheca
 */
public class AudioPost extends Post {

    public static final int PERMISSION_REQUEST_RECORDER = 1;

    private String fileName;

    public AudioPost(String sender, Date date, String fileName) {
        super(sender, date);
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int getType() {
        return Post.AUDIO_POST;
    }
}
