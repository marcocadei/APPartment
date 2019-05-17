package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.Appartment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.UserHome;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe che rappresenta l'Activity per creare una nuova casa
 */
public class CreateHomeActivity extends FormActivity {

    private static final int MIN_HOME_PASSWORD_LENGTH = 6;

    EditText inputHomeName;
    EditText inputPassword;
    EditText inputRepeatPassword;
    EditText inputNickname;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;
    TextInputLayout layoutNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);

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
        inputPassword = findViewById(R.id.activity_create_home_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password_value);
        inputNickname = findViewById(R.id.activity_create_home_input_repeat_nickname_value);
        layoutHomeName = findViewById(R.id.activity_create_home_input_homename);
        layoutPassword = findViewById(R.id.activity_create_home_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password);
        layoutNickname = findViewById(R.id.activity_create_home_input_nickname);

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

        // Gestione click sul bottone per completare l'inserimento
        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(CreateHomeActivity.this);
                if (checkInput()) {
                    // Se i controlli locali vanno a buon fine controllo che la casa esista
                    checkHome(inputHomeName.getText().toString(), inputPassword.getText().toString());
                }
            }
        });
    }

    protected boolean checkInput() {
        resetErrorMessage(layoutHomeName);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);
        resetErrorMessage(layoutNickname);

        inputHomeName.setText(inputHomeName.getText().toString().trim());
        inputNickname.setText(inputNickname.getText().toString().trim());

        String homeNameValue = inputHomeName.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();
        String nicknameValue = inputNickname.getText().toString();

        boolean result = true;

        // Controllo che i campi "nome casa" e "nickname" non siano stati lasciati vuoti
        if (homeNameValue.trim().length() == 0) {
            layoutHomeName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (nicknameValue.trim().length() == 0) {
            layoutNickname.setError(getString(R.string.form_error_missing_value));
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

        return new Home(homeName, password);
    }

    private HomeUser createHomeUser() {
        // Precondizione: Tutti i campi della form sono corretti

        String nickname = inputNickname.getText().toString();

        return new HomeUser(nickname, Home.ROLE_OWNER);
    }

    private UserHome createUserHome() {
        // Precondizione: Tutti i campi della form sono corretti

        String homeName = inputHomeName.getText().toString();

        return new UserHome(homeName, Home.ROLE_OWNER);
    }

    private void checkHome(final String homeName, final String password) {
        progressDialog = FirebaseProgressDialogFragment.newInstance(
                getString(R.string.activity_create_home_progress_title),
                getString(R.string.activity_create_home_progress_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Esiste già una casa con il nome specificato dall'utente
                    layoutHomeName.setError(getString(R.string.form_error_duplicate_homename));
                    dismissProgress();
                }
                else {
                    writeHomeInDb(homeName);
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

    /**
     * Effettua la scrittura nel database di diversi nodi in modo da creare i dati corrispondenti
     * ad una nuova casa.
     * @param homeName Nome della nuova casa (utilizzato per determinare il path dei nodi in cui
     *                 scrivere).
     */
    private void writeHomeInDb(final String homeName) {
        String separator = getString(R.string.db_separator);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String homePath = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        final String homeuserPath = getString(R.string.db_homeusers) + separator + getString(R.string.db_homeusers_homename, homeName) + separator + getString(R.string.db_homeusers_homename_uid, uid);
        final String userhomePath = getString(R.string.db_userhomes) + separator + getString(R.string.db_userhomes_uid, uid) + separator + getString(R.string.db_userhomes_uid_homename, homeName);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(homePath, createHome());
        childUpdates.put(homeuserPath, createHomeUser());
        childUpdates.put(userhomePath, createUserHome());

        dbRef.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Appartment.getInstance().setHome(homeName);
                            moveToNextActivity(MainActivity.class);
                            dismissProgress();
                        }
                        else {
                            try {
                                throw task.getException();
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
                    }
                });
    }

    @Override
    protected void moveToNextActivity(Class destination) {
        Intent i = new Intent(CreateHomeActivity.this, destination);
        // Passo il nome della casa all'activity successiva
        i.putExtra(MainActivity.EXTRA_HOME_NAME, inputHomeName.getText().toString());
        startActivity(i);
        finish();
    }

}
