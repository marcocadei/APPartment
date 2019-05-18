package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.fragments.HomeListFragment;
import com.unison.appartment.R;
import com.unison.appartment.model.UserHome;

/**
 * Classe che rappresenta l'Activity per visualizzare il profilo dell'utente e la lista di case
 * in cui lo stesso è presente
 */
public class UserProfileActivity extends AppCompatActivity implements HomeListFragment.OnHomeListFragmentInteractionListener {

    private View emptyListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Supporto per la toolbar
        Toolbar toolbar = findViewById(R.id.activity_user_profile_toolbar);
        setSupportActionBar(toolbar);

        emptyListLayout = findViewById(R.id.activity_user_profile_layout_empty_list);

        // TODO riempire i campi di testo con i dati dell'utente loggato

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_profile_toolbar, menu);
        return true;
    }

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

    @Override
    public void onHomeListFragmentInteraction(UserHome item) {
        /*
        Quando l'utente seleziona una voce dalla lista delle case, deve essere portato alla
        MainActivity della casa selezionata.
         */

        Appartment.getInstance().setHome(item.getHomename());
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onHomeListElementsLoaded(long elements) {
        /*
        Quando viene completato il caricamento, la progress bar viene nascosta e se la lista ha
        0 elementi viene mostrato un apposito messaggio.
         */

        // Sia che l'utente abbia delle case o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = findViewById(R.id.activity_user_profile_progress);
        progressBar.setVisibility(View.GONE);

        // Se gli elementi sono 0 allora mostro un testo che indichi all'utente l'assenza di case
        if (elements == 0) {
            emptyListLayout.setVisibility(View.VISIBLE);
        }
    }
}
