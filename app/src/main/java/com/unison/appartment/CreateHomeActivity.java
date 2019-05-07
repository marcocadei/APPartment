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

    public static final String FROM_ENTER = "fromEnter";
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
                    // Se i controlli locali vanno a buon fine controllo che la casa esista
                    checkHouseExists();
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

    private void checkHouseExists() {
        String homeNameValue = inputHomeName.getText().toString();
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_create_home_progress_title),
                getString(R.string.activity_create_home_progress_description), true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("homes").child(homeNameValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    layoutHomeName.setError(getString(R.string.form_error_home_exists));
                } else {
                    layoutHomeName.setError(null);
                    layoutHomeName.setErrorEnabled(false);
                    moveToNextActivity();
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void moveToNextActivity() {
        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        // Passo i parametri della casa all'activity successiva
        Intent i = new Intent(CreateHomeActivity.this, CreateMemberActivity.class);
        i.putExtra("homeName", homeNameValue);
        i.putExtra("homePassword", passwordValue);
        i.putExtra("origin", FROM_ENTER);
        startActivity(i);
    }

}
