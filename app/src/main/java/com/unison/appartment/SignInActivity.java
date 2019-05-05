package com.unison.appartment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText inputHomeName = findViewById(R.id.activity_signin_input_homename_value);
        final EditText inputName = findViewById(R.id.activity_signin_input_username_value);
        final EditText inputPassword = findViewById(R.id.activity_signin_input_password_value);

        FloatingActionButton floatNext = findViewById(R.id.activity_sign_in_float_next);
        floatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(inputHomeName.getText().toString(), inputName.getText().toString(), inputPassword.getText().toString());
            }
        });

    }

    private void signIn(final String homeName, final String username, final String password) {
        final ProgressDialog progress = ProgressDialog.show(
                this,
                getString(R.string.activity_signin_signin_title),
                getString(R.string.activity_signin_signin_description), true);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        // Prima di tutto devo recuperare l'indirizzo email dell'utente
        database.child("users").child(homeName + "-" + username).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.getValue(String.class);
                    performSignIn(email, password, progress);
                } else {
                    Log.d("login", "fallito");
                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void performSignIn(String email, String password, final ProgressDialog progress) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO rimuovere questa riga di codice
                            auth.signOut();
                            moveToNextActivity();
                        } else {
                            Log.d("login", "fallito");
                        }
                        progress.dismiss();
                    }
                });
    }

    private void moveToNextActivity() {
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(i);
    }
}
