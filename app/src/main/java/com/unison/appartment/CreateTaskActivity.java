package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.unison.appartment.model.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final static String DATE_PICKER_FRAGMENT_TAG = "datePickerFragment";

    // Date formatter utilizzato per formattare la deadline
    private DateFormat dateFormatter;

    private Toolbar toolbar;
    private EditText inputName;
    private EditText inputDescription;
    private EditText inputPoints;
    private EditText inputDeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        // Supporto per la toolbar
        toolbar = findViewById(R.id.activity_create_task_toolbar);
        setSupportActionBar(toolbar);
        // Gestione del click della freccia indietro presente sulla toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputName = findViewById(R.id.activity_create_task_input_name_value);
        inputDescription = findViewById(R.id.activity_create_task_input_description_value);
        inputPoints = findViewById(R.id.activity_create_task_input_points_value);

        // Mostro il date picker
        inputDeadline = findViewById(R.id.activity_create_task_input_deadline_value);
        inputDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        FloatingActionButton floatFinish = findViewById(R.id.activity_create_task_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        // Nota: dateFormatter non è static ed è inizializzato alla creazione dell'activity
        // perché il cambio di Locale è proprio una delle modifiche che possono causare il
        // riavvio dell'activity.
        this.dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        /*
        Se lo schermo è stato ruotato mentre il date picker era aperto, l'activity è stata distrutta
        e ora sta venendo ricreata. Si vuole mantenere aperto lo stesso date picker, a cui però
        deve essere cambiato il listener dal momento che altrimenti farebbe riferimento all'activity
        distrutta non più esistente.
         */
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DATE_PICKER_FRAGMENT_TAG);
        if (fragment != null) {
            ((DatePickerFragment) fragment).setListener(this);
        }
    }

    public void createTask() {
        Task newTask = new Task(
                inputName.getText().toString(),
                inputDescription.getText().toString(),
                inputDeadline.getText().toString(),
                Integer.valueOf(inputPoints.getText().toString())
        );
        Intent returnIntent = new Intent();
        returnIntent.putExtra("newTask", newTask);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void showDatePickerDialog() {
        int year, month, day;
        final Calendar cal = Calendar.getInstance();

        // Se la data non è ancora stata selezionata, uso la data corrente come quella di default nel date picker
        // (non faccio niente perché Calendar.getInstance() mi restituisce già la data corrente)

        // Altrimenti, uso la data precedentemente selezionata come quella di default
        if (inputDeadline.getText().length() != 0) {
            try {
                cal.setTime(dateFormatter.parse(inputDeadline.getText().toString()));
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerFragment.newInstance(year, month, day, this).show(getSupportFragmentManager(), DATE_PICKER_FRAGMENT_TAG);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        inputDeadline.setText(dateFormatter.format(cal.getTime()));
    }
}
