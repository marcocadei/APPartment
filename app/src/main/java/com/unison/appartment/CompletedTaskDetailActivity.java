package com.unison.appartment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.model.CompletedTask;

public class CompletedTaskDetailActivity extends AppCompatActivity {

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

        Intent creationIntent = getIntent();
        completedTask = (CompletedTask) creationIntent.getSerializableExtra(DoneFragment.EXTRA_COMPLETEDTASK_OBJECT);

    }
}
