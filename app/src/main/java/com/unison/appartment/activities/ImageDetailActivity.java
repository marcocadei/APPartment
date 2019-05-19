package com.unison.appartment.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.unison.appartment.R;

/**
 * Classe che rappresenta l'Activity con il dettaglio dell'immagine
 */
public class ImageDetailActivity extends AppCompatActivity {

    public final static String EXTRA_IMAGE_URI = "imageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent i = getIntent();
        Uri imageUri = Uri.parse(i.getStringExtra(EXTRA_IMAGE_URI));
        // Prima di avviare l'animazione si attende che l'immagine venga caricata
        PhotoView image = findViewById(R.id.activity_image_detail_img);
        supportPostponeEnterTransition();
        Glide
                .with(this)
                .load(imageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ImageDetailActivity.this.supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(image);
    }
}
