package com.unison.appartment.model;

public class AudioPost extends Post {

    public static final int PERMISSION_REQUEST_RECORDER = 1;

    private String fileName;

    public AudioPost(String fileName) {
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
