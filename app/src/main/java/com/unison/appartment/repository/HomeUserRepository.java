package com.unison.appartment.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeUserRepository {

    // Riferimento al nodo root del database
    private DatabaseReference rootRef;
    // Riferimento al nodo del database a cui sono interessato
    private Query homeUsersRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo Reward
    private FirebaseQueryLiveData liveData;
    private LiveData<List<HomeUser>> homeUserLiveData;

    private MutableLiveData<Boolean> error;

    public HomeUserRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del database a cui sono interessato
        homeUsersRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMEUSERS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName()).orderByChild(DatabaseConstants.HOMEUSERS_HOMENAME_UID_NICKNAME);
        liveData = new FirebaseQueryLiveData(homeUsersRef);
        homeUserLiveData = Transformations.map(liveData, new HomeUserRepository.Deserializer());

        error = new MutableLiveData<>();
    }

    @NonNull
    public LiveData<List<HomeUser>> getHomeUserLiveData() {
        return homeUserLiveData;
    }

    public MutableLiveData<Boolean> getErrorLiveData() {
        return error;
    }

    public void changeRole(String userId, int newRole) {
        String homeName = Appartment.getInstance().getHome().getName();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERS_HOMENAME_UID_ROLE;
        String userHomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + userId +
                DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                DatabaseConstants.USERHOMES_UID_HOMENAME_ROLE;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(homeUserPath, newRole);
        childUpdates.put(userHomePath, newRole);
        rootRef.updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                error.setValue(true);
            }
        });
    }

    public void leaveHome(String userId, Set<String> requestedRewards, Set<String> assignedTasks, @Nullable String newOwnerId) {
        String homeName = Appartment.getInstance().getHome().getName();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId;
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId;
        String userHomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + userId +
                DatabaseConstants.SEPARATOR + homeName;
        String baseRewardPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR;
        String baseTaskPath = DatabaseConstants.UNCOMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR;

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(homeUserPath, null);
        childUpdates.put(homeUserRefPath, null);
        childUpdates.put(userHomePath, null);
        for (String rewardId : requestedRewards) {
            childUpdates.put(baseRewardPath + rewardId + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONID, null);
            childUpdates.put(baseRewardPath + rewardId + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONNAME, null);
            childUpdates.put(baseRewardPath + rewardId + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_VERSION, 0);
        }
        for (String taskId : assignedTasks) {
            childUpdates.put(baseTaskPath + taskId + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERID, null);
            childUpdates.put(baseTaskPath + taskId + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, null);
            childUpdates.put(baseTaskPath + taskId + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_MARKED, false);
            childUpdates.put(baseTaskPath + taskId + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_VERSION, 0);
        }

        if (newOwnerId != null) {
            String newOwnerHomeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR +
                    homeName + DatabaseConstants.SEPARATOR + newOwnerId + DatabaseConstants.SEPARATOR +
                    DatabaseConstants.HOMEUSERS_HOMENAME_UID_ROLE;
            String newOwnerUserHomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR +
                    newOwnerId + DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR +
                    DatabaseConstants.USERHOMES_UID_HOMENAME_ROLE;

            childUpdates.put(newOwnerHomeUserPath, Home.ROLE_OWNER);
            childUpdates.put(newOwnerUserHomePath, Home.ROLE_OWNER);
        }

        rootRef.updateChildren(childUpdates);
    }

    public void deleteHome() {
        String homeName = Appartment.getInstance().getHome().getName();
        String completedTaskPath = DatabaseConstants.COMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName;
        String completionPath = DatabaseConstants.COMPLETIONS + DatabaseConstants.SEPARATOR + homeName;
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName;
        String homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR + homeName;
        String homePath = DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + homeName;
        String postPath = DatabaseConstants.POSTS + DatabaseConstants.SEPARATOR + homeName;
        String rewardPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR + homeName;
        String uncompletedTaskPath = DatabaseConstants.UNCOMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName;

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(completedTaskPath, null);
        childUpdates.put(completionPath, null);
        childUpdates.put(homeUserPath, null);
        childUpdates.put(homeUserRefPath, null);
        childUpdates.put(homePath, null);
        childUpdates.put(postPath, null);
        childUpdates.put(rewardPath, null);
        childUpdates.put(uncompletedTaskPath, null);
        for (HomeUser homeUser : Appartment.getInstance().getHomeUsers().values()) {
            childUpdates.put(DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + homeUser.getUserId() +
                    DatabaseConstants.SEPARATOR + homeName, null);
        }

        rootRef.updateChildren(childUpdates);
    }

    public void changeNickname(String userId, Set<String> requestedRewards, Set<String> assignedTasks, Set<String> ownPosts, String newNickname) {
        if (Appartment.getInstance().getHomeUser(userId) == null) {
            error.setValue(true);
            return;
        }

        String homeName = Appartment.getInstance().getHome().getName();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + userId + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERS_HOMENAME_UID_NICKNAME;
        String basePostPath = DatabaseConstants.POSTS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR;
        String baseRewardPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR;
        String baseTaskPath = DatabaseConstants.UNCOMPLETEDTASKS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR;

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(homeUserPath, newNickname);
        for (String postId : ownPosts) {
            childUpdates.put(basePostPath + postId + DatabaseConstants.SEPARATOR + DatabaseConstants.POSTS_HOMENAME_POSTID_AUTHOR, newNickname);
        }
        for (String rewardId : requestedRewards) {
            childUpdates.put(baseRewardPath + rewardId + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONNAME, newNickname);
        }
        for (String taskId : assignedTasks) {
            childUpdates.put(baseTaskPath + taskId + DatabaseConstants.SEPARATOR + DatabaseConstants.UNCOMPLETEDTASKS_HOMENAME_TASKID_ASSIGNEDUSERNAME, newNickname);
        }
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
