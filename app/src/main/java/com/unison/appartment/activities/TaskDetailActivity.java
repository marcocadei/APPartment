package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.fragments.UserPickerFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.DateUtils;

import java.util.Date;

/**
 * Classe che rappresenta l'Activity con il dettaglio del UncompletedTask
 */
public class TaskDetailActivity extends ActivityWithNetworkConnectionDialog implements UserPickerFragment.OnUserPickerFragmentInteractionListener {

    public final static String EXTRA_TASK_OBJECT = "taskObject";

    private final static String BUNDLE_KEY_TASK = "task";

    private final static int EDIT_TASK_REQUEST_CODE = 101;

    public final static int RESULT_OK = 200;
    public final static int RESULT_EDITED = 201;
    public final static int RESULT_NOT_EDITED = 202;

    private UncompletedTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_task_detail_toolbar);
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
        task = (UncompletedTask) creationIntent.getParcelableExtra(EXTRA_TASK_OBJECT);

        TextView textName = findViewById(R.id.activity_task_detail_name);
        TextView textPoints = findViewById(R.id.activity_task_detail_points_value);
        TextView textDescription = findViewById(R.id.activity_task_detail_text_description_value);
        TextView textCreationDate = findViewById(R.id.activity_task_detail_text_creation_date_value);

        textName.setText(task.getName());
        textPoints.setText(String.valueOf(task.getPoints()));
        textDescription.setText(task.getDescription());
        textCreationDate.setText(DateUtils.formatDateWithCurrentDefaultLocale(new Date(task.getCreationDate())));

        if (task.isAssigned()) {
            TextView textAssignedUserTitle = findViewById(R.id.activity_task_detail_text_assigned_user_title);
            TextView textAssignedUser = findViewById(R.id.activity_task_detail_text_assigned_user_value);
            textAssignedUserTitle.setVisibility(View.VISIBLE);
            textAssignedUser.setVisibility(View.VISIBLE);
            textAssignedUser.setText(task.getAssignedUserName());
        }

        MaterialButton btnComplete = findViewById(R.id.activity_task_detail_btn_complete);
        MaterialButton btnAssign = findViewById(R.id.activity_task_detail_btn_assign);

        final String userId = new FirebaseAuth().getCurrentUserUid();

        if (Appartment.getInstance().getHomeUser(userId).getRole() == Home.ROLE_SLAVE) {
            /*
            Se l'utente è uno slave, vengono visualizzati due bottoni:
            - il bottone per assegnare il task a quell'utente;
            - il bottone per marcare il compito come completato.
             */

            if (task.isAssigned()) {
                if (task.getAssignedUserId().equals(userId)) {
                    if (task.isMarked()) {
                        /*
                        [Caso A] Il task è assegnato all'utente loggato ed è marcato come completato:
                        in questo caso viene visualizzato solo un bottone disabilitato che dice che
                        il task è già stato marcato come completato (non si può fare niente).
                         */
                        btnComplete.setEnabled(false);
                        btnComplete.setText(R.string.activity_task_detail_btn_complete_task_marked);
                        btnAssign.setVisibility(View.GONE);
                    }
                    else {
                        /*
                        [Caso B] Il task è assegnato all'utente loggato ma non è ancora marcato come
                        completato: in questo caso vengono visualizzati entrambi i bottoni assign
                        e complete, l'utente ha la possibilità di marcare il task come completato o
                        di disassegnarselo.
                         */
                        btnComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendMarkData(userId);
                            }
                        });
                        btnAssign.setText(R.string.activity_task_detail_btn_unassign);
                        btnAssign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendRemoveAssignmentData();
                            }
                        });
                    }
                }
                else {
                    /*
                    [Caso C] Il task è assegnato ad un utente diverso da quello loggato (a prescindere
                    che sia marcato come completato o meno): in questo caso l'utente vede soltanto
                    un bottone disabilitato e non può fare niente.
                     */
                    btnComplete.setVisibility(View.GONE);
                    btnAssign.setEnabled(false);
                    btnAssign.setText(R.string.activity_task_detail_btn_complete_task_already_assigned);
                }
            }
            else {
                /*
                [Caso D] Il task non è ancora assegnato: in questo caso all'utente vengono mostrati
                entrambi i bottoni assign e complete, l'utente ha la possibilità di marcare il task
                come completato o di assegnarselo.
                 */
                btnAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendAssignData(userId);
                    }
                });
                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMarkData(userId);
                    }
                });
            }
        }
        else {
            /*
            Se l'utente è un master, vengono visualizzati anche i bottoni per l'eliminazione del task
            e la conferma/annullamento delle richieste di completamento provenienti dagli slave.
             */
            MaterialButton btnConfirm = findViewById(R.id.activity_task_detail_btn_confirm_completion);
            MaterialButton btnCancel = findViewById(R.id.activity_task_detail_btn_cancel_completion);
            MaterialButton btnSwitch = findViewById(R.id.activity_task_detail_btn_switch);
            MaterialButton btnDelete = findViewById(R.id.activity_task_detail_btn_delete);

            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDeleteData();
                }
            });

            if (task.isAssigned()) {
                if (task.getAssignedUserId().equals(userId) && !task.isMarked()) {
                    /*
                    [Caso B] Il task è assegnato al master loggato (e non è marcato come completato,
                    dal momento che questo stato non ha senso per i master): in questo caso vengono
                    visualizzati entrambi i bottoni assign e complete, il master ha la possibilità
                    di completare il task o di disassegnarselo.
                     */
                    btnComplete.setText(R.string.activity_task_detail_btn_complete_task_available_masters);
                    btnComplete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendCompletionData(userId);
                        }
                    });
                    btnAssign.setText(R.string.activity_task_detail_btn_unassign);
                    btnAssign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRemoveAssignmentData();
                        }
                    });
                    btnSwitch.setVisibility(View.VISIBLE);
                    btnSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserPickerFragment.newInstance().show(getSupportFragmentManager(), UserPickerFragment.TAG_USER_PICKER);
                        }
                    });
                }
                else {
                    if (task.isMarked()) {
                        /*
                        [Caso C1] Il task è assegnato ad un utente diverso da quello loggato ed è marcato
                        come completato: in questo caso il master vede i bottoni per annullare o
                        confermare la richiesta di completamento.
                         */
                        btnComplete.setVisibility(View.GONE);
                        btnAssign.setVisibility(View.GONE);
                        btnConfirm.setVisibility(View.VISIBLE);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendCompletionData(task.getAssignedUserId());
                            }
                        });
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendUnmarkData();
                            }
                        });
                    }
                    else {
                        /*
                        [Caso C2] Il task è assegnato ad un utente diverso da quello loggato e non è marcato
                        come completato: in questo caso il master vede soltanto il bottone per eseguire
                        il disassegnamento.
                         */
                        btnComplete.setVisibility(View.GONE);
                        btnAssign.setText(R.string.activity_task_detail_btn_unassign);
                        btnAssign.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendRemoveAssignmentData();
                            }
                        });
                        btnSwitch.setVisibility(View.VISIBLE);
                        btnSwitch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserPickerFragment.newInstance().show(getSupportFragmentManager(), UserPickerFragment.TAG_USER_PICKER);
                            }
                        });
                    }
                }
            }
            else {
                /*
                [Caso D] Il task non è ancora assegnato: in questo caso vengono visualizzati
                entrambi i bottoni assign e complete, il master ha la possibilità di completare lui
                stesso il task oppure di assegnarlo ad utente qualunque.
                 */
                btnAssign.setText(R.string.activity_task_detail_btn_assign_masters);
                btnAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserPickerFragment.newInstance().show(getSupportFragmentManager(), UserPickerFragment.TAG_USER_PICKER);
                    }
                });
                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // In questo caso devo fare sia l'assegnamento (a me stesso) che il completamento
                        // In realtà mi basta fare il completamento normale perché:
                        // -> uncompleted tasks viene eliminato e quindi non conta in che stato era
                        // -> le stat in home-users sono aggiornate correttamente
                        // -> completed-tasks contiene solo le informazioni sul task e non sugli utenti
                        // -> completions vuole sapere chi ha completato il task e quindi sarebbe necessario
                        //    assegnarlo, ma in realtà nel repository io recupero il nickname dell'utente
                        //    a partire dalle informazioni nello stato e lo userId
                        sendCompletionData(userId);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(BUNDLE_KEY_TASK, task);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        task = savedInstanceState.getParcelable(BUNDLE_KEY_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        L'options menù contiene il solo tasto di modifica, che è visualizzato solo se sono soddisfatte
        entrambe le seguenti condizioni:
        - l'utente loggato è un master o il proprietario della casa;
        - il task di cui sono visualizzati i dettagli non è ancora stato marcato come completato
         */
        if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() != Home.ROLE_SLAVE && !task.isMarked()) {
            getMenuInflater().inflate(R.menu.activity_task_detail_toolbar, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_task_detail_toolbar_edit) {
            Intent i = new Intent(this, CreateTaskActivity.class);
            i.putExtra(CreateTaskActivity.EXTRA_EDIT_TASK_DATA, task);
            startActivityForResult(i, EDIT_TASK_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TASK_REQUEST_CODE) {
            Intent returnIntent = new Intent();
            if (resultCode == Activity.RESULT_OK) {
                returnIntent.putExtra(TodoFragment.EXTRA_NEW_TASK, data.getParcelableExtra(TodoFragment.EXTRA_NEW_TASK));
                setResult(RESULT_EDITED, returnIntent);
            } else {
                // Necessario impostare questo resultCode perché altrimenti il default è OK e non
                // riesco a capire cosa è successo
                setResult(RESULT_NOT_EDITED, returnIntent);
            }
            finish();
        }
    }

    private void sendAssignData(String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_ASSIGN);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK_DATA, task);
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(TodoFragment.EXTRA_USER_NAME, Appartment.getInstance().getHomeUser(userId).getNickname());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendRemoveAssignmentData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_REMOVE_ASSIGNMENT);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK_ID, task.getId());
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, task.getAssignedUserId());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendMarkData(String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_MARK_AS_COMPLETED);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK_ID, task.getId());
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(TodoFragment.EXTRA_USER_NAME, Appartment.getInstance().getHomeUser(userId).getNickname());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendUnmarkData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_UNMARK);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK_ID, task.getId());
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, task.getAssignedUserId());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendDeleteData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_DELETE);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK_ID, task.getId());
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, task.getAssignedUserId());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendCompletionData(String assignedUserId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_OPERATION_TYPE, TodoFragment.OPERATION_CONFIRM_COMPLETION);
        returnIntent.putExtra(TodoFragment.EXTRA_TASK, task);
        returnIntent.putExtra(TodoFragment.EXTRA_USER_ID, assignedUserId);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onListFragmentInteraction(HomeUser item) {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(UserPickerFragment.TAG_USER_PICKER);
        try {
            fragment.dismiss();
            sendAssignData(item.getUserId());
        }
        catch (NullPointerException e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }
    }
}
