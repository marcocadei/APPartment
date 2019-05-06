package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.unison.appartment.model.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

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
        DialogFragment newFragment = new DatePickerFragment(inputDeadline);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private EditText textOutput;
        private DateFormat dateFormatter;

        public DatePickerFragment(EditText textOutput) {
            super();

            // Questo fragment sopravvive alla distruzione dell'activity in cui è inserito
            // (questa proprietà viene utilizzata per far sì che non venga distrutto alla rotazione dello schermo)
            setRetainInstance(true);

            this.textOutput = textOutput;

            // Date formatter usato per leggere e scrivere la data nel campo di testo
            this.dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            final Calendar cal = Calendar.getInstance();

            // Se la data non è ancora stata selezionata, uso la data corrente come quella di default nel date picker
            // (non faccio niente perché Calendar.getInstance() mi restituisce già la data corrente)

            // Altrimenti, uso la data precedentemente selezionata come quella di default
            if (textOutput.getText().length() != 0) {
                try {
                    cal.setTime(dateFormatter.parse(textOutput.getText().toString()));
                }
                catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),this, year, month, day);
        }

        // La data è stata impostata e la scrivo nel campo di testo
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            textOutput.setText(dateFormatter.format(cal.getTime()));
        }
    }
}
