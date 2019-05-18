package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.unison.appartment.R;
import com.unison.appartment.state.Appartment;

/**
 * Classe che rappresenta l'Activity per entrare nell'applicazione, registrandosi oppure accedendo
 */
public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Button btnSignup = findViewById(R.id.activity_enter_btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EnterActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        Button btnLogin = findViewById(R.id.activity_enter_btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EnterActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });

        /*
        Nota: Tutte le operazioni precedenti (es. impostazione dei listener sui bottoni) sono
        eseguite a prescindere dallo stato dell'autenticazione; se infatti l'utente esegue il
        logout, si vuole che questo ritorni alla EnterActivity (che quindi deve avere l'interfaccia
        già pronta).
         */

        // Controllo se c'è già un utente loggato: in caso vado direttamente alla UserProfileActivity
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // Controllo se l'utente è uscito dall'app mentre era in una casa: in caso vado direttamente
            // alla MainActivity
            Intent i;
            if (Appartment.getInstance().getHome() != null) {
                i = new Intent(EnterActivity.this, MainActivity.class);
            } else {
                i = new Intent(EnterActivity.this, UserProfileActivity.class);
            }
            startActivity(i);
            finish();
        }
    }
}