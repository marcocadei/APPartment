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
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeUserRepository {

    // Riferimento al nodo root del database
    private DatabaseReference rootRef;
    // Riferimento al nodo del database a cui sono interessato
    private DatabaseReference homeUsersRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo Reward
    private FirebaseQueryLiveData liveData;
    private LiveData<List<HomeUser>> homeUserLiveData;

    public HomeUserRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del database a cui sono interessato
        homeUsersRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMEUSERS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        liveData = new FirebaseQueryLiveData(homeUsersRef);
        homeUserLiveData = Transformations.map(liveData, new HomeUserRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<HomeUser>> getHomeUserLiveData() {
        return homeUserLiveData;
    }

    public void changeRole(String userId, int newRole) {
        Map<String, Object> childUpdates = new HashMap<>();
        String homeName = Appartment.getInstance().getHome().getName();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERS_HOMENAME_UID_ROLE;
        String userHomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + userId +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                DatabaseConstants.USERHOMES_UID_HOMENAME_ROLE;
        childUpdates.put(homeUserPath, newRole);
        childUpdates.put(userHomePath, newRole);
        rootRef.updateChildren(childUpdates);
    }

    private class Deserializer implements Function<DataSnapshot, List<HomeUser>> {
        @Override
        public List<HomeUser> apply(DataSnapshot dataSnapshot) {
            List<HomeUser> homeUsers = new ArrayList<>();
            for (DataSnapshot homeUserSnapshot : dataSnapshot.getChildren()) {
                HomeUser homeUser = homeUserSnapshot.getValue(HomeUser.class);
                homeUser.setUserId(homeUserSnapshot.getKey());
                homeUsers.add(homeUser);
            }
            return homeUsers;
        }
    }
}
