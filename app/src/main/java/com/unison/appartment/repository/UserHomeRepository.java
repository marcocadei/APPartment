package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.List;

public class UserHomeRepository {
    // Nodo del database a cui sono interessato
    private DatabaseReference userHomeRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<UserHome>> userHomeLiveData;

    public UserHomeRepository() {
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        userHomeRef =
                FirebaseDatabase.getInstance().getReference(
                        DatabaseConstants.SEPARATOR + DatabaseConstants.USERHOMES +
                                DatabaseConstants.SEPARATOR + FirebaseAuth.getInstance().getCurrentUser().getUid());
        liveData = new FirebaseQueryLiveData(userHomeRef);
        userHomeLiveData = Transformations.map(liveData, new UserHomeRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<UserHome>> getUserHomeLiveData() {
        return userHomeLiveData;
    }

    private class Deserializer implements Function<DataSnapshot, List<UserHome>> {
        @Override
        public List<UserHome> apply(DataSnapshot dataSnapshot) {
            List<UserHome> userHomes = new ArrayList<>();
            for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                UserHome newUserHome = taskSnapshot.getValue(UserHome.class);
                // newUncompletedTask.setId(taskSnapshot.getKey());
                userHomes.add(newUserHome);
            }
            return userHomes;
        }
    }
}
