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

public class JoinHomeActivity extends AppCompatActivity {

    EditText inputHomeName;
    EditText inputPassword;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_home);

        inputHomeName = findViewById(R.id.activity_join_home_input_homename_value);
        inputPassword = findViewById(R.id.activity_join_home_input_password_value);
        layoutHomeName = findViewById(R.id.activity_join_home_input_homename);
        layoutPassword = findViewById(R.id.activity_join_home_input_password);

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

        FloatingActionButton floatNext = findViewById(R.id.activity_join_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(JoinHomeActivity.this);
                if (checkInput()) {
                    checkCredentials(inputHomeName.getText().toString(), inputPassword.getText().toString());
                }
            }
        });
    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private boolean checkInput() {
        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();

        boolean result = true;

        // Si controlla unicamente che i campi non siano stati lasciati vuoti

        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        else {
            resetErrorMessage(layoutHomeName);
        }

        if (passwordValue.trim().length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        else {
            resetErrorMessage(layoutPassword);
        }

        return result;
    }

    private void checkCredentials(final String homeName, final String password) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_join_home_progress_check_title),
                getString(R.string.activity_join_home_progress_check_description), true);

        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // La casa specificata non esiste (viene comunque mostrato un messaggio d'errore generico)
                    layoutHomeName.setError(getString(R.string.form_error_incorrect_credentials));
                    layoutPassword.setError(getString(R.string.form_error_incorrect_credentials));
                    progress.dismiss();
                }
                else {
                    String homePassword = dataSnapshot.child(getString(R.string.db_homes_homename_password)).getValue(String.class);
                    if (!password.equals(homePassword)) {
                        // La password inserita è sbagliata (viene comunque mostrato un messaggio d'errore generico)
                        layoutHomeName.setError(getString(R.string.form_error_incorrect_credentials));
                        layoutPassword.setError(getString(R.string.form_error_incorrect_credentials));
                        progress.dismiss();
                    }
                    else {
                        // Credenziali corrette, posso passare alla creazione dell'utente
                        Intent i = new Intent(JoinHomeActivity.this, CreateMemberActivity.class);
                        i.putExtra(CreateMemberActivity.EXTRA_SOURCE_ACTIVITY, JoinHomeActivity.class.toString());
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO aggiungere gestione degli errori
            }
        });
    }
}
