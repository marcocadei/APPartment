package com.unison.appartment;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TodoTaskViewModel extends ViewModel {
    /*private static String separator = Resources.getSystem().getString(R.string.db_separator);
    private static String uncompletedTasks = Resources.getSystem().getString(R.string.db_uncompleted_tasks);*/
    private static final DatabaseReference UNCOMPLETED_TASKS_REF =
            FirebaseDatabase.getInstance().getReference("/uncompleted-tasks" + "/casatest1");

    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(UNCOMPLETED_TASKS_REF);

    private final LiveData<List<Task>> taskLiveData = Transformations.map(liveData, new Deserializer());

    private class Deserializer implements Function<DataSnapshot, List<Task>> {
        @Override
        public List<Task> apply(DataSnapshot dataSnapshot) {
            List<Task> tasks = new ArrayList<>();
            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                tasks.add(taskSnapshot.getValue(Task.class));
            }
            return tasks;
        }
    }

    @NonNull
    public LiveData<List<Task>> getTaskLiveData() {
        return taskLiveData;
    }
}
