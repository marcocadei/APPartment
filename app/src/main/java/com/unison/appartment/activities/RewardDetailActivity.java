package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.unison.appartment.R;
import com.unison.appartment.model.Reward;

import java.util.Locale;

/**
 * Classe che rappresenta l'Activity con il dettaglio del Reward
 */
public class RewardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar;
        toolbar = findViewById(R.id.activity_reward_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        Impostazione del contenuto delle TextView in base al premio per il quale
        è costruita l'activity.
         */
        Intent creationIntent = getIntent();
        Resources res = getResources();
        Reward reward = (Reward) creationIntent.getSerializableExtra("rewardObject");
        TextView textName = findViewById(R.id.activity_reward_detail_text_name);
        textName.setText(res.getString(R.string.activity_reward_detail_text_name, reward.getName()));
        TextView textDescription = findViewById(R.id.activity_reward_detail_text_description_value);
        // Viene gestito il caso in cui la descrizione sia vuota
        String shownDescription = reward.getDescription();
        if (shownDescription == null || shownDescription.isEmpty()) {
            shownDescription = res.getString(R.string.general_no_description_available);
            textDescription.setTypeface(null, Typeface.ITALIC);
        }
        textDescription.setText(shownDescription);
        TextView textPoints = findViewById(R.id.activity_reward_detail_text_points_value);
        textPoints.setText(String.format(Locale.getDefault(), "%d", reward.getPoints()));
        toolbar.setTitle(res.getString(R.string.activity_reward_detail_title));

        /*
        Il bottone per la richiesta del premio è disabilitato se il premio è già
        stato richiesto.
         */
        MaterialButton btnReserve = findViewById(R.id.activity_reward_detail_btn_reserve);
        if (reward.isRequested()) {
            btnReserve.setText(res.getString(R.string.activity_reward_detail_btn_reserve_disabled));
            btnReserve.setEnabled(false);
        }

        // FIXME togliere il random e metterci dietro una logica
        // (ora è messo così per poter vedere entrambe le schermate viste dai master e dagli slave)
        if (Math.random() > 0.5) {
            btnReserve.setEnabled(false);
            MaterialButton btnDelete = findViewById(R.id.activity_reward_detail_btn_delete);
            btnDelete.setVisibility(View.GONE);
        }
    }
}
