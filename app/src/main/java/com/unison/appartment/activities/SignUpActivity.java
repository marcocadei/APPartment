package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import com.unison.appartment.fragments.DatePickerFragment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.utils.DateUtils;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.R;
import com.unison.appartment.model.User;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Classe che rappresenta l'Activity per effettuare la registrazione all'applicazione
 */
public class SignUpActivity extends FormActivity implements DatePickerDialog.OnDateSetListener {

    private static final int MIN_USER_PASSWORD_LENGTH = 6;
    private final static String BUNDLE_KEY_DATE_OBJECT = "dateObject";

    EditText inputEmail;
    EditText inputPassword;
    EditText inputRepeatPassword;
    EditText inputBirthdate;
    EditText inputNickname;
    TextInputLayout layoutEmail;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;
    TextInputLayout layoutBirthdate;
    TextInputLayout layoutNickname;
    RadioGroup inputGender;

    Date birthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Precondizione: Se viene acceduta questa activity, vuol dire che non c'è nessun utente loggato

        inputEmail = findViewById(R.id.activity_signup_input_email_value);
        inputPassword = findViewById(R.id.activity_signup_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_signup_input_repeat_password_value);
        inputBirthdate = findViewById(R.id.activity_signup_input_birthdate_value);
        inputNickname = findViewById(R.id.activity_signup_input_nickname_value);
        inputGender = findViewById(R.id.activity_signup_radio_gender);
        layoutEmail = findViewById(R.id.activity_signup_input_email);
        layoutPassword = findViewById(R.id.activity_signup_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_signup_input_repeat_password);
        layoutBirthdate = findViewById(R.id.activity_signup_input_birthdate);
        layoutNickname = findViewById(R.id.activity_signup_input_nickname);

        /*
        I listener sono duplicati anche se fanno la stessa cosa poiché la view che prende il focus
        è il textInput mentre la view su cui bisogna operare per rimuovere il messaggio di errore
        è il textLayout, e per qualche motivo invocando getParent sul textInput NON viene restituito
        il textLayout.
         */
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
        inputNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutNickname);
            }
        });

        inputBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetErrorMessage(layoutBirthdate);
                showDatePickerDialog();
            }
        });

        FloatingActionButton floatFinish = findViewById(R.id.activity_signup_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(SignUpActivity.this);
                if (checkInput()) {
                    writeAuthInfo(createUser());
                }
            }
        });

        /*
        Se lo schermo è stato ruotato mentre il date picker era aperto, l'activity è stata distrutta
        e ora sta venendo ricreata. Si vuole mantenere aperto lo stesso date picker, a cui però
        deve essere cambiato il listener dal momento che altrimenti farebbe riferimento all'activity
        distrutta non più esistente.
         */
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DatePickerFragment.TAG_DATE_PICKER);
        if (fragment != null) {
            ((DatePickerFragment) fragment).setListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_DATE_OBJECT, birthdate);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        birthdate = (Date) savedInstanceState.getSerializable(BUNDLE_KEY_DATE_OBJECT);
    }

    protected boolean checkInput() {
        resetErrorMessage(layoutEmail);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);
        resetErrorMessage(layoutBirthdate);
        resetErrorMessage(layoutNickname);

        inputEmail.setText(inputEmail.getText().toString().trim());
        inputNickname.setText(inputNickname.getText().toString().trim());

        String emailValue = inputEmail.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();
        String birthdateValue = inputBirthdate.getText().toString();
        String nicknameValue = inputNickname.getText().toString();

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

        // Controllo che la data scelta non si a successiva alla data odierna
        Calendar today = Calendar.getInstance();
        if (birthdate != null) {
            if (birthdate.compareTo(today.getTime()) > 0) {
                layoutBirthdate.setError(getString(R.string.form_error_invalid_date));
                result = false;
            }
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
        if (birthdateValue.length() == 0) {
            layoutBirthdate.setError(null);
            layoutBirthdate.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (nicknameValue.trim().length() == 0) {
            layoutNickname.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    private User createUser() {
        // Precondizione: Tutti i campi della form sono corretti

        // Recupero i valori dei campi della form
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String nickname = inputNickname.getText().toString();
        RadioButton selectedGender = findViewById(inputGender.getCheckedRadioButtonId());
        int gender;
        switch (inputGender.getCheckedRadioButtonId()) {
            default:
            case R.id.activity_signup_radio_gender_male:
                gender = User.GENDER_MALE;
                break;
            case R.id.activity_signup_radio_gender_female:
                gender = User.GENDER_FEMALE;
                break;
        }

        return new User(email, password, nickname, DateUtils.formatDateWithStandardLocale(birthdate), gender);
    }

    private void writeAuthInfo(final User newUser) {
        progressDialog = FirebaseProgressDialogFragment.newInstance(
                getString(R.string.activity_signup_signup_title),
                getString(R.string.activity_signup_signup_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

        // Salvataggio delle informazioni in Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeUserInDb(newUser);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                // Email già in uso
                                layoutEmail.setError(getString(R.string.form_error_duplicate_email));
                            } catch (FirebaseAuthWeakPasswordException e) {
                                // Password non abbastanza robusta

                                /*
                                Questa eccezione non dovrebbe mai verificarsi in quanto sono già
                                eseguiti controlli lato client sulla lunghezza della password.
                                Se si entra in questo blocco c'è qualche problema!
                                 */
                                Log.w(getClass().getCanonicalName(), e.getMessage());
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Email malformata

                                /*
                                Questa eccezione non dovrebbe mai verificarsi in quanto sono già
                                eseguiti controlli lato client sulla struttura dell'indirizzo mail.
                                Se si entra in questo blocco c'è qualche problema!
                                 */
                                Log.w(getClass().getCanonicalName(), e.getMessage());
                            } catch (Exception e) {
                                // Generico
                                showErrorDialog();
                            }
                            dismissProgress();
                        }
                    }
                });
    }

    /**
     * Metodo per effettuare la scrittura in Firebase Database di un nuovo User
     *
     * @param newUser Il nuovo User che si vuole scrivere in Firebase Database
     */
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
                            moveToNextActivity(UserProfileActivity.class);
                            dismissProgress();
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                // (DatabaseException se si verifica una violazione delle regole di sicurezza)
                                // Generico
                                showErrorDialog();
                            }
                            dismissProgress();
                        }
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        birthdate = cal.getTime();
        inputBirthdate.setText(DateUtils.formatDateWithCurrentDefaultLocale(birthdate));
    }

    public void showDatePickerDialog() {
        int year, month, day;
        final Calendar cal = Calendar.getInstance();

        // Se la data non è ancora stata selezionata, uso la data corrente come quella di default nel date picker
        // (non faccio niente perché Calendar.getInstance() mi restituisce già la data corrente)

        // Altrimenti, uso la data precedentemente selezionata come quella di default
        if (birthdate != null) {
            cal.setTime(birthdate);
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerFragment.newInstance(year, month, day, this).show(getSupportFragmentManager(), DatePickerFragment.TAG_DATE_PICKER);
    }

}
