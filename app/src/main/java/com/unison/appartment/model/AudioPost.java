package com.unison.appartment.model;

public class AudioPost extends Post {

    public static final int PERMISSION_REQUEST_RECORDER = 1;

    @Override
    public int getType() {
        return Post.AUDIO_POST;
    }
}
