package com.unison.appartment.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.database.Auth;
import com.unison.appartment.database.AuthListener;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.User;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;


/**
 * Classe che rappresenta l'Activity per effettuare l'accesso all'applicazione
 */
public class SignInActivity extends ActivityWithDialogs implements FormActivity {

    private Auth auth;
    private DatabaseReader databaseReader;

    private EditText inputEmail;
    private EditText inputPassword;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = new FirebaseAuth();
        databaseReader = new FirebaseDatabaseReader();

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
                    progressDialog = FirebaseProgressDialogFragment.newInstance(
                            getString(R.string.activity_signin_progress_title),
                            getString(R.string.activity_signin_progress_description));
                    progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
                    auth.signIn(inputEmail.getText().toString(), inputPassword.getText().toString(), authListener);
                }
            }
        });
    }

    public boolean checkInput() {
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

    // Listener processo di lettura dal database del nuovo utente
    final DatabaseReaderListener databaseReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(Object object) {
            Appartment.getInstance().setUser((User) object);
            moveToNextActivity(UserProfileActivity.class);
            dismissProgress();
        }

        @Override
        public void onReadEmpty() {
            // TODO gestire l'errore
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            /*
            onCancelled viene invocato solo se si verifica un errore a lato server oppure se
            le regole di sicurezza impostate in Firebase non permettono l'operazione richiesta.
            In questo caso perciò viene visualizzato un messaggio di errore generico, dato che
            la situazione non può essere risolta dall'utente.
            */
            showErrorDialog();
        }
    };

    // Listener processo di autenticazione
    final AuthListener authListener = new AuthListener() {
        @Override
        public void onSuccess() {
            databaseReader.retrieveUser(auth.getCurrentUserUid(), databaseReaderListener);
        }

        @Override
        public void onFail(Exception exception) {
            try {
                throw exception;
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
    };
}
