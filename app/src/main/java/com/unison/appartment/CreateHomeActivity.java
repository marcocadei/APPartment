package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.unison.appartment.model.Home;
import com.unison.appartment.model.Member;
import com.unison.appartment.model.UserHome;

import java.util.HashMap;
import java.util.Map;

public class CreateHomeActivity extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    private static final int MIN_HOME_PASSWORD_LENGTH = 6;

    EditText inputHomeName;
    EditText inputPassword;
    EditText inputRepeatPassword;
    EditText inputNickname;
    TextInputLayout layoutHomeName;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;
    TextInputLayout layoutNickname;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_home);

        inputHomeName = findViewById(R.id.activity_create_home_input_homename_value);
        inputPassword = findViewById(R.id.activity_create_home_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password_value);
        inputNickname = findViewById(R.id.activity_create_home_input_repeat_nickname_value);
        layoutHomeName = findViewById(R.id.activity_create_home_input_homename);
        layoutPassword = findViewById(R.id.activity_create_home_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_create_home_input_repeat_password);
        layoutNickname = findViewById(R.id.activity_create_home_input_nickname);

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

        FloatingActionButton floatNext = findViewById(R.id.activity_create_home_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(CreateHomeActivity.this);
                if (checkInput()) {
                    // Se i controlli locali vanno a buon fine controllo che la casa esista
                    checkHouseExists(inputHomeName.getText().toString(), inputPassword.getText().toString(), inputNickname.getText().toString());
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
            layoutHomeName.setError(getString(R.string.form_error_invalid_character));
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

    private void checkHouseExists(final String homeName, final String password, final String nickname) {
        progress = ProgressDialog.show(
                this,
                getString(R.string.activity_create_home_progress_title),
                getString(R.string.activity_create_home_progress_description), true);

        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Esiste già una casa con il nome specificato dall'utente
                    layoutHomeName.setError(getString(R.string.form_error_home_exists));
                    dismissProgress();
                }
                else {
                    writeNewHomeInDb(homeName, password, nickname);
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
                dismissProgress();
                dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
            }
        });
    }

    private void writeNewHomeInDb(final String homeName, final String password, final String nickname) {
        String separator = getString(R.string.db_separator);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String homePath = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
        final String familyPath = getString(R.string.db_families) + separator + getString(R.string.db_families_homename, homeName) + separator + getString(R.string.db_families_homename_userid, uid);
        final String userhomePath = getString(R.string.db_userhomes) + separator + getString(R.string.db_userhomes_userid, uid) + separator + getString(R.string.db_userhomes_userid_homename, homeName);

        Map<String, Object> childUpdates = new HashMap<>();
        Home home = new Home(homeName, password);
        childUpdates.put(homePath, home);
        Member member = new Member(nickname);
        childUpdates.put(familyPath, member);
        UserHome userHome = new UserHome(homeName, UserHome.ROLE_OWNER);
        childUpdates.put(userhomePath, userHome);

        dbRef.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            moveToNextActivity();
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
                                    // Implica: ??? FIXME

                                    FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                                    dismissProgress();
                                    dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
                                }
                                else {
                                    // Altro errore generico
                                    FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                                    dismissProgress();
                                    dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
                                }
                            }
                            catch (Exception e) {
                                // Generico
                                FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
                                dismissProgress();
                                dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
                            }
                            dismissProgress();
                        }
                    }
                });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(CreateHomeActivity.this, MainActivity.class);
        // Passo il nome della casa all'activity successiva
        i.putExtra(MainActivity.EXTRA_HOME_NAME, inputHomeName.getText().toString());
        startActivity(i);
        finish();
    }

    private void dismissProgress() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void onDialogFragmentDismiss() {
        Intent i = new Intent(this, EnterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
