package com.unison.appartment.activities;

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

import java.util.Locale;

/**
 * Classe che rappresenta l'Activity con il dettaglio del Reward
 */
public class RewardDetailActivity extends AppCompatActivity {

    private static final int EDIT_REWARD_REQUEST_CODE = 101;

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
        final Reward reward = (Reward) creationIntent.getSerializableExtra(RewardsFragment.EXTRA_REWARD_OBJECT);

        TextView textName = findViewById(R.id.activity_reward_detail_text_name);
        TextView textDescription = findViewById(R.id.activity_reward_detail_text_description_value);
        TextView textPoints = findViewById(R.id.activity_reward_detail_text_points_value);

        textName.setText(reward.getName());
        textPoints.setText(String.format(Locale.getDefault(), "%d", reward.getPoints()));
        // Viene gestito il caso in cui la descrizione sia vuota
        String shownDescription = reward.getDescription();
        if (shownDescription == null || shownDescription.isEmpty()) {
            shownDescription = getString(R.string.general_no_description_available);
            textDescription.setTypeface(null, Typeface.ITALIC);
        }
        textDescription.setText(shownDescription);

        if (reward.isRequested()) {
            TextView textReservationTitle = findViewById(R.id.activity_reward_detail_text_reservation_title);
            TextView textReservation = findViewById(R.id.activity_reward_detail_text_reservation_value);
            textReservationTitle.setVisibility(View.VISIBLE);
            textReservation.setVisibility(View.VISIBLE);
            textReservation.setText(reward.getReservationName());
        }

        MaterialButton btnReserve = findViewById(R.id.activity_reward_detail_btn_reserve);

        final String userId = new FirebaseAuth().getCurrentUserUid();

        if (Appartment.getInstance().getHomeUser().getRole() == Home.ROLE_SLAVE) {
            /*
            Se l'utente è uno slave, l'unico bottone che viene visualizzato è quello per richiedere il
            premio (disabilitato se il premio se è già stato richiesto).
             */

            if (reward.isRequested()) {
                btnReserve.setEnabled(false);
                if (reward.getReservationId().equals(userId)) {
                    btnReserve.setText(getString(R.string.activity_reward_detail_btn_reserve_reward_requested));
                } else {
                    btnReserve.setText(getString(R.string.activity_reward_detail_btn_reserve_reward_unavailable));
                }
            } else {
                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Appartment.getInstance().getHomeUser().getPoints() < reward.getPoints()) {
                            Snackbar.make(findViewById(R.id.activity_reward_detail),
                                    getString(R.string.error_not_enough_points),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            sendMakeRequestData(reward, userId);
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
            MaterialButton btnCancel = findViewById(R.id.activity_reward_detail_btn_cancel_reservation);
            MaterialButton btnDelete = findViewById(R.id.activity_reward_detail_btn_delete);

            btnDelete.setVisibility(View.VISIBLE);
            if (reward.isRequested()) {
                btnReserve.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendConfirmRequestData(reward, reward.getReservationId());
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCancelRequestData(reward);
                    }
                });
            } else {
                btnReserve.setText(getString(R.string.activity_reward_detail_btn_reserve_reward_available_masters));
                btnReserve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Appartment.getInstance().getHomeUser().getPoints() < reward.getPoints()) {
                            Snackbar.make(findViewById(R.id.activity_reward_detail),
                                    getString(R.string.error_not_enough_points),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else {
                            sendConfirmRequestData(reward, userId);
                        }
                    }
                });
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteData(reward);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Appartment.getInstance().getHomeUser().getRole() != Home.ROLE_SLAVE) {
            getMenuInflater().inflate(R.menu.activity_reward_detail_toolbar, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Appartment.getInstance().getHomeUser().getRole() != Home.ROLE_SLAVE) {
            if (item.getItemId() == R.id.activity_reward_detail_toolbar_edit) {
                Intent i = new Intent(this, CreateRewardActivity.class);
                startActivityForResult(i, EDIT_REWARD_REQUEST_CODE);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendConfirmRequestData(Reward reward, String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_CONFIRM);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_ID, reward.getId());
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_ID, userId);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void sendMakeRequestData(Reward reward, String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_RESERVE);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_ID, reward.getId());
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(RewardsFragment.EXTRA_USER_NAME, Appartment.getInstance().getUser().getName());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void sendCancelRequestData(Reward reward) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_CANCEL);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_ID, reward.getId());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void sendDeleteData(Reward reward) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RewardsFragment.EXTRA_OPERATION_TYPE, RewardsFragment.OPERATION_DELETE);
        returnIntent.putExtra(RewardsFragment.EXTRA_REWARD_ID, reward.getId());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
