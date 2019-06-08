package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.R;
import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.fragments.UserPickerFragment;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.utils.KeyboardUtils;

/**
 * Classe che rappresenta l'Activity per creare un nuovo UncompletedTask
 */
public class CreateTaskActivity extends FormActivity implements UserPickerFragment.OnUserPickerFragmentInteractionListener {

    public final static String EXTRA_TASK_DATA = "taskData";
    public final static String EXTRA_EDIT_TASK_DATA = "editTaskData";

    private final static String BUNDLE_KEY_ASSIGNED_USER_ID = "assignedUserId";
    private final static String BUNDLE_KEY_ASSIGNED_USER_NAME = "assignedUserName";

    private TextView txtTitle;
    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;
    private EditText inputAssignedUser;
    private TextInputLayout layoutName;
    private TextInputLayout layoutDescription;
    private TextInputLayout layoutPoints;

    private String assignedUserId;
    private String assignedUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
        this.errorDialogDestinationActivity = MainActivity.class;

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_create_task_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtTitle = findViewById(R.id.activity_create_task_text_title);
        inputName = findViewById(R.id.activity_create_task_input_name_value);
        inputDescription = findViewById(R.id.activity_create_task_input_description_value);
        inputPoints = findViewById(R.id.activity_create_task_input_points_value);
        inputAssignedUser = findViewById(R.id.activity_create_task_input_assigned_user_value);
        layoutName = findViewById(R.id.activity_create_task_input_name);
        layoutDescription = findViewById(R.id.activity_create_task_input_description);
        layoutPoints = findViewById(R.id.activity_create_task_input_points);

        // Se provengo dall'activity di dettaglio di un'attività completata allora ho già delle informazioni
        Intent creationIntent = getIntent();
        CompletedTask completedTask = (CompletedTask) creationIntent.getSerializableExtra(EXTRA_TASK_DATA);
        if (completedTask != null) {
            inputName.setText(completedTask.getName());
            inputDescription.setText(completedTask.getLastDescription());
            inputPoints.setText(String.valueOf(completedTask.getLastPoints()));
        }

        final UncompletedTask uncompletedTask = (UncompletedTask) creationIntent.getSerializableExtra(EXTRA_EDIT_TASK_DATA);
        if (uncompletedTask != null) {
            // Imposto il titolo opportunamente se devo modificare e non creare un premio
            toolbar.setTitle(R.string.activity_create_task_title_edit);
            txtTitle.setText(R.string.activity_create_task_title_edit);
            inputName.setText(uncompletedTask.getName());
            inputDescription.setText(uncompletedTask.getDescription());
            inputPoints.setText(String.valueOf(uncompletedTask.getPoints()));
            // Quando modifico un task non do la possibilità di cambiare anche l'assegnamento perché
            // questo può già essere cambiato nella schermata di dettaglio, tramite i bottoni messi
            // a disposizione
            inputAssignedUser.setVisibility(View.GONE);
        }

        inputAssignedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPickerFragment.newInstance().show(getSupportFragmentManager(), UserPickerFragment.TAG_USER_PICKER);
            }
        });

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutName);
            }
        });
        inputDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutDescription);
            }
        });
        inputPoints.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutPoints);
            }
        });

        FloatingActionButton floatFinish = findViewById(R.id.activity_create_task_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(CreateTaskActivity.this);
                if (checkInput()) {
                    createTask(uncompletedTask);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(BUNDLE_KEY_ASSIGNED_USER_ID, assignedUserId);
        outState.putString(BUNDLE_KEY_ASSIGNED_USER_NAME, assignedUserName);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        assignedUserId = savedInstanceState.getString(BUNDLE_KEY_ASSIGNED_USER_ID);
        assignedUserName = savedInstanceState.getString(BUNDLE_KEY_ASSIGNED_USER_NAME);
    }

    @Override
    protected boolean checkInput() {
        resetErrorMessage(layoutName);
        resetErrorMessage(layoutDescription);
        resetErrorMessage(layoutPoints);

        inputName.setText(inputName.getText().toString().trim());
        inputDescription.setText(inputDescription.getText().toString().trim());

        String nameValue = inputName.getText().toString();
        String descriptionValue = inputDescription.getText().toString();
        String pointsValue = inputPoints.getText().toString();

        boolean result = true;

        // Controllo che i campi non siano stati lasciati vuoti
        if (nameValue.trim().length() == 0) {
            layoutName.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (descriptionValue.trim().length() == 0) {
            layoutDescription.setError(getString(R.string.form_error_missing_value));
            result = false;
        }
        if (pointsValue.trim().length() == 0) {
            layoutPoints.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        // Controllo che il nome del task non contenga un carattere non ammesso
        // (Il nome del task è usato come nome di un nodo in Firebase e lì alcuni caratteri non sono ammessi)
        if (nameValue.matches(".*[.$\\[\\]#/].*")) {
            layoutName.setError(null);
            layoutName.setError(getString(R.string.form_error_invalid_characters));
            result = false;
        }

        return result;
    }

    public void createTask(UncompletedTask uncompletedTask) {
        UncompletedTask newUncompletedTask;
        // Se sto modificando il task allora ho anche il campo id, che voglio mantenere uguale
        if (uncompletedTask != null) {
            newUncompletedTask = new UncompletedTask(
                    uncompletedTask.getId(),
                    inputName.getText().toString(),
                    inputDescription.getText().toString(),
                    Integer.valueOf(inputPoints.getText().toString()),
                    uncompletedTask.getCreationDate(),
                    uncompletedTask.getAssignedUserId(),
                    uncompletedTask.getAssignedUserName(),
                    uncompletedTask.isMarked()
            );
        } else {
            newUncompletedTask = new UncompletedTask(
                    inputName.getText().toString(),
                    inputDescription.getText().toString(),
                    Integer.valueOf(inputPoints.getText().toString()),
                    System.currentTimeMillis(), // La data viene salvata in un formato indipendente dalla lingua utilizzata nel device
                    assignedUserId,
                    assignedUserName,
                    false
            );
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(TodoFragment.EXTRA_NEW_TASK, newUncompletedTask);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onListFragmentInteraction(HomeUser item) {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(UserPickerFragment.TAG_USER_PICKER);
        if (fragment != null) {
            fragment.dismiss();
            assignedUserId = item.getUserId();
            assignedUserName = item.getNickname();
            inputAssignedUser.setText(assignedUserName);
        }
    }
}
