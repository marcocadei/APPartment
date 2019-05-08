package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class EnterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Button btnRegister = findViewById(R.id.activity_enter_btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EnterActivity.this, CreateHomeActivity.class);
                startActivity(i);
            }
        });

        Button btnJoin = findViewById(R.id.activity_enter_btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EnterActivity.this, JoinHomeActivity.class);
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // C'è già un utente loggato: vado direttamente alla MainActivity
            Intent i = new Intent(EnterActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        /*
        Nota: Tutte le operazioni precedenti (es. impostazione dei listener sui bottoni) sono
        eseguite a prescindere dallo stato dell'autenticazione; se infatti l'utente esegue il
        logout dalla MainActivity, si vuole che questo ritorni alla EnterActivity (che quindi
        deve avere l'interfaccia già pronta).
         */
    }
}
