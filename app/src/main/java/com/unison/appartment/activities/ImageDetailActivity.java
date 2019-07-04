package com.unison.appartment.activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;

import com.appeaser.imagetransitionlibrary.ImageTransitionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.unison.appartment.R;
import com.unison.appartment.utils.ImageUtils;

/**
 * Classe che rappresenta l'Activity con il dettaglio dell'immagine
 */
public class ImageDetailActivity extends ActivityWithNetworkConnectionDialog {

    public final static String EXTRA_IMAGE_URI = "imageUri";
    public final static String EXTRA_IMAGE_TYPE = "imageType";

    private String imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent i = getIntent();
        String imageUri = i.getStringExtra(EXTRA_IMAGE_URI);
        imageType = i.getStringExtra(EXTRA_IMAGE_TYPE);

        ImageView image;

        // Riga necessaria per gestire l'animazione nel caso si apra un'immagine tonda
        if (imageType.equals(ImageUtils.IMAGE_TYPE_ROUND)) {
            setEnterSharedElementCallback(ImageTransitionUtil.DEFAULT_SHARED_ELEMENT_CALLBACK);
            image = findViewById(R.id.activity_image_detail_img_round);
            // Animazione apertura immagine tonda
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(ImageDetailActivity.this).inflateTransition(R.transition.itl_image_transition));
            getWindow().setSharedElementExitTransition(TransitionInflater.from(ImageDetailActivity.this).inflateTransition(R.transition.itl_image_transition));
        } else {
            image = findViewById(R.id.activity_image_detail_img);
        }
        image.setVisibility(View.VISIBLE);

        // Prima di avviare l'animazione si attende che l'immagine venga caricata
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

    // Metodo necessario per gestire l'animazione nel caso si apra un'immagine tonda
    @Override
    public void onBackPressed() {
        if (imageType.equals(ImageUtils.IMAGE_TYPE_ROUND)) {
            supportFinishAfterTransition();
        }
        super.onBackPressed();
    }
}
