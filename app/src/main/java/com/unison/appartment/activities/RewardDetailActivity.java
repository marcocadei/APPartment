package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.Reward;
import com.unison.appartment.state.Appartment;

/**
 * Classe che rappresenta l'Activity con il dettaglio del Reward
 */
public class RewardDetailActivity extends ActivityWithNetworkConnectionDialog {

    public final static String EXTRA_REWARD_OBJECT = "rewardObject";

    private final static String BUNDLE_KEY_REWARD = "reward";

    private final static int EDIT_REWARD_REQUEST_CODE = 101;

    public final static int RESULT_OK = 200;
    public final static int RESULT_EDITED = 201;
    public final static int RESULT_NOT_EDITED = 202;

    private Reward reward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_reward_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        Impostazione del contenuto delle TextView in base al premio per il quale
        è costruita l'activity.
         */
        Intent creationIntent = getIntent();
        reward = (Reward) creationIntent.getSerializableExtra(EXTRA_REWARD_OBJECT);

        TextView textName = findViewById(R.id.activity_reward_detail_text_name);
        TextView textDescription = findViewById(R.id.activity_reward_detail_text_description_value);
        TextView textPoints = findViewById(R.id.activity_reward_detail_text_points_value);

        textName.setText(reward.getName());
        textPoints.setText(String.valueOf(reward.getPoints()));
        textDescription.setText(reward.getDescription());

        /* Vecchia gestione della descrizione (che poteva essere facoltativa)

        // Viene gestito il caso in cui la descrizione sia vuota
        String shownDescription = reward.getDescription();
        if (shownDescription == null || shownDescription.isEmpty()) {
            shownDescription = getString(R.string.general_no_description_available);
            textDescription.setTypeface(null, Typeface.ITALIC);
        }
        textDescription.setText(shownDescription);
        */

        if (reward.isRequested()) {
            TextView textReservationTitle = findViewById(R.id.activity_reward_detail_text_reservation_title);
            TextView textReservation = findViewById(R.id.activity_reward_detail_text_reservation_value);
            textReservationTitle.setVisibility(View.VISIBLE);
            textReservation.setVisibility(View.VISIBLE);
            textReservation.setText(reward.getReservationName());
        }

        MaterialButton btnReserve = findViewById(R.id.activity_reward_detail_btn_reserve);
        MaterialButton btnCancel = findViewById(R.id.activity_reward_detail_btn_cancel_reservation);

        final String userId = new FirebaseAuth().getCurrentUserUid();

        /*
        Quando il btnCancel è visualizzato, deve fare sempre la stessa azione a prescindere dal
        ruolo dell'utente loggato.
         */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCancelRequestData();
            }
        });

        if (Appartment.getInstance().getHomeUser(userId).getRole() == Home.ROLE_SLAVE) {
            /*
            Se l'utente è uno slave, l'unico bottone che viene visualizzato è quello per richiedere il
            premio (disabilitato se il premio se è già stato richiesto).
             */

            if (reward.isRequested()) {
                if (reward.getReservationId().equals(userId)) {
                    btnReserve.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                } else {
                    btnReserve.setEnabled(false);
                    btnReserve.setText(R.string.activity_reward_detail_btn_reserve_reward_unavailable);
                }
            } else {
                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Appartment.getInstance().getHomeUser(userId).getPoints() < reward.getPoints()) {
                            Snackbar.make(findViewById(R.id.activity_reward_detail),
                                    getString(R.string.error_not_enough_points),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            sendMakeRequestData(userId);
                        }

                    }
                });
            }
        } else {
            /*
            Se l'utente è un master o un owner:
            - viene visualizzato il bottone per l'eliminazione del premio;
            - se il premio è stato richiesto da uno slave, vengono visualizzati i bottoni per confermare
              o rifiutare la richiesta di premio;
            - se il premio è ancora disponibile, viene modificato il testo del bottone di richiesta.
             */
            MaterialButton btnConfirm = findViewById(R.id.activity_reward_detail_btn_confirm_reservation);
            MaterialButton btnDelete = findViewById(R.id.activity_reward_detail_btn_delete);
            TextView textInfo = findViewById(R.id.activity_reward_detail_text_info);

            textInfo.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteData();
                }
            });

            if (reward.isRequested()) {
                btnReserve.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendConfirmRequestData(reward.getReservationId());
                    }
                });
            } else {
                btnReserve.setText(R.string.activity_reward_detail_btn_reserve_reward_available_masters);
                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Appartment.getInstance().getHomeUser(userId).getPoints() < reward.getPoints()) {
                            Snackbar.make(findViewById(R.id.activity_reward_detail),
                                    getString(R.string.error_not_enough_points),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            sendConfirmRequestData(userId);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_REWARD, reward);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        reward = (Reward) savedInstanceState.getSerializable(BUNDLE_KEY_REWARD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        L'options menù contiene il solo tasto di modifica, che è visualizzato solo se sono soddisfatte
        entrambe le seguenti condizioni:
        - l'utente loggato è un master o il proprietario della casa;
        - il premio di cui sono visualizzati i dettagli non è ancora stato richiesto da nessuno
         */
        if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() != Home.ROLE_SLAVE && !reward.isRequested()) {
            getMenuInflater().inflate(R.menu.activity_reward_detail_toolbar, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() != Home.ROLE_SLAVE) {
            if (item.getItemId() == R.id.activity_reward_detail_toolbar_edit) {
                Intent i = new Intent(this, CreateRewardActivity.class);
                i.putExtra(CreateRewardActivity.EXTRA_REWARD_DATA, reward);
                startActivityForResult(i, EDIT_REWARD_REQUEST_CODE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REWARD_REQUEST_CODE) {
            Intent returnIntent = new Intent();
            if (resultCode == Activity.RESULT_OK) {
                returnIntent.putExtra(RewardsFragment.EXTRA_NEW_REWARD, data.getSerializableExtra(RewardsFragment.EXTRA_NEW_REWARD));
                setResult(RESULT_EDITED, returnIntent);
            } else {
                // Necessario impostare questo resultCode perché altrimenti il default è OK e non
                // riesco a capire cosa è successo
                setResult(RESULT_NOT_EDITED, returnIntent);
            }
            finish();
        }
    }

    private void sendConfirmRequestData(String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_CONFIRM);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_DATA, reward);
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_NAME, Appartment.getInstance().getHomeUser(userId).getNickname());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendMakeRequestData(String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_RESERVE);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_DATA, reward);
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_NAME, Appartment.getInstance().getHomeUser(userId).getNickname());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendCancelRequestData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_CANCEL);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_DATA, reward);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendDeleteData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_DELETE);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_DATA, reward);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
