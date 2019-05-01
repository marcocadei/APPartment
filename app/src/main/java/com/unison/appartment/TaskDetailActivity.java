package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.unison.appartment.model.Task;

public class TaskDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        // Supporto per la toolbar
        toolbar = findViewById(R.id.activity_task_detail_toolbar);
        setSupportActionBar(toolbar);
        // Gestione del click della freccia indietro presente sulla toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Recupero il riferimento agli elementi dell'interfaccia
        TextView name = findViewById(R.id.activity_task_detail_name);
        TextView points = findViewById(R.id.activity_task_detail_points_value);
        TextView description = findViewById(R.id.activity_task_detail_description);
        TextView deadline = findViewById(R.id.activity_task_detail_deadline);


        Intent i = getIntent();
        Task task = (Task) i.getSerializableExtra("task");
        // Popolo l'interfaccia con i dati del task ricevuto
        name.setText(task.getName());
        points.setText(String.valueOf(task.getPoints()));
        description.setText(task.getDescription());
        deadline.setText(String.format(getString(R.string.activity_task_detail_deadline), task.getDeadline()));

    }
}
