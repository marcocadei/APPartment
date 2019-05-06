package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class CreateHomeActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;

    EditText inputHomeName;
    EditText inputPassword;
    EditText inputRepeatPassword;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);

        inputHomeName = findViewById(R.id.activity_create_home_input_homename_value);
        inputPassword = findViewById(R.id.activity_create_home_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password_value);
        layoutHomeName = findViewById(R.id.activity_create_home_input_homename);
        layoutPassword = findViewById(R.id.activity_create_home_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password);

        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    Intent i = new Intent(CreateHomeActivity.this, CreateMemberActivity.class);
                    // Passo i parametri della casa all'activity successiva
                    i.putExtra("homeName", inputHomeName.getText().toString());
                    i.putExtra("homePassword", inputPassword.getText().toString());
                    i.putExtra("origin", "fromEnter");
                    startActivity(i);
                }
            }
        });
    }

    private boolean checkInput() {
        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();

        boolean result = true;
        // Controllo che tutti i campi siano compilati
        if (homeNameValue.length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutHomeName.setError(null);
            layoutHomeName.setErrorEnabled(false);
        }
        if (passwordValue.length() < MIN_PASSWORD_LENGTH) {
            layoutPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_PASSWORD_LENGTH));
            result = false;
        } else {
            layoutPassword.setError(null);
            layoutPassword.setErrorEnabled(false);
        }
        if (repeatPasswordValue.length() < MIN_PASSWORD_LENGTH) {
            layoutRepeatPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_PASSWORD_LENGTH));
            result = false;
        } else {
            layoutRepeatPassword.setError(null);
            layoutRepeatPassword.setErrorEnabled(false);
        }
        // Controllo che le password coincidano
        if (passwordValue.length() > MIN_PASSWORD_LENGTH && repeatPasswordValue.length() > MIN_PASSWORD_LENGTH) {
            if (!passwordValue.equals(repeatPasswordValue)) {
                layoutPassword.setError(getString(R.string.form_error_mismatch_password));
                layoutRepeatPassword.setError(getString(R.string.form_error_mismatch_password));
                result = false;
            } else {
                layoutPassword.setError(null);
                layoutPassword.setErrorEnabled(false);
                layoutRepeatPassword.setError(null);
                layoutRepeatPassword.setErrorEnabled(false);
            }
        }

        return result;
    }
}
