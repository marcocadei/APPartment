package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.Member;
import com.unison.appartment.model.Reward;

import java.util.Locale;

public class FamilyMemberDetailActivity extends AppCompatActivity {

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

         /*
        Impostazione del contenuto delle TextView in base al membro per il quale
        è costruita l'activity.
         */
        Intent creationIntent = getIntent();
        Resources res = getResources();
        Member member = (Member) creationIntent.getSerializableExtra("member");

        TextView textName = findViewById(R.id.activity_family_member_detail_text_name);
        textName.setText(res.getString(R.string.activity_family_member_detail_text_name, member.getName()));

        TextView textPoints = findViewById(R.id.activity_family_member_detail_text_points_value);
        textPoints.setText(String.format(Locale.getDefault(), "%d", member.getPoints()));

        /*ImageView image = findViewById(R.id.activity_family_member_detail_img_profile);
        image.setImage(member.getDescription());*/

        TextView textRole = findViewById(R.id.activity_family_member_detail_text_role_value);
        textRole.setText(res.getString(R.string.activity_family_member_detail_text_role_value, member.getRole()));

        TextView textGender = findViewById(R.id.activity_family_member_detail_text_gender_value);
        textGender.setText(res.getString(R.string.activity_family_member_detail_text_gender_value, member.getGender()));
    }

}