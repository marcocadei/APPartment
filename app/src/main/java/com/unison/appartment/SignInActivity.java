package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

        FloatingActionButton floatNext = findViewById(R.id.activity_sign_in_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    signIn(inputHomeName.getText().toString(), inputUsername.getText().toString(), inputPassword.getText().toString());
                }
            }
        });

    }

    private void signIn(final String homeName, final String username, final String password) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_signin_progress_title),
                getString(R.string.activity_signin_progress_description), true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Prima di tutto devo recuperare l'indirizzo email dell'utente
        database.child("users").child(homeName + "-" + username).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.getValue(String.class);
                    performSignIn(email, password, progress);
                    // Rimuovo gli errori se li avevo impostati in precedenza
                    layoutHomeName.setError(null);
                    layoutHomeName.setErrorEnabled(false);
                    layoutUsername.setError(null);
                    layoutUsername.setErrorEnabled(false);
                } else {
                    // Se fallisco qui so che è il nome della casa o lo layoutUsername ad essere errato
                    layoutHomeName.setError(getString(R.string.form_error_wrong_home_username));
                    layoutUsername.setError(getString(R.string.form_error_wrong_home_username));
                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                            // Rimuovo l'errore se prima lo avevo impostato
                            layoutPassword.setError(null);
                            layoutPassword.setErrorEnabled(false);
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
        if (homeNameValue.length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutHomeName.setError(null);
            layoutHomeName.setErrorEnabled(false);
        }
        if (usernameValue.length() == 0) {
            layoutUsername.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutUsername.setError(null);
            layoutUsername.setErrorEnabled(false);
        }
        if (passwordValue.length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutPassword.setError(null);
            layoutPassword.setErrorEnabled(false);
        }

        return result;
    }

    private void moveToNextActivity() {
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(i);
    }
}
