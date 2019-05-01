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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.Task;

import java.util.Calendar;

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

        // Prova DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("appartment-unison");
        ref.setValue(newTask);

        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(inputDeadline);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private EditText textOutput;

        public DatePickerFragment(EditText textOutput) {
            super();
            this.textOutput = textOutput;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Uso la data corrente come quella di default nel date picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.DialogTheme,this, year, month, day);
        }

        // La data è stata impostata e la scrivo nel campo di testo
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            textOutput.setText(String.format(getString(R.string.activity_create_task_input_deadline_value)
                                        , dayOfMonth, month + 1, year));
        }
    }
}
