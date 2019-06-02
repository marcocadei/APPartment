package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.Completion;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.List;

public class CompletionRepository {
    // Nodo del database a cui sono interessato
    private DatabaseReference completionRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo Completion
    private FirebaseQueryLiveData liveData;
    private LiveData<List<Completion>> completionLiveData;

    public CompletionRepository() {
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        completionRef =
                FirebaseDatabase.getInstance().getReference(
                        DatabaseConstants.SEPARATOR + DatabaseConstants.COMPLETIONS +
                                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName() +
                                DatabaseConstants.SEPARATOR + Appartment.getInstance().getCurrentCompletedTaskName());
        Query orderedCompletions = completionRef.orderByChild(DatabaseConstants.COMPLETIONS_HOMENAME_TASKID_COMPLETIONDATE);
        liveData = new FirebaseQueryLiveData(orderedCompletions);
        completionLiveData = Transformations.map(liveData, new CompletionRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<Completion>> getCompletionLiveData() {
        return completionLiveData;
    }

    private class Deserializer implements Function<DataSnapshot, List<Completion>> {
        @Override
        public List<Completion> apply(DataSnapshot dataSnapshot) {
            List<Completion> completions = new ArrayList<>();
            for (DataSnapshot completionSnapshot : dataSnapshot.getChildren()) {
                Completion completion = completionSnapshot.getValue(Completion.class);
                completions.add(completion);
            }
            return completions;
        }
    }
}
