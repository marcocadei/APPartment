package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.User;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.fragments.UserHomeListFragment;
import com.unison.appartment.R;
import com.unison.appartment.model.UserHome;

/**
 * Classe che rappresenta l'Activity per visualizzare il profilo dell'utente e la lista di case
 * in cui lo stesso è presente
 */
public class UserProfileActivity extends AppCompatActivity implements UserHomeListFragment.OnHomeListFragmentInteractionListener {

    private DatabaseReader databaseReader;

    FirebaseProgressDialogFragment progressDialog;

    private View emptyListLayout;
    private TextView textName;
    private TextView textEmail;
    private TextView textGender;
    private TextView textBirthdate;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        databaseReader = new FirebaseDatabaseReader();

        // Quando entro in quest activity devo dimenticarmi l'ultima casa in cui è entrato l'utente
        Appartment.getInstance().setHome(null);

        // Supporto per la toolbar
        Toolbar toolbar = findViewById(R.id.activity_user_profile_toolbar);
        setSupportActionBar(toolbar);

        emptyListLayout = findViewById(R.id.activity_user_profile_layout_empty_list);
        textName = findViewById(R.id.activity_user_profile_text_name_value);
        textEmail = findViewById(R.id.activity_user_profile_text_email_value);
        textGender = findViewById(R.id.activity_user_profile_text_gender_value);
        textBirthdate = findViewById(R.id.activity_user_profile_text_birthdate_value);
        imgProfile = findViewById(R.id.activity_user_profile_img_profile);

        // Carico i dati dell'utente loggato
        final User currentUser = Appartment.getInstance().getUser();
        textName.setText(currentUser.getName());
        textEmail.setText(currentUser.getEmail());
        textGender.setText(currentUser.getGenderString());
        textBirthdate.setText(currentUser.getBirthdate());
        if (currentUser.getImage() != null) {
            Glide.with(imgProfile.getContext()).load(currentUser.getImage()).placeholder(R.drawable.ic_person).apply(RequestOptions.circleCropTransform()).into(imgProfile);
        } else {
            Glide.with(imgProfile.getContext()).load(R.drawable.ic_person).apply(RequestOptions.circleCropTransform()).into(imgProfile);
        }

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

    // Questa Activity contiene il fragment UserHomeListFragment, quindi ne implementa i metodi del listener

    @Override
    public void onHomeListFragmentInteraction(UserHome item) {
        /*
        Quando l'utente selezione una voce dalla lista delle case, leggo l'oggetto Home corrispondente
        alla casa selezionata e vado nella MainActivity
         */
        progressDialog = FirebaseProgressDialogFragment.newInstance(
                getString(R.string.activity_user_profile_progress_title),
                getString(R.string.activity_user_profile_progress_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
        // Devo popolare Appartment con User (c'è già), Home (scarico), UserHome (elemento selezionato dalla lista), HomeUser (scarico)
        Appartment.getInstance().setUserHome(item);
        databaseReader.retrieveHome(item.getHomename(), databaseReaderListener);
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

    private void moveToNextActivity() {
        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(i);
    }

    // Listener processo di lettura nel database della casa in cui si vuole entrare
    final DatabaseReaderListener databaseReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(Object object) {
            /*
            Quando l'utente seleziona una voce dalla lista delle case, deve essere portato alla
            MainActivity della casa selezionata.
            */
            Appartment.getInstance().setHome((Home) object);
            moveToNextActivity();
            progressDialog.dismiss();
        }

        @Override
        public void onReadEmpty() {
            // TODO Se si entra qui c'è un errore perché la casa è selezionata dalla lista e quindi deve esistere
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            // TODO Se si entra qui c'è un errore perché la casa è selezionata dalla lista e quindi deve esistere
        }
    };
}
