package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.unison.appartment.fragments.HomeListFragment;
import com.unison.appartment.R;
import com.unison.appartment.model.UserHome;

/**
 * Classe che rappresenta l'Activity per visualizzare il profilo dell'utente e la lista di case
 * in cui lo stesso è presente
 */
public class UserProfileActivity extends AppCompatActivity implements HomeListFragment.OnHomeListFragmentInteractionListener {

    private Toolbar toolbar;
    private TextView emptyHomeListTitle;
    private TextView emptyHomeListText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Supporto per la toolbar
        toolbar = findViewById(R.id.activity_user_profile_toolbar);
        setSupportActionBar(toolbar);

        emptyHomeListTitle = findViewById(R.id.activity_user_profile_empty_home_list_title);
        emptyHomeListText = findViewById(R.id.activity_user_profile_empty_home_list_text);

        MaterialButton btnJoin = findViewById(R.id.activity_user_profile_btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, JoinHomeActivity.class);
                startActivity(i);
            }
        });

        MaterialButton btnCreate = findViewById(R.id.activity_user_profile_btn_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfileActivity.this, CreateHomeActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Metodo per creare il menù presente sulla toolbar
     *
     * @param menu Il menù da aggiungere
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_profile_toolbar, menu);
        return true;
    }

    /**
     * Metodo per reagire alla selezione di una voce del menù della toolbar
     *
     * @param item L'elemento selezionato
     * @return True
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO ora è implementato soltanto il logout
        switch (item.getItemId()) {
            case R.id.activity_user_profile_toolbar_logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, EnterActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Lascia il backstack inalterato, ma mette tutte le attività in background, esattamente
     * come se l'utente avesse premuto il bottone home
     * 2° RISPOSTA SU:
     * https://stackoverflow.com/questions/8631095/how-to-prevent-going-back-to-the-previous-activity
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Questa Activity contiene il fragment HomeListFragment, quindi ne implementa i metodi del listener

    /**
     * Metodo che interviene al click dell'utente su una delle case della lista: l'utente deve
     * essere rediretto alla Main Activity della casa selezionata
     *
     * @param item L'oggetto UserHome rappresentante la relazione tra lo User e la casa selezionata
     */
    @Override
    public void onHomeListFragmentInteraction(UserHome item) {
        // TODO andare alla main activity della casa selezionata

        // (righe qui sotto solo temporanee, poi rifare meglio)
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_HOME_NAME, item.getHomeName());
        startActivity(i);
    }

    /**
     * Metodo che interviene al caricamento della lista di case: la progress bar deve scomparire e se
     * non c'è alcuna casa nella lista deve apparire un apposito messaggio
     *
     * @param elements Il numero di elementi nella lista
     */
    @Override
    public void onHomeListElementsLoaded(long elements) {
        // Sia che l'utente abbia delle case o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = findViewById(R.id.activity_user_profile_progress);
        progressBar.setVisibility(View.GONE);

        // Se gli elementi sono 0 allora mostro un testo che indichi all'utente l'assenza di case
        if (elements == 0) {
            emptyHomeListTitle.setVisibility(View.VISIBLE);
            emptyHomeListText.setVisibility(View.VISIBLE);
        }
    }
}
