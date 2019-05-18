package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.Appartment;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.UncompletedTask;
import java.util.ArrayList;
import java.util.List;


public class TodoTaskRepository {

    // Nodo del database a cui sono interessato
    private DatabaseReference uncompletedTasksRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<UncompletedTask>> taskLiveData;

    public TodoTaskRepository() {
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        uncompletedTasksRef =
                FirebaseDatabase.getInstance().getReference(
                        DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETED_TASKS +
                              DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome());
        Query orderedTasks = uncompletedTasksRef.orderByChild(DatabaseConstants.UNCOMPLETED_TASKS_HOMENAME_TASKID_CREATIONDATE);
        liveData = new FirebaseQueryLiveData(orderedTasks);
        taskLiveData = Transformations.map(liveData, new TodoTaskRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<UncompletedTask>> getTaskLiveData() {
        return taskLiveData;
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        String key = uncompletedTasksRef.push().getKey();
        newUncompletedTask.setId(key);
        uncompletedTasksRef.child(key).setValue(newUncompletedTask);
    }

    private class Deserializer implements Function<DataSnapshot, List<UncompletedTask>> {
        @Override
        public List<UncompletedTask> apply(DataSnapshot dataSnapshot) {
            List<UncompletedTask> uncompletedTasks = new ArrayList<>();
            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                UncompletedTask newUncompletedTask = taskSnapshot.getValue(UncompletedTask.class);
                newUncompletedTask.setId(taskSnapshot.getKey());
                uncompletedTasks.add(newUncompletedTask);
            }
            return uncompletedTasks;
        }
    }
}
