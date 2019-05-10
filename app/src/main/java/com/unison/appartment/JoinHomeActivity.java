package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.util.Log;
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
import com.unison.appartment.model.Member;
import com.unison.appartment.model.UserHome;

import java.util.HashMap;
import java.util.Map;

public class JoinHomeActivity extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    EditText inputHomeName;
    EditText inputPassword;
    EditText inputNickname;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;
    TextInputLayout layoutNickname;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_home);

        inputHomeName = findViewById(R.id.activity_join_home_input_homename_value);
        inputPassword = findViewById(R.id.activity_join_home_input_password_value);
        inputNickname = findViewById(R.id.activity_join_home_input_nickname_value);
        layoutHomeName = findViewById(R.id.activity_join_home_input_homename);
        layoutPassword = findViewById(R.id.activity_join_home_input_password);
        layoutNickname = findViewById(R.id.activity_join_home_input_nickname);

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

        FloatingActionButton floatNext = findViewById(R.id.activity_join_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(JoinHomeActivity.this);
                if (checkInput()) {
                    checkHomeCredentials(inputHomeName.getText().toString(), inputPassword.getText().toString(), inputNickname.getText().toString());
                }
            }
        });
    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private boolean checkInput() {
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

    private void checkHomeCredentials(final String homeName, final String password, final String nickname) {
        progress = ProgressDialog.show(
                this,
                getString(R.string.activity_join_home_progress_check_title),
                getString(R.string.activity_join_home_progress_check_description), true);

        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName) + separator + getString(R.string.db_homes_homename_password);
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
                    String homePassword = dataSnapshot.getValue(String.class);
                    if (!password.equals(homePassword)) {
                        // La password inserita è sbagliata (viene comunque mostrato un messaggio d'errore generico)
                        layoutHomeName.setError(getString(R.string.form_error_incorrect_credentials));
                        layoutPassword.setError(getString(R.string.form_error_incorrect_credentials));
                        progress.dismiss();
                    }
                    else {
                        // Credenziali corrette, posso passare alla scrittura dei nuovi dati nel db
                        writeInDb(homeName, password, nickname);
                    }
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
                FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                progress.dismiss();
                dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
            }
        });
    }

    private void writeInDb(final String homeName, final String password, final String nickname) {
        String separator = getString(R.string.db_separator);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String familiesPath = getString(R.string.db_families) + separator + getString(R.string.db_families_homename, homeName) + separator + getString(R.string.db_families_homename_userid, uid);
        String userhomesPath = getString(R.string.db_userhomes) + separator + getString(R.string.db_userhomes_userid, uid) + separator + getString(R.string.db_userhomes_userid_homename, homeName);

        Map<String, Object> childUpdates = new HashMap<>();
        Member member = new Member(nickname);
        childUpdates.put(familiesPath, member);
        UserHome userHome = new UserHome(homeName, UserHome.ROLE_SLAVE);
        childUpdates.put(userhomesPath, userHome);

        dbRef.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            moveToNextActivity();
                            progress.dismiss();
                        }
                        else {
                            try {
                                throw task.getException();
                            }
                            catch (DatabaseException e) {
                                int errorCode = DatabaseError.fromException(e).getCode();
                                if (errorCode == DatabaseError.PERMISSION_DENIED || errorCode == DatabaseError.USER_CODE_EXCEPTION) {
                                    // Regole di sicurezza violate
                                    // Implica: L'utente ha specificato una casa di cui è già membro
                                    layoutHomeName.setError(getString(R.string.form_error_home_already_joined));
                                    progress.dismiss();
                                }
                                else {
                                    // Altro errore generico
                                    FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                                    progress.dismiss();
                                    dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
                                }
                            }
                            catch (Exception e) {
                                // Generico
                                FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                                progress.dismiss();
                                dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
                            }
                            progress.dismiss();
                        }
                    }
                });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(JoinHomeActivity.this, MainActivity.class);
        // Passo il nome della casa all'activity successiva
        i.putExtra(MainActivity.EXTRA_HOME_NAME, inputHomeName.getText().toString());
        startActivity(i);
        finish();
    }

    @Override
    public void onDialogFragmentDismiss() {
        Intent i = new Intent(this, HomeListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
