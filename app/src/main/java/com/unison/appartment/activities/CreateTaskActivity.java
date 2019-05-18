package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.unison.appartment.R;
import com.unison.appartment.model.UncompletedTask;

import java.util.Calendar;

/**
 * Classe che rappresenta l'Activity per creare un nuovo UncompletedTask
 */
public class CreateTaskActivity extends AppCompatActivity {

    public final static String EXTRA_NEW_TASK = "newTask";

    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;

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

        FloatingActionButton floatFinish = findViewById(R.id.activity_create_task_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        /*
        Se lo schermo è stato ruotato mentre il date picker era aperto, l'activity è stata distrutta
        e ora sta venendo ricreata. Si vuole mantenere aperto lo stesso date picker, a cui però
        deve essere cambiato il listener dal momento che altrimenti farebbe riferimento all'activity
        distrutta non più esistente.
         *//*
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DatePickerFragment.TAG_DATE_PICKER);
        if (fragment != null) {
            ((DatePickerFragment) fragment).setListener(this);
        }*/
    }

    public void createTask() {
        Calendar calendar = Calendar.getInstance();

        UncompletedTask newUncompletedTask = new UncompletedTask(
                inputName.getText().toString(),
                inputDescription.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString()),
                (-1) * calendar.getTimeInMillis() // La data viene salvata in un formato indipendente dalla lingua utilizzata nel device
        );
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_NEW_TASK, newUncompletedTask);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
