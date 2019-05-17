package com.unison.appartment.activities;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.Appartment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.User;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;

/**
 * Classe che rappresenta l'Activity per effettuare l'accesso all'applicazione
 */
public class SignInActivity extends FormActivity {

    EditText inputEmail;
    EditText inputPassword;
    TextInputLayout layoutEmail;
    TextInputLayout layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Precondizione: Se viene acceduta questa activity, vuol dire che non c'è nessun utente loggato

        inputEmail = findViewById(R.id.activity_signin_input_email_value);
        inputPassword = findViewById(R.id.activity_signin_input_password_value);
        layoutEmail = findViewById(R.id.activity_signin_input_email);
        layoutPassword = findViewById(R.id.activity_signin_input_password);

        /*
        I listener sono duplicati anche se fanno la stessa cosa poiché la view che prende il focus
        è il textInput mentre la view su cui bisogna operare per rimuovere il messaggio di errore
        è il textLayout, e per qualche motivo invocando getParent sul textInput NON viene restituito
        il textLayout.
         */
        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutPassword);
            }
        });
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutEmail);
            }
        });

        // Gestione click sul bottone per effettuare l'accesso
        FloatingActionButton floatNext = findViewById(R.id.activity_signin_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(SignInActivity.this);
                if (checkInput()) {
                    performSignIn(inputEmail.getText().toString(), inputPassword.getText().toString());
                }
            }
        });
    }

    protected boolean checkInput() {
        resetErrorMessage(layoutEmail);
        resetErrorMessage(layoutPassword);

        String emailValue = inputEmail.getText().toString();
        String passwordValue = inputPassword.getText().toString();

        boolean result = true;

        // Controllo che la mail inserita sia valida
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            layoutEmail.setError(getString(R.string.form_error_incorrect_email));
            result = false;
        }

        // Controllo che tutti i campi siano compilati
        if (emailValue.trim().length() == 0) {
            /*
            Bisogna prima resettare l'errore eventualmente impostato nell'if precedente, altrimenti
            (per qualche motivo) il messaggio non è visualizzato correttamente.
             */
            layoutEmail.setError(null);
            layoutEmail.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (passwordValue.trim().length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    private void performSignIn(final String email, final String password) {
        progressDialog = FirebaseProgressDialogFragment.newInstance(
                getString(R.string.activity_signin_progress_title),
                getString(R.string.activity_signin_progress_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            retrieveUser(uid);
                            moveToNextActivity(UserProfileActivity.class);
                            dismissProgress();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Password sbagliata
                                layoutPassword.setError(getString(R.string.form_error_incorrect_password));
                            } catch (FirebaseAuthInvalidUserException e) {
                                // Utente non esistente
                                layoutEmail.setError(getString(R.string.form_error_nonexistent_email));
                            } catch (Exception e) {
                                // Generico
                                showErrorDialog();
                            }
                            dismissProgress();
                        }
                    }
                });
    }

    private void retrieveUser(String uid) {
        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_users) + separator + getString(R.string.db_users_uid, uid);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appartment.getInstance().setUser(dataSnapshot.getValue(User.class));
                }
                else {
                    // TODO Gestire l'errore
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*
                onCancelled viene invocato solo se si verifica un errore a lato server oppure se
                le regole di sicurezza impostate in Firebase non permettono l'operazione richiesta.
                In questo caso perciò viene visualizzato un messaggio di errore generico, dato che
                la situazione non può essere risolta dall'utente.
                 */
                showErrorDialog();
            }
        });
    }

}
