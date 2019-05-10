package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.User;

public class SignUpActivity extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    private static final int MIN_USER_PASSWORD_LENGTH = 6;

    EditText inputEmail;
    EditText inputPassword;
    EditText inputRepeatPassword;
    EditText inputAge;
    TextInputLayout layoutEmail;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;
    TextInputLayout layoutAge;
    RadioGroup inputGender;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Precondizione: Se viene acceduta questa activity, vuol dire che non c'è nessun utente loggato

        inputEmail = findViewById(R.id.activity_signup_input_email_value);
        inputPassword = findViewById(R.id.activity_signup_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_signup_input_repeat_password_value);
        inputAge = findViewById(R.id.activity_signup_input_age_value);
        inputGender = findViewById(R.id.activity_signup_radio_gender);
        layoutEmail = findViewById(R.id.activity_signup_input_email);
        layoutPassword = findViewById(R.id.activity_signup_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_signup_input_repeat_password);
        layoutAge = findViewById(R.id.activity_signup_input_age);

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutEmail);
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
        inputAge.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutAge);
            }
        });

        // Gestione click sul bottone per completare l'inserimento
        FloatingActionButton floatFinish = findViewById(R.id.activity_signup_float_finish);
        /*
        Se l'utente proviene dalla CreateHomeActivity, alla pressione del bottone bisogna:
        - creare un nuovo record in FirebaseAuth
        - inserire nel database la nuova casa
        - inserire nel database il nuovo utente (owner della nuova casa)
        Se l'utente invece proviene dalla JoinHomeActivity la casa è già esistente, quindi bisogna solo
        procedere al salvataggio dei dati relativi all'utente (sia nel database che in FirebaseAuth)
         */
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(SignUpActivity.this);
                if (checkInput()) {
                    writeAuthInfo(createUser());
                }
            }
        });
    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private User createUser() {
        // Recupero i valori dei campi della form
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        int age = Integer.parseInt(inputAge.getText().toString());
        RadioButton selectedGender = findViewById(inputGender.getCheckedRadioButtonId());
        String gender = selectedGender.getText().toString();

        return new User(email, password, age, gender);
    }

    private boolean checkInput() {
        resetErrorMessage(layoutEmail);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);
        resetErrorMessage(layoutAge);

        inputEmail.setText(inputEmail.getText().toString().trim());

        String emailValue = inputEmail.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();
        String ageValue = inputAge.getText().toString();

        boolean result = true;

        // Controllo che la mail inserita sia valida
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                layoutEmail.setError(getString(R.string.form_error_incorrect_email));
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
        if (passwordValue.trim().length() < MIN_USER_PASSWORD_LENGTH) {
            layoutPassword.setError(null);
            layoutPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_USER_PASSWORD_LENGTH));
            result = false;
        }
        if (repeatPasswordValue.trim().length() < MIN_USER_PASSWORD_LENGTH) {
            layoutRepeatPassword.setError(null);
            layoutRepeatPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_USER_PASSWORD_LENGTH));
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
        if (ageValue.trim().length() == 0) {
            layoutAge.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    private void writeAuthInfo(final User newUser) {
        progress = ProgressDialog.show(
                this,
                getString(R.string.activity_signup_signup_title),
                getString(R.string.activity_signup_signup_description), true);

        // Salvataggio delle informazioni in Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeUserInDb(newUser);
                        }
                        else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthUserCollisionException e) {
                                // Email già in uso
                                layoutEmail.setError(getString(R.string.form_error_duplicate_email));
                            }
                            catch (FirebaseAuthWeakPasswordException e) {
                                // Password non abbastanza robusta

                            }
                            catch (FirebaseAuthInvalidCredentialsException e) {
                                // Email malformata

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

    private void writeUserInDb(final User newUser) {
        // Scrittura dei dati relativi al nuovo utente nel database
        String separator = getString(R.string.db_separator);
        String path = getString(R.string.db_users) + separator + getString(R.string.db_users_uid, FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        dbRef.setValue(newUser)
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
                            catch (Exception e) {
                                // (DatabaseException se si verifica una violazione delle regole di sicurezza)
                                // Generico
                                showErrorDialog();
                            }
                            dismissProgress();
                        }
                    }
                });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(this, HomeListActivity.class);
        startActivity(i);
        finish();
    }

    private void showErrorDialog() {
        FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
        dismissProgress();
        dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
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
