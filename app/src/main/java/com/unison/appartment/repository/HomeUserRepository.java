package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.Reward;
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

    public void leaveHome(String userId) {
//        String homeName = Appartment.getInstance().getHome().getName();
//        int homeMembers = Appartment.getInstance().getHome().getMembers();
//        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
//                DatabaseConstants.SEPARATOR + userId;
//        String homePath = DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + homeName +
//                DatabaseConstants.SEPARATOR + DatabaseConstants.HOMES_HOMENAME_MEMBERS;
//
//        final Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(homeUserPath, null);
//        childUpdates.put(homePath, homeMembers - 1);
//
//        rootRef.child(DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR + homeName)
//                .orderByChild(DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONID).equalTo(userId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot reward: dataSnapshot.getChildren()) {
//                            Log.e("zzz", reward.getValue(Reward.class).getName());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

//        rootRef.runTransaction(new Transaction.Handler() {
//            @NonNull
//            @Override
//            public Transaction.Result doTransaction(final @NonNull MutableData mutableData) {
//
//                rootRef.updateChildren(childUpdates);
//                rootRef.child(DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR + homeName)
//                        .orderByChild(DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONID).equalTo(userId)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                return Transaction.success(mutableData);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                return Transaction.abort();
//                            }
//                        });
//            }
//
//            @Override
//            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//
//            }
//        });

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
