package com.unison.appartment.repository;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.Appartment;
import com.unison.appartment.R;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TodoTaskRepository {

    // Nodo del database a cui sono interessato
    private DatabaseReference uncompletedTasksRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo Task
    private FirebaseQueryLiveData liveData;
    private LiveData<List<Task>> taskLiveData;

    public TodoTaskRepository() {
        Resources res = Appartment.getInstance().getContext().getResources();
        String separator = res.getString(R.string.db_separator);
        String uncompletedTasks = res.getString(R.string.db_uncompleted_tasks);
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        uncompletedTasksRef =
                FirebaseDatabase.getInstance().getReference(separator + uncompletedTasks + separator + Appartment.getInstance().getHome());
        Query orderedTasks = uncompletedTasksRef.orderByChild(res.getString(R.string.db_uncompleted_tasks_homename_taskid_creationdate));
        liveData = new FirebaseQueryLiveData(orderedTasks);
        taskLiveData = Transformations.map(liveData, new TodoTaskRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<Task>> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(Task newTask) {
        String key = uncompletedTasksRef.push().getKey();
        newTask.setId(key);
        uncompletedTasksRef.child(key).setValue(newTask);
    }

    private class Deserializer implements Function<DataSnapshot, List<Task>> {
        @Override
        public List<Task> apply(DataSnapshot dataSnapshot) {
            List<Task> tasks = new ArrayList<>();
            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                Task newTask = taskSnapshot.getValue(Task.class);
                newTask.setId(taskSnapshot.getKey());
                tasks.add(newTask);
            }
            return tasks;
        }
    }
}
