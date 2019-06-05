package com.unison.appartment.activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.unison.appartment.database.Auth;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.DatabaseWriter;
import com.unison.appartment.database.DatabaseWriterListener;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.database.FirebaseDatabaseWriter;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.UserHome;

import java.util.Map;

/**
 * Classe che rappresenta l'Activity per creare una nuova casa
 */
public class CreateHomeActivity extends FormActivity {

    public final static String EXTRA_HOME_DATA = "homeData";

    private static final int MIN_HOME_PASSWORD_LENGTH = 6;

    private Auth auth;
    private DatabaseReader databaseReader;
    private DatabaseWriter databaseWriter;

    private EditText inputHomeName;
    private EditText inputConversionFactor;
    private EditText inputPassword;
    private EditText inputRepeatPassword;
    private EditText inputNickname;
    private TextInputLayout layoutHomeName;
    private TextInputLayout layoutConversionFactor;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutRepeatPassword;
    private TextInputLayout layoutNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);

        auth = new FirebaseAuth();
        databaseReader = new FirebaseDatabaseReader();
        databaseWriter = new FirebaseDatabaseWriter();

        // Supporto per la toolbar
        Toolbar toolbar = findViewById(R.id.activity_create_home_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
        this.errorDialogDestinationActivity = UserProfileActivity.class;

        inputHomeName = findViewById(R.id.activity_create_home_input_homename_value);
        inputConversionFactor = findViewById(R.id.activity_create_home_input_conversion_factor_value);
        inputPassword = findViewById(R.id.activity_create_home_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password_value);
        inputNickname = findViewById(R.id.activity_create_home_input_repeat_nickname_value);
        layoutHomeName = findViewById(R.id.activity_create_home_input_homename);
        layoutConversionFactor = findViewById(R.id.activity_create_home_input_conversion_factor);
        layoutPassword = findViewById(R.id.activity_create_home_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password);
        layoutNickname = findViewById(R.id.activity_create_home_input_nickname);
        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);

        inputNickname.setText(Appartment.getInstance().getUser().getName());
        inputConversionFactor.setText(String.valueOf(Home.DEFAULT_CONVERSION_FACTOR));

        Intent i = getIntent();
        final Home home = (Home) i.getSerializableExtra(EXTRA_HOME_DATA);
        if (home != null) {
            // Imposto il titolo opportunamente se devo modificare e non creare un premio
            toolbar.setTitle(R.string.activity_create_home_text_title_edit);
            TextView textTitle = findViewById(R.id.activity_create_home_text_title);
            textTitle.setText(R.string.activity_create_home_text_title_edit);
            TextView textRoleInfo = findViewById(R.id.activity_create_home_text_role_info);
            textRoleInfo.setVisibility(View.GONE);
            layoutHomeName.setVisibility(View.GONE);
            inputHomeName.setText(home.getName());
            layoutConversionFactor.setVisibility(View.VISIBLE);
            inputConversionFactor.setText(String.valueOf(home.getConversionFactor()));
//            layoutPassword.setVisibility(View.GONE);
//            inputPassword.setText(home.getPassword());
//            layoutRepeatPassword.setVisibility(View.GONE);
//            inputRepeatPassword.setText(home.getPassword());
            layoutNickname.setVisibility(View.GONE);

            // Gestione click sul bottone per completare la modifica
            floatNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardUtils.hideKeyboard(CreateHomeActivity.this);
                    if (checkInput()) {
                        progressDialog = FirebaseProgressDialogFragment.newInstance(
                                getString(R.string.activity_create_home_edit_progress_title),
                                getString(R.string.activity_create_home_edit_progress_description));
                        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
                        // Scrivo i dati aggiornati nel db
                        databaseWriter.writeHome(createHome(), dbHomeEditWriterListener);
                    }
                }
            });
        }
        else {
            // Gestione click sul bottone per completare l'inserimento
            floatNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardUtils.hideKeyboard(CreateHomeActivity.this);
                    if (checkInput()) {
                        progressDialog = FirebaseProgressDialogFragment.newInstance(
                                getString(R.string.activity_create_home_progress_title),
                                getString(R.string.activity_create_home_progress_description));
                        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
                        // Se i controlli locali vanno a buon fine controllo che la casa esista
                        databaseReader.retrieveHome(inputHomeName.getText().toString(), databaseReaderListener);
                    }
                }
            });
        }

        inputHomeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutHomeName);
            }
        });
        inputConversionFactor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutConversionFactor);
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
        inputNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutNickname);
            }
        });
    }

    protected boolean checkInput() {
        resetErrorMessage(layoutHomeName);
        resetErrorMessage(layoutConversionFactor);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);
        resetErrorMessage(layoutNickname);

        inputHomeName.setText(inputHomeName.getText().toString().trim());
        inputNickname.setText(inputNickname.getText().toString().trim());

        String homeNameValue = inputHomeName.getText().toString();
        String conversionFactorValue = inputConversionFactor.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();
        String nicknameValue = inputNickname.getText().toString();

        boolean result = true;

        // Controllo che i campi "nome casa", "nickname" e "conversion factor" non siano stati lasciati vuoti
        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (nicknameValue.trim().length() == 0) {
            layoutNickname.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (conversionFactorValue.trim().length() == 0) {
            layoutConversionFactor.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        // Controllo che il fattore di conversione impostato sia maggiore di 0
        if (Integer.parseInt(conversionFactorValue) <= 0) {
            layoutConversionFactor.setError(null);
            layoutConversionFactor.setError(getString(R.string.form_error_nonzero_required));
            result = false;
        }

        // Controllo che il nome della casa non contenga un carattere non ammesso
        // (Il nome della casa è usato come nome di un nodo in Firebase e lì alcuni caratteri non sono ammessi)
        if (homeNameValue.matches(".*[.$\\[\\]#/].*")) {
            layoutHomeName.setError(getString(R.string.form_error_invalid_characters));
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
            layoutPassword.setError(null);
            layoutPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_HOME_PASSWORD_LENGTH));
            result = false;
        }
        if (repeatPasswordValue.trim().length() < MIN_HOME_PASSWORD_LENGTH) {
            layoutRepeatPassword.setError(null);
            layoutRepeatPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_HOME_PASSWORD_LENGTH));
            result = false;
        }

        return result;
    }

    private Home createHome() {
        // Precondizione: Tutti i campi della form sono corretti

        String homeName = inputHomeName.getText().toString();
        String password = inputPassword.getText().toString();
        int conversionFactor = Integer.parseInt(inputConversionFactor.getText().toString());

        return new Home(homeName, password, conversionFactor);
    }

    private HomeUser createHomeUser() {
        // Precondizione: Tutti i campi della form sono corretti

        String nickname = inputNickname.getText().toString();

        return new HomeUser(nickname, Home.ROLE_OWNER, Appartment.getInstance().getUser().getImage());
    }

    private UserHome createUserHome() {
        // Precondizione: Tutti i campi della form sono corretti

        String homeName = inputHomeName.getText().toString();

        return new UserHome(homeName, Home.ROLE_OWNER);
    }

    // Listener processo di aggiornamento di una casa già esistente
    final DatabaseWriterListener dbHomeEditWriterListener = new DatabaseWriterListener() {
        @Override
        public void onWriteSuccess() {
            Appartment.getInstance().setHome(createHome());
            finish();
            dismissProgress();
        }

        @Override
        public void onWriteFail(Exception exception) {
            try {
                throw exception;
            } catch (Exception e) {
                // (DatabaseException se si verifica una violazione delle regole di sicurezza)
                // Generico
                showErrorDialog();
            }
            dismissProgress();
        }
    };

    // Listener processo di scrittura nel database dei record necessari per registrare la creazione di una casa
    final DatabaseWriterListener databaseWriterListener = new DatabaseWriterListener() {
        @Override
        public void onWriteSuccess() {
            Appartment appState = Appartment.getInstance();
            appState.setHome(createHome());
            appState.setUserHome(createUserHome());
            databaseReader.retrieveHomeUsers(createHome().getName(), auth.getCurrentUserUid(),  dbReaderHomeUserListener);
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
                    // Implica: Esiste già una casa con il nome specificato dall'utente
                    // (Si può verificare solo se tra la lettura precedente e questa
                    // scrittura un altro utente ha registrato una casa con lo stesso nome)
                    layoutHomeName.setError(getString(R.string.form_error_duplicate_homename));
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

    final DatabaseReaderListener dbReaderHomeUserListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(String key, Object object) {
            Appartment.getInstance().setHomeUsers((Map<String, HomeUser>)object);
            moveToNextActivity(MainActivity.class);
            dismissProgress();
        }

        @Override
        public void onReadEmpty() {
            // TODO Se si entra qui c'è un errore perché la casa è selezionata dalla lista e quindi deve esistere
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            // TODO Se si entra qui c'è un errore perché la casa è selezionata dalla lista e quindi deve esistere
        }
    };

    // Listener processo di lettura nel database della casa che si vuole creare
    final DatabaseReaderListener databaseReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(String key, Object object) {
            // Esiste già una casa con il nome specificato dall'utente
            layoutHomeName.setError(getString(R.string.form_error_duplicate_homename));
            dismissProgress();
        }

        @Override
        public void onReadEmpty() {
            databaseWriter.writeCreateHome(inputHomeName.getText().toString(), auth.getCurrentUserUid(),
                    createHome(), createHomeUser(), createUserHome(), databaseWriterListener);
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
