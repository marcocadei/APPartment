package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.List;
import androidx.arch.core.util.Function;

public class CompletedTaskRepository {
    // Nodo del database a cui sono interessato
    private DatabaseReference completedTaskRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<CompletedTask>> completedTaskLiveData;

    public CompletedTaskRepository() {
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        completedTaskRef =
                FirebaseDatabase.getInstance().getReference(
                        DatabaseConstants.SEPARATOR + DatabaseConstants.COMPLETEDTASKS +
                                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        Query orderedCompletedTasks = completedTaskRef.orderByChild(DatabaseConstants.COMPLETEDTASKS_HOMENAME_TASKID_NAME);
        liveData = new FirebaseQueryLiveData(orderedCompletedTasks);
        completedTaskLiveData = Transformations.map(liveData, new CompletedTaskRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<CompletedTask>> getCompletedTaskLiveData() {
        return completedTaskLiveData;
    }

    private class Deserializer implements Function<DataSnapshot, List<CompletedTask>> {
        @Override
        public List<CompletedTask> apply(DataSnapshot dataSnapshot) {
            List<CompletedTask> completedTasks = new ArrayList<>();
            for (DataSnapshot completedTaskSnapshot : dataSnapshot.getChildren()) {
                CompletedTask completedTask = completedTaskSnapshot.getValue(CompletedTask.class);
                completedTask.setId(completedTaskSnapshot.getKey());
                completedTask.setLastCompletionDate((-1) * completedTask.getLastCompletionDate());
                completedTasks.add(completedTask);
            }
            return completedTasks;
        }
    }
}
