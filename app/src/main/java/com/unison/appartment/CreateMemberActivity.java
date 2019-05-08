package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateMemberActivity extends AppCompatActivity {

    private static final String FROM_CREATE_MEMBER = "fromCreateMember";
    private static final int ADD_MEMBER_REQUEST_CODE = 1;

    EditText inputEmail;
    EditText inputUsername;
    EditText inputAge;
    TextInputLayout layoutEmail;
    TextInputLayout layoutUsername;
    TextInputLayout layoutAge;
    RadioGroup inputGender;
    RadioGroup inputRole;

    // Utilizzato per la registrazione dell'utente
    private FirebaseAuth auth;
    private String origin;
    private String homePassword;
    private String homeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        // Inizializzo il componente di firebase usato per l'autenticazione
        auth = FirebaseAuth.getInstance();

        // Recupero i parametri dell'activity
        final Intent i = getIntent();
        origin = i.getStringExtra("origin");
        // Nel caso in cui provenga dall'actiivity di enter ho dei parametri aggiuntivi
        // TODO questi parametri ce li devo avere anche se provengo dall'activity family
        if (origin.equals("fromEnter")) {
            homeName = i.getStringExtra("homeName");
            homePassword = i.getStringExtra("homePassword");
        }

        inputEmail = findViewById(R.id.activity_create_member_input_email_value);
        inputUsername = findViewById(R.id.activity_create_member_input_username_value);
        inputAge = findViewById(R.id.activity_create_member_input_age_value);
        inputGender = findViewById(R.id.activity_create_member_radio_gender);
        inputRole = findViewById(R.id.activity_create_member_radio_role);
        layoutEmail = findViewById(R.id.activity_create_member_input_email);
        layoutUsername = findViewById(R.id.activity_create_member_input_username);
        layoutAge = findViewById(R.id.activity_create_member_input_age);

        // Se provengo dall'activity create home allora l'unico
        // ruolo selezionabile deve essere 'Creatore'
        if (origin.equals(CreateHomeActivity.FROM_ENTER)) {
            RadioButton radioRoleOwner = findViewById(R.id.activity_create_member_radio_role_owner);
            RadioButton radioRoleMaster= findViewById(R.id.activity_create_member_radio_role_master);
            RadioButton radioRoleSlave = findViewById(R.id.activity_create_member_radio_role_slave);
            radioRoleOwner.setEnabled(true);
            radioRoleMaster.setEnabled(false);
            radioRoleSlave.setEnabled(false);
            radioRoleOwner.setChecked(true);
        }

//        // Gestione click sul bottone per aggiungere un nuovo membro
//        FloatingActionButton floatNewMember = findViewById(R.id.activity_create_member_float_new_member);
//        floatNewMember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (checkInput()) {
//                    Intent i = new Intent(CreateMemberActivity.this, CreateMemberActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.putExtra("origin", FROM_CREATE_MEMBER);
//                    // La prima volta passo un array vuoto, mentre le chiamate successive alla stessa
//                    // activity passo l'array di membri costruiti fino a quel momento
//                    ArrayList<Member> newMembers = (ArrayList<Member>) i.getSerializableExtra("newMembers");
//                    // TODO aggiungere controllo che nuovo utente non abbia un nome o l'email uguale a quello degli utenti già inseriti
//                    newMembers.add(createMember());
//                    i.putExtra("newMembers", newMembers);
//                    startActivity(i);
//                }
//            }
//        });

        // Gestione click sul bottone per completare l'inserimento
        FloatingActionButton floatFinish = findViewById(R.id.activity_create_member_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    signUp(createMember());
                }
            }
        });
    }

    private Member createMember() {
        // Recupero i valori dei campi della form
        String email = inputEmail.getText().toString();
        String name = inputUsername.getText().toString();
        int age = Integer.parseInt(inputAge.getText().toString());
        RadioButton selectedGender = findViewById(inputGender.getCheckedRadioButtonId());
        String gender = selectedGender.getText().toString();
        RadioButton selectedRole = findViewById(inputRole.getCheckedRadioButtonId());
        String role = selectedRole.getText().toString();

        return new Member(email, name, age, gender, role, 0);
    }

    private boolean checkInput() {
        String emailValue = inputEmail.getText().toString();
        String usernameValue = inputUsername.getText().toString();
        String ageValue = inputAge.getText().toString();
        boolean result = true;
        // Controllo che tutti i campi siano compilati
        if (emailValue.length() == 0) {
            layoutEmail.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutEmail.setError(null);
            layoutEmail.setErrorEnabled(false);
        }
        if (usernameValue.length() == 0) {
            layoutUsername.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutUsername.setError(null);
            layoutUsername.setErrorEnabled(false);
        }
        if (ageValue.length() == 0) {
            layoutAge.setError(getString(R.string.form_error_missing_value));
            result = false;
        } else {
            layoutAge.setError(null);
            layoutAge.setErrorEnabled(false);
        }
        if (emailValue.length() > 0) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                layoutEmail.setError(getString(R.string.form_error_incorrect_email));
                result = false;
            } else {
                layoutEmail.setError(null);
                layoutEmail.setErrorEnabled(false);
            }
        }
        findViewById(R.id.activity_create_member).requestFocus();

        return result;
    }

    private void signUp(final Member newMember) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_create_member_signup_title),
                getString(R.string.activity_create_member_signup_description), true);
        /**
         * ATTENZIONE: la password deve essere di almeno 6 caratteri, altrimenti firebase fallisce
         */
        auth.createUserWithEmailAndPassword(newMember.getEmail(), homePassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            insertUser(newMember);
                        } else {
                            layoutEmail.setError(getString(R.string.form_error_incorrect_email));
                            Log.d("registrazione", "fallita");
                        }
                        progress.dismiss();
                    }
                });
    }

    /**
     * Una volta registrato l'utente, associo allo stesso delle informazioni aggiuntive nel db
     */
    private void insertUser(Member newMember) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users/" + (homeName + "-" + newMember.getName()));
        database.setValue(newMember);

        // Una volta terminata la scrittura vado alla prossima activity
        moveToNextActivity(origin, newMember);
    }

    private void insertHome(Member newMember) {
        // TODO spostare questo codice in un punto migliore
        // Salvo anche la casa
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("homes/" + homeName);
        database.child("name").setValue(homeName);
        database.child("password").setValue(homePassword);
        database.child("members").child(homeName + "-" + newMember.getName()).setValue(true);
    }

    /**
     * In base all'activity da cui provengo andrò in activity differenti
     * @param origin activity da cui provengo
     * @param newMember nuovo membro appena creato
     */
    private void moveToNextActivity(String origin, Member newMember) {
        if(origin.equals("fromEnter")) {
            Intent i = new Intent(CreateMemberActivity.this, MainActivity.class);
            startActivity(i);
        }
        else if(origin.equals("fromFamily")){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("newMember", newMember);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, EnterActivity.class);
        startActivity(i);
        finish();
    }
}
