package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText inputHomeName;
    EditText inputUsername;
    EditText inputPassword;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutUsername;
    TextInputLayout layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        inputHomeName = findViewById(R.id.activity_signin_input_homename_value);
        inputUsername = findViewById(R.id.activity_signin_input_username_value);
        inputPassword = findViewById(R.id.activity_signin_input_password_value);
        layoutHomeName = findViewById(R.id.activity_signin_input_homename);
        layoutUsername = findViewById(R.id.activity_signin_input_username);
        layoutPassword = findViewById(R.id.activity_signin_input_password);

        /*
        I listener sono duplicati anche se fanno la stessa cosa poiché la view che prende il focus
        è il textInput mentre la view su cui bisogna operare per rimuovere il messaggio di errore
        è il textLayout, e per qualche motivo invocando getParent sul textInput NON viene restituito
        il textLayout.
         */
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
        inputUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutUsername);
            }
        });

        FloatingActionButton floatNext = findViewById(R.id.activity_signin_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(SignInActivity.this);
                if (checkInput()) {
                    signIn(inputHomeName.getText().toString(), inputUsername.getText().toString(), inputPassword.getText().toString());
                }
            }
        });

    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private void signIn(final String homeName, final String username, final String password) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_signin_progress_title),
                getString(R.string.activity_signin_progress_description), true);
        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_users) + separator + getString(R.string.db_users_userid, homeName, username) + separator + getString(R.string.db_users_userid_email);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        // Prima di tutto devo recuperare l'indirizzo email dell'utente
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.getValue(String.class);
                    performSignIn(email, password, progress);
                } else {
                    // Se fallisco qui so che è il nome della casa o lo username ad essere errato
                    layoutHomeName.setError(getString(R.string.form_error_wrong_home_username));
                    layoutUsername.setError(getString(R.string.form_error_wrong_home_username));
                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO aggiungere gestione degli errori
            }
        });
    }

    private void performSignIn(String email, final String password, final ProgressDialog progress) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO rimuovere questa riga di codice
                            auth.signOut();
                            moveToNextActivity();
                        } else {
                            // Se fallisco qui deve essere la layoutPassword sbagliata
                            layoutPassword.setError(getString(R.string.form_error_incorrect_password));
                        }
                        progress.dismiss();
                    }
                });
    }

    private boolean checkInput() {
        String homeNameValue = inputHomeName.getText().toString();
        String usernameValue = inputUsername.getText().toString();
        String passwordValue = inputPassword.getText().toString();

        boolean result = true;
        // Parto controllando che tutti i campi siano compilati
        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            resetErrorMessage(layoutHomeName);
        }
        if (usernameValue.trim().length() == 0) {
            layoutUsername.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            resetErrorMessage(layoutUsername);
        }
        if (passwordValue.trim().length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            resetErrorMessage(layoutPassword);
        }

        return result;
    }

    private void moveToNextActivity() {
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
