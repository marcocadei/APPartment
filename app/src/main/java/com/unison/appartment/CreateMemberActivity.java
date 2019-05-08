package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.Member;

import java.util.HashMap;
import java.util.Map;

public class CreateMemberActivity extends AppCompatActivity {

    public static final String EXTRA_HOME_NAME = "homeName";
    public static final String EXTRA_HOME_PASSWORD = "homePassword";

    private static final int MIN_USER_PASSWORD_LENGTH = 6;

    EditText inputEmail;
    EditText inputUsername;
    EditText inputPassword;
    EditText inputRepeatPassword;
    EditText inputAge;
    TextInputLayout layoutEmail;
    TextInputLayout layoutUsername;
    TextInputLayout layoutPassword;
    TextInputLayout layoutRepeatPassword;
    TextInputLayout layoutAge;
    RadioGroup inputGender;
    RadioGroup inputRole;

    // Utilizzato per la registrazione dell'utente
    private String referrer;
    private String homeName;
    private String homePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        // Precondizione: Se viene acceduta questa activity, vuol dire che non c'è nessun utente loggato

        // Recupero i parametri dell'activity
        final Intent i = getIntent();
        referrer = i.getStringExtra(Intent.EXTRA_REFERRER_NAME);
        homeName = i.getStringExtra(EXTRA_HOME_NAME);
        if (referrer.equals(CreateHomeActivity.class.toString())) {
            // La password è stata passata nell'intent solo se si è arrivati qui da CreateHomeActivity
            homePassword = i.getStringExtra(EXTRA_HOME_PASSWORD);
        }

        inputEmail = findViewById(R.id.activity_create_member_input_email_value);
        inputUsername = findViewById(R.id.activity_create_member_input_username_value);
        inputPassword = findViewById(R.id.activity_create_member_input_password_value);
        inputRepeatPassword = findViewById(R.id.activity_create_member_input_repeat_password_value);
        inputAge = findViewById(R.id.activity_create_member_input_age_value);
        inputGender = findViewById(R.id.activity_create_member_radio_gender);
        inputRole = findViewById(R.id.activity_create_member_radio_role);
        layoutEmail = findViewById(R.id.activity_create_member_input_email);
        layoutUsername = findViewById(R.id.activity_create_member_input_username);
        layoutPassword = findViewById(R.id.activity_create_member_input_password);
        layoutRepeatPassword = findViewById(R.id.activity_create_member_input_repeat_password);
        layoutAge = findViewById(R.id.activity_create_member_input_age);

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutEmail);
            }
        });
        inputUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutUsername);
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

        // Se provengo dall'activity create home allora l'unico ruolo selezionabile deve essere 'Creatore'
        if (referrer.equals(CreateHomeActivity.class.toString())) {
            RadioButton radioRoleOwner = findViewById(R.id.activity_create_member_radio_role_owner);
            RadioButton radioRoleMaster= findViewById(R.id.activity_create_member_radio_role_master);
            RadioButton radioRoleSlave = findViewById(R.id.activity_create_member_radio_role_slave);
            radioRoleOwner.setEnabled(true);
            radioRoleOwner.setChecked(true);
            radioRoleMaster.setEnabled(false);
            radioRoleSlave.setEnabled(false);
        }

        // Gestione click sul bottone per completare l'inserimento
        FloatingActionButton floatFinish = findViewById(R.id.activity_create_member_float_finish);
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
                KeyboardUtils.hideKeyboard(CreateMemberActivity.this);
                if (checkInput()) {
                    writeNewDatabaseRecord(createMember(), referrer.equals(CreateHomeActivity.class.toString()));
                }
            }
        });
    }

    private void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private Member createMember() {
        // Recupero i valori dei campi della form
        String email = inputEmail.getText().toString();
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        int age = Integer.parseInt(inputAge.getText().toString());
        RadioButton selectedGender = findViewById(inputGender.getCheckedRadioButtonId());
        String gender = selectedGender.getText().toString();
        RadioButton selectedRole = findViewById(inputRole.getCheckedRadioButtonId());
        String role = selectedRole.getText().toString();

        return new Member(email, username, password, age, gender, role);
    }

    private boolean checkInput() {
        resetErrorMessage(layoutEmail);
        resetErrorMessage(layoutUsername);
        resetErrorMessage(layoutPassword);
        resetErrorMessage(layoutRepeatPassword);
        resetErrorMessage(layoutAge);

        inputEmail.setText(inputEmail.getText().toString().trim());
        inputUsername.setText(inputUsername.getText().toString().trim());

        String emailValue = inputEmail.getText().toString();
        String usernameValue = inputUsername.getText().toString();
        String passwordValue = inputPassword.getText().toString();
        String repeatPasswordValue = inputRepeatPassword.getText().toString();
        String ageValue = inputAge.getText().toString();

        boolean result = true;

        // Controllo che tutti i campi siano compilati
        if (emailValue.trim().length() == 0) {
            layoutEmail.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (usernameValue.trim().length() == 0) {
            layoutUsername.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (ageValue.trim().length() == 0) {
            layoutAge.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

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
            layoutPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_USER_PASSWORD_LENGTH));
            result = false;
        }
        if (repeatPasswordValue.trim().length() < MIN_USER_PASSWORD_LENGTH) {
            layoutRepeatPassword.setError(String.format(getString(R.string.form_error_short_password), MIN_USER_PASSWORD_LENGTH));
            result = false;
        }

        return result;
    }

    private void writeNewDatabaseRecord(final Member newMember, boolean writeHomeData) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_create_member_signup_title),
                getString(R.string.activity_create_member_signup_description), true);

        // Scrittura dei dati relativi alla casa e al proprietario nel database
        String separator = getString(R.string.db_separator);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();

        String userPath = getString(R.string.db_users) + separator + getString(R.string.db_users_userid, homeName, newMember.getName());
        childUpdates.put(userPath, newMember);

        // TODO manca controllo sulla mail

        if (writeHomeData) {
            Home newHome = new Home(homeName, homePassword);
            newHome.addMember(newMember.getName());
            String homePath = getString(R.string.db_homes) + separator + getString(R.string.db_homes_homename, homeName);
            childUpdates.put(homePath, newHome);
        }

        dbRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        writeNewAuthInfo(newMember, progress);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO gestire errore
                        /*
                        Se questa operazione fallisce, vuol dire necessariamente che qualcun'altro
                        nel frattempo ha creato una casa con lo stesso nome. Di conseguenza, l'unica
                        cosa che può essere fatta è dare informazione dell'errore all'utente e
                        tornare alla EnterActivity.
                         */
                    }
                });
    }

    private void writeNewAuthInfo(final Member newMember, final ProgressDialog progress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(newMember.getEmail(), inputPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        moveToNextActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /*
                        Se si verifica un fallimento qui l'owner e la casa nel db sono GIÀ stati
                        salvati, quindi bisogna avvertire l'utente che deve semplicemente riloggarsi
                        (se provasse a creare una nuova casa con gli stessi dati, gli verrebbe detto
                        che è già esistente).
                         */
                    }
                });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, EnterActivity.class);
        startActivity(i);
        finish();
    }
}
