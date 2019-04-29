package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.unison.appartment.model.Reward;

public class CreateRewardActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar;
        setContentView(R.layout.activity_create_reward);
        toolbar = findViewById(R.id.activity_create_reward_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        Creazione di un nuovo premio alla pressione del bottone.
         */
        inputName = findViewById(R.id.activity_create_reward_input_name_value);
        inputPoints = findViewById(R.id.activity_create_reward_input_points_value);

        MaterialButton btnConfirm = findViewById(R.id.activity_create_reward_btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReward();
            }
        });
    }

    public void createReward() {
        Reward reward = new Reward(
                inputName.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString())
        );
        Intent i = new Intent();
        i.putExtra("rewardObject", reward);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
