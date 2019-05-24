package com.unison.appartment.activities;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.model.Reward;
import com.unison.appartment.utils.KeyboardUtils;

/**
 * Classe che rappresenta l'Activity per creare un nuovo Reward
 */
public class CreateRewardActivity extends FormActivity {

    public final static String EXTRA_REWARD_DATA = "rewardData";

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;
    private TextInputLayout layoutName;
    private TextInputLayout layoutDescription;
    private TextInputLayout layoutPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reward);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_create_reward_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
        this.errorDialogDestinationActivity = MainActivity.class;

        inputName = findViewById(R.id.activity_create_reward_input_name_value);
        inputDescription = findViewById(R.id.activity_create_reward_input_description_value);
        inputPoints = findViewById(R.id.activity_create_reward_input_points_value);
        layoutName = findViewById(R.id.activity_create_reward_input_name);
        layoutDescription = findViewById(R.id.activity_create_reward_input_description);
        layoutPoints = findViewById(R.id.activity_create_reward_input_points);

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutName);
            }
        });
        inputDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutDescription);
            }
        });
        inputPoints.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutPoints);
            }
        });

        Intent i = getIntent();
        Reward reward = (Reward) i.getSerializableExtra(EXTRA_REWARD_DATA);
        if (reward != null) {
            inputPoints.setText(String.valueOf(reward.getPoints()));
            inputDescription.setText(reward.getDescription());
            inputName.setText(reward.getName());
        }

        FloatingActionButton floatConfirm = findViewById(R.id.activity_create_reward_float_confirm);
        floatConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(CreateRewardActivity.this);
                if (checkInput()) {
                    createReward();
                }
            }
        });
    }

    @Override
    protected boolean checkInput() {
        resetErrorMessage(layoutName);
        resetErrorMessage(layoutDescription);
        resetErrorMessage(layoutPoints);

        inputName.setText(inputName.getText().toString().trim());

        String nameValue = inputName.getText().toString();
        String descriptionValue = inputDescription.getText().toString();
        String pointsValue = inputPoints.getText().toString();

        boolean result = true;

        // Controllo che i campi non siano stati lasciati vuoti
        if (nameValue.trim().length() == 0) {
            layoutName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (descriptionValue.trim().length() == 0) {
            layoutDescription.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (pointsValue.trim().length() == 0) {
            layoutPoints.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    /**
     * Metodo per creare un nuovo Reward
     */
    public void createReward() {
        Reward reward = new Reward(
                inputName.getText().toString(),
                inputDescription.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString())
        );
        Intent i = new Intent();
        i.putExtra(RewardsFragment.EXTRA_NEW_REWARD, reward);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
