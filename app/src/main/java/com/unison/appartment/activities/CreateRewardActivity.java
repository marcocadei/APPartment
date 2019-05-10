package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.R;
import com.unison.appartment.model.Reward;

public class CreateRewardActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reward);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar;
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
        inputDescription = findViewById(R.id.activity_create_reward_input_description_value);
        inputPoints = findViewById(R.id.activity_create_reward_input_points_value);

        FloatingActionButton floatConfirm = findViewById(R.id.activity_create_reward_float_confirm);
        floatConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReward();
            }
        });
    }

    public void createReward() {
        Reward reward = new Reward(
                inputName.getText().toString(),
                inputDescription.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString())
        );
        Intent i = new Intent();
        i.putExtra("rewardObject", reward);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
