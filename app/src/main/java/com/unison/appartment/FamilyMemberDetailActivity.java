package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.User;

public class FamilyMemberDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);
        // Supporto per la toolbar
        toolbar = findViewById(R.id.activity_family_member_detail_toolbar);
        setSupportActionBar(toolbar);
        // Gestione del click della freccia indietro presente sulla toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Recupero il riferimento agli elementi dell'interfaccia
        TextView name = findViewById(R.id.activity_family_member_detail_name);
        TextView points = findViewById(R.id.activity_family_member_detail_points_value);
        ImageView image = findViewById(R.id.activity_family_member_detail_image);

        Intent i = getIntent();
        User user = (User) i.getSerializableExtra("user");
        // Popolo l'interfaccia con i dati del task ricevuto
        // TODO risistemare con Member e non User
        name.setText("paolo");
        points.setText("444");
//        name.setText(user.getName());
//        points.setText(String.valueOf(user.getPoints()));
        /*image.setImage(user.getDescription());*/
    }
}