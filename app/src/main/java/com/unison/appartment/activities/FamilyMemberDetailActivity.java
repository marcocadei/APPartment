package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.unison.appartment.R;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.ImageUtils;

/**
 * Classe che rappresenta l'Activity con il dettaglio di un membro della famiglia
 */
public class FamilyMemberDetailActivity extends AppCompatActivity {

    private HomeUser member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar;
        toolbar = findViewById(R.id.activity_family_member_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent creationIntent = getIntent();
        member = (HomeUser) creationIntent.getSerializableExtra(FamilyFragment.EXTRA_MEMBER_OBJECT);
        
        String[] roles = getResources().getStringArray(R.array.desc_userhomes_uid_homename_role_values);

        // Recupero il riferimento agli elementi dell'interfaccia
        final ImageView image = findViewById(R.id.activity_family_member_detail_img_profile);
        TextView name = findViewById(R.id.activity_family_member_detail_text_name);
        TextView points = findViewById(R.id.activity_family_member_detail_text_points_value);
        TextView role = findViewById(R.id.activity_family_member_detail_text_role_value);
        
        name.setText(member.getNickname());
        points.setText(String.valueOf(member.getPoints()));
        role.setText(roles[member.getRole()]);
        if (member.getImage() != null) {
            image.setColorFilter(getResources().getColor(R.color.transparentWhite, null));
            Glide.with(image.getContext()).load(member.getImage()).apply(RequestOptions.circleCropTransform()).into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FamilyMemberDetailActivity.this, ImageDetailActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            FamilyMemberDetailActivity.this, image, ViewCompat.getTransitionName(image));
                    // Animazione apertura immagine tonda
                    getWindow().setSharedElementEnterTransition(TransitionInflater.from(FamilyMemberDetailActivity.this).inflateTransition(R.transition.itl_image_transition));
                    getWindow().setSharedElementExitTransition(TransitionInflater.from(FamilyMemberDetailActivity.this).inflateTransition(R.transition.itl_image_transition));
                    i.putExtra(ImageDetailActivity.EXTRA_IMAGE_URI, member.getImage());
                    i.putExtra(ImageDetailActivity.EXTRA_IMAGE_TYPE, ImageUtils.IMAGE_TYPE_ROUND);
                    startActivity(i, options.toBundle());
                }
            });
        }
        else {
            image.setColorFilter(getResources().getColor(R.color.colorPrimaryDark, null));
            Glide.with(image.getContext()).load(R.drawable.ic_person).into(image);
        }
    }
}