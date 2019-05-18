package com.unison.appartment.activities;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.DatabaseWriter;
import com.unison.appartment.database.DatabaseWriterListener;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.database.FirebaseDatabaseWriter;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;
import com.unison.appartment.model.UserHome;


/**
 * Classe che rappresenta l'Activity per unirsi ad una nuova casa
 */
public class JoinHomeActivity extends FormActivity {

    private DatabaseReader databaseReader;
    private DatabaseWriter databaseWriter;

    private EditText inputHomeName;
    private EditText inputPassword;
    private EditText inputNickname;
    private TextInputLayout layoutHomeName;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_home);

        databaseReader = new FirebaseDatabaseReader();
        databaseWriter = new FirebaseDatabaseWriter();

        // Supporto per la toolbar
        Toolbar toolbar = findViewById(R.id.activity_join_home_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
        this.errorDialogDestinationActivity = UserProfileActivity.class;

        inputHomeName = findViewById(R.id.activity_join_home_input_homename_value);
        inputPassword = findViewById(R.id.activity_join_home_input_password_value);
        inputNickname = findViewById(R.id.activity_join_home_input_nickname_value);
        layoutHomeName = findViewById(R.id.activity_join_home_input_homename);
        layoutPassword = findViewById(R.id.activity_join_home_input_password);
        layoutNickname = findViewById(R.id.activity_join_home_input_nickname);

        inputNickname.setText(Appartment.getInstance().getUser().getName());

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
        inputNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutNickname);
            }
        });

        // Gestione click sul bottone per effettuare l'unione
        FloatingActionButton floatNext = findViewById(R.id.activity_join_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(JoinHomeActivity.this);
                if (checkInput()) {
                    progressDialog = FirebaseProgressDialogFragment.newInstance(
                            getString(R.string.activity_join_home_progress_check_title),
                            getString(R.string.activity_join_home_progress_check_description));
                    progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
                    databaseReader.retrieveHomePassword(inputHomeName.getText().toString(), databaseReaderListener);
                }
            }
        });
    }

    protected boolean checkInput() {
        resetErrorMessage(layoutHomeName);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutNickname);

        inputNickname.setText(inputNickname.getText().toString().trim());

        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String nicknameValue = inputNickname.getText().toString();

        boolean result = true;

        // Si controlla unicamente che i campi non siano stati lasciati vuoti
        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (passwordValue.trim().length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (nicknameValue.trim().length() == 0) {
            layoutNickname.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    private HomeUser createHomeUser() {
        // Precondizione: Tutti i campi della form sono corretti

        String nickname = inputNickname.getText().toString();

        return new HomeUser(nickname, Home.ROLE_SLAVE);
    }

    private UserHome createUserHome() {
        // Precondizione: Tutti i campi della form sono corretti

        String homeName = inputHomeName.getText().toString();

        return new UserHome(homeName, Home.ROLE_SLAVE);
    }

    @Override
    protected void moveToNextActivity(Class destination) {
        Intent i = new Intent(JoinHomeActivity.this, destination);
        // Passo il nome della casa all'activity successiva
        i.putExtra(MainActivity.EXTRA_HOME_NAME, inputHomeName.getText().toString());
        startActivity(i);
        finish();
    }

    // Listener processo di scrittura nel database dei record necessari per registrare l'unione ad una casa
    final DatabaseWriterListener databaseWriterListener = new DatabaseWriterListener() {
        @Override
        public void onWriteSuccess() {
            Appartment.getInstance().setHome(inputHomeName.getText().toString());
            moveToNextActivity(MainActivity.class);
            dismissProgress();
        }

        @Override
        public void onWriteFail(Exception exception) {
            try {
                throw exception;
            }
            catch (DatabaseException e) {
                int errorCode = DatabaseError.fromException(e).getCode();
                if (errorCode == DatabaseError.PERMISSION_DENIED || errorCode == DatabaseError.USER_CODE_EXCEPTION) {
                    // Regole di sicurezza violate
                    // Implica: L'utente ha specificato una casa di cui è già membro
                    layoutHomeName.setError(getString(R.string.form_error_home_already_joined));
                    dismissProgress();
                }
                else {
                    // Altro errore generico
                    showErrorDialog();
                }
            }
            catch (Exception e) {
                // Generico
                showErrorDialog();
            }
            dismissProgress();
        }
    };

    // Listener processo di lettura nel database della casa in cui si vuole entrare
    final DatabaseReaderListener databaseReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(Object object) {
            String insertedPassword = inputPassword.getText().toString();
            String homePassword = (String)object;
            if (!insertedPassword.equals(homePassword)) {
                // La password inserita è sbagliata (viene comunque mostrato un messaggio d'errore generico)
                layoutHomeName.setError(getString(R.string.form_error_incorrect_credentials));
                layoutPassword.setError(getString(R.string.form_error_incorrect_credentials));
                dismissProgress();
            }
            else {
                // Credenziali corrette, posso passare alla scrittura dei nuovi dati nel db
                databaseWriter.writeJoinHome(inputHomeName.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        createHomeUser(), createUserHome(), databaseWriterListener);
            }
        }

        @Override
        public void onReadEmpty() {
            // La casa specificata non esiste (viene comunque mostrato un messaggio d'errore generico)
            layoutHomeName.setError(getString(R.string.form_error_incorrect_credentials));
            layoutPassword.setError(getString(R.string.form_error_incorrect_credentials));
            dismissProgress();
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
}
