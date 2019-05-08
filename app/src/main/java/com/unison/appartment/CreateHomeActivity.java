package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateHomeActivity extends AppCompatActivity {

    private static final int MIN_HOME_PASSWORD_LENGTH = 6;

    EditText inputHomeName;
    EditText inputPassword;
    EditText inputRepeatPassword;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;

    ProgressDialog progress;

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

        inputHomeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutHomeName);
            }
        });
        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutPassword);
            }
        });
        inputRepeatPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutRepeatPassword);
            }
        });

        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(CreateHomeActivity.this);
                if (checkInput()) {
                    // Se i controlli locali vanno a buon fine controllo che la casa esista
                    checkHouseExists(inputHomeName.getText().toString());
                }
            }
        });
    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private boolean checkInput() {
        resetErrorMessage(layoutHomeName);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);

        inputHomeName.setText(inputHomeName.getText().toString().trim());

        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();

        boolean result = true;

        // Controllo che il campo "nome casa" non sia stato lasciato vuoto
        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        // Controllo che le due password inserite coincidano
        if (!passwordValue.equals(repeatPasswordValue)) {
            layoutPassword.setError(getString(R.string.form_error_mismatch_password));
            layoutRepeatPassword.setError(getString(R.string.form_error_mismatch_password));
            result = false;
        }

        // Controllo che nei campi "password" e "ripeti password" sia stata specificata una
        // password avente il numero minimo di caratteri richiesto
        if (passwordValue.trim().length() < MIN_HOME_PASSWORD_LENGTH) {
            layoutPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_HOME_PASSWORD_LENGTH));
            result = false;
        }
        if (repeatPasswordValue.trim().length() < MIN_HOME_PASSWORD_LENGTH) {
            layoutRepeatPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_HOME_PASSWORD_LENGTH));
            result = false;
        }

        return result;
    }

    private void checkHouseExists(final String homeName) {
        progress = ProgressDialog.show(
                this,
                getString(R.string.activity_create_home_progress_title),
                getString(R.string.activity_create_home_progress_description), true);

        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    layoutHomeName.setError(getString(R.string.form_error_home_exists));
                }
                else {
                    moveToNextActivity();
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO aggiungere gestione degli errori
            }
        });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(CreateHomeActivity.this, CreateMemberActivity.class);
        // Passo nome e password della casa all'activity successiva
        i.putExtra(CreateMemberActivity.EXTRA_HOME_NAME, inputHomeName.getText().toString());
        i.putExtra(CreateMemberActivity.EXTRA_HOME_PASSWORD, inputPassword.getText().toString());
        i.putExtra(Intent.EXTRA_REFERRER_NAME, CreateHomeActivity.class.toString());
        startActivity(i);
        finish();
    }

}
