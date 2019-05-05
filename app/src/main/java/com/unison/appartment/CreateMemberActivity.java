package com.unison.appartment;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.Member;

public class CreateMemberActivity extends AppCompatActivity {
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

        final EditText inputEmail = findViewById(R.id.activity_create_member_input_email_value);
        final EditText inputUsername = findViewById(R.id.activity_create_member_input_username_value);
        final EditText inputAge = findViewById(R.id.activity_create_member_input_age_value);
        final RadioGroup inputGender = findViewById(R.id.activity_create_member_radio_gender);
        final RadioGroup inputRole = findViewById(R.id.activity_create_member_radio_role);

        // Gestione click sul bottone per aggiungere un nuovo membro
        FloatingActionButton floatNewMember = findViewById(R.id.activity_create_member_float_new_member);
        floatNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateMemberActivity.this, CreateMemberActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        // Gestione click sul bottone per completare l'inserimento
        FloatingActionButton floatFinish = findViewById(R.id.activity_create_member_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupero i valori dei campi della form
                String email = inputEmail.getText().toString();
                String name = inputUsername.getText().toString();
                int age = Integer.parseInt(inputAge.getText().toString());
                RadioButton selectedGender = findViewById(inputGender.getCheckedRadioButtonId());
                String gender = selectedGender.getText().toString();
                RadioButton selectedRole = findViewById(inputRole.getCheckedRadioButtonId());
                String role = selectedRole.getText().toString();
                // Nel caso in cui provenga dall'actiivity di enter ho dei parametri aggiuntivi
                // TODO questi parametri ce li devo avere anche se provengo dall'activity family
                if (origin.equals("fromEnter")) {
                    homePassword = i.getStringExtra("homePassword");
                    homeName = i.getStringExtra("homeName");
                }
                // Creo l'oggetto membero
                Member newMember = new Member(email, name, age, gender, role, 0);

                // Effettuo la registrazione del nuovo membro
                signUp(newMember);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO rimuovere questa riga di codice
        auth.signOut();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // TODO costruire l'utente leggendo i dati dal DB
            Member newMember = null;
            moveToNextActivity(origin, newMember);
        }
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
                            FirebaseUser currentUser = auth.getCurrentUser();
                            insertUser(newMember);
                            moveToNextActivity(origin, newMember);
                        } else {
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
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").child(homeName + "-" + newMember.getName()).child("name").setValue(newMember.getName());
        database.child("users").child(homeName + "-" + newMember.getName()).child("age").setValue(newMember.getAge());
        database.child("users").child(homeName + "-" + newMember.getName()).child("email").setValue(newMember.getEmail());
        database.child("users").child(homeName + "-" + newMember.getName()).child("gender").setValue(newMember.getGender());
        database.child("users").child(homeName + "-" + newMember.getName()).child("role").setValue(newMember.getRole());
        database.child("users").child(homeName + "-" + newMember.getName()).child("points").setValue(newMember.getPoints());

        // TODO spostare questo codice in un punto migliore
        // Salvo anche la casa
        database.child("homes").child(homeName).child("name").setValue(homeName);
        database.child("homes").child(homeName).child("password").setValue(homePassword);
        database.child("homes").child(homeName).child("members").child(homeName + "-" + newMember.getName()).setValue(true);
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
}
