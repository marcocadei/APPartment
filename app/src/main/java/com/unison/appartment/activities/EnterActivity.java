package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.state.Appartment;

/**
 * Classe che rappresenta l'Activity per entrare nell'applicazione, registrandosi oppure accedendo
 */
public class EnterActivity extends ActivityWithNetworkConnectionDialog {

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

        // Controllo se c'è già un utente loggato
        String loggedUserUid = new FirebaseAuth().getCurrentUserUid();
        if (loggedUserUid != null) {
            /*
            In ogni caso se c'è già un utente loggato deve essere caricato l'oggetto User dentro
            Appartment. Questa chiamata ha sempre l'effetto di triggerare la lettura dalle Shared
            Preferences, in cui si suppone che ci sia **già** l'oggetto User salvato se si è nel caso
            in cui l'utente risulta già loggato.
             */
            Appartment.getInstance().getUser();

            Intent i;
            if (Appartment.getInstance().getHome() != null) {
                // Se l'utente è uscito dall'app mentre era in una casa: in caso vado direttamente
                // alla MainActivity
                i = new Intent(EnterActivity.this, MainActivity.class);
            } else {
                // Se l'utente è uscito dall'app quando era fuori da una casa: vado alla UserProfileActivity
                i = new Intent(EnterActivity.this, UserProfileActivity.class);
            }
            startActivity(i);
            finish();
        }
        else {
            /*
            Se non c'è un utente loggato, ripulisco l'oggetto Appartment.
            Questa invocazione ha due effetti:
            - se è stata appena aperta l'app in una situazione in cui non c'è nessun utente loggato,
              viene creata l'istanza di Appartment che verrà poi usata in tutto il resto
              dell'applicazione;
            - se si è giunti alla EnterActivity da un'altra activity dell'app e dunque a seguito
              di un logout, tutte le informazioni in esso presenti vengono cancellate come è giusto
              che sia.
             */
            Appartment.getInstance().clearAll();
        }
    }
}