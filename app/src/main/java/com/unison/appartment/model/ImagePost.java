package com.unison.appartment.model;

import android.net.Uri;

public class ImagePost extends Post {

    private Uri image;

    public ImagePost(String sender, Uri image) {
        super(sender);
        this.image = image;
    }

    public Uri getImage() {
        return this.image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    @Override
    public int getType() {
        return Post.IMAGE_POST;
    }
}
