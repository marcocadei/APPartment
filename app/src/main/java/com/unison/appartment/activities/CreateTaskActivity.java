package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.R;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.fragments.UserPickerFragment;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.UncompletedTask;

/**
 * Classe che rappresenta l'Activity per creare un nuovo UncompletedTask
 */
public class CreateTaskActivity extends AppCompatActivity implements UserPickerFragment.OnUserPickerFragmentInteractionListener {

    private final static String BUNDLE_KEY_ASSIGNED_USER_ID = "assignedUserId";
    private final static String BUNDLE_KEY_ASSIGNED_USER_NAME = "assignedUserName";

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;
    private EditText inputAssignedUser;

    private String assignedUserId;
    private String assignedUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // Supporto per la toolbar
        Toolbar toolbar = findViewById(R.id.activity_create_task_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputName = findViewById(R.id.activity_create_task_input_name_value);
        inputDescription = findViewById(R.id.activity_create_task_input_description_value);
        inputPoints = findViewById(R.id.activity_create_task_input_points_value);
        inputAssignedUser = findViewById(R.id.activity_create_task_input_assigned_user_value);

        inputAssignedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPickerFragment.newInstance().show(getSupportFragmentManager(), UserPickerFragment.TAG_USER_PICKER);
            }
        });

        FloatingActionButton floatFinish = findViewById(R.id.activity_create_task_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
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

    public void createTask() {
        UncompletedTask newUncompletedTask = new UncompletedTask(
                inputName.getText().toString(),
                inputDescription.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString()),
                System.currentTimeMillis(), // La data viene salvata in un formato indipendente dalla lingua utilizzata nel device
                assignedUserId,
                assignedUserName,
                false
        );
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
