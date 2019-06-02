package com.unison.appartment.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.unison.appartment.R;
import com.unison.appartment.fragments.CompletionListFragment;
import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.DateUtils;

import java.util.Date;

public class CompletedTaskDetailActivity extends AppCompatActivity implements CompletionListFragment.OnCompletionListFragmentInteractionListener {

    private static final int ADD_TASK_REQUEST_CODE = 101;
    public final static int RESULT_OK = 200;
    public final static int RESULT_CREATED = 201;
    public final static int RESULT_NOT_CREATED = 202;

    private CompletedTask completedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task_detail);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_completed_task_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Carico il fragment contenente la cronologia dei completamenti
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_completed_task_detal_fragment_completion_list, CompletionListFragment.newInstance(1));
        ft.commit();

        Intent creationIntent = getIntent();
        completedTask = (CompletedTask) creationIntent.getSerializableExtra(DoneFragment.EXTRA_COMPLETEDTASK_OBJECT);
        // Imposto il nome del task visualizzato all'interno dello stato
        Appartment.getInstance().setCurrentCompletedTaskName(completedTask.getName());
        Log.d("STATO", Appartment.getInstance().getCurrentCompletedTaskName());

        TextView textName = findViewById(R.id.activity_completed_task_detail_name);
        TextView textPoints = findViewById(R.id.activity_completed_task_detail_points_value);
        TextView textDescription = findViewById(R.id.activity_completed_task_detail_text_description_value);
        TextView textCompletionDate = findViewById(R.id.activity_completed_task_detail_text_completion_date_value);

        textName.setText(completedTask.getName());
        textPoints.setText(String.valueOf(completedTask.getLastPoints()));
        textDescription.setText(completedTask.getLastDescription());
        textCompletionDate.setText(DateUtils.formatDateWithCurrentDefaultLocale(new Date(completedTask.getLastCompletionDate())));

        MaterialButton btnCreate = findViewById(R.id.activity_completed_task_detail_btn_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompletedTaskDetailActivity.this, CreateTaskActivity.class);
                i.putExtra(DoneFragment.EXTRA_COMPLETEDTASK_OBJECT, completedTask);
                startActivityForResult(i, ADD_TASK_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE) {
            Intent returnIntent = new Intent();
            if (resultCode == Activity.RESULT_OK) {
                returnIntent.putExtra(TodoFragment.EXTRA_NEW_TASK, data.getSerializableExtra(TodoFragment.EXTRA_NEW_TASK));
                setResult(RESULT_CREATED, returnIntent);
            } else {
                // Necessario impostare questo resultCode perché altrimenti il default è OK e non
                // riesco a capire cosa è successo
                setResult(RESULT_NOT_CREATED, returnIntent);
            }
            finish();
        }
    }

    @Override
    public void onRewardListElementsLoaded(long elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = findViewById(R.id.activity_completed_task_detail_progress);
        progressBar.setVisibility(View.GONE);
    }
}
