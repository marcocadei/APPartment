package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.R;
import com.unison.appartment.database.Auth;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.User;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.KeyboardUtils;

import java.util.Map;

public class UserDeletionActivity extends FormActivity {

    private EditText inputPassword;
    private TextInputLayout layoutPassword;
    private CheckBox checkBox;

    private Auth auth;
    private DatabaseReader databaseReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deletion);
        Toolbar toolbar = findViewById(R.id.activity_user_deletion_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputPassword = findViewById(R.id.activity_user_deletion_input_password_value);
        layoutPassword = findViewById(R.id.activity_user_deletion_input_password);
        checkBox = findViewById(R.id.activity_user_deletion_check);
        final MaterialButton btnDelete = findViewById(R.id.activity_user_deletion_btn_delete);

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutPassword);
            }
        });

        auth = new FirebaseAuth();
        databaseReader = new FirebaseDatabaseReader();


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnDelete.setEnabled(isChecked);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(UserDeletionActivity.this);
                if (checkInput()) {
                    progressDialog = FirebaseProgressDialogFragment.newInstance(
                            getString(R.string.activity_user_deletion_progress_title),
                            getString(R.string.activity_user_deletion_progress_description));
                    progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

                    // Lettura dei riferimenti a task e premi a cui va aggiornato il nickname
                    databaseReader.retrieveUserHomes(auth.getCurrentUserUid(), dbReaderListener);
                }
            }
        });
    }

    @Override
    protected boolean checkInput() {
        resetErrorMessage(layoutPassword);

        String passwordValue = inputPassword.getText().toString();

        boolean result = true;

        if (passwordValue.trim().length() == 0) {
            layoutPassword.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    // Listener processo di lettura dal database degli user homes
    final DatabaseReaderListener dbReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(String key, Object object) {
            Map<String, UserHome> userHomes = (Map<String, UserHome>) object;






        }

        @Override
        public void onReadEmpty() {

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
            dismissProgress();
        }
    };
}