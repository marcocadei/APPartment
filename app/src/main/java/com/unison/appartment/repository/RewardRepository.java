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
import com.unison.appartment.model.Reward;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardRepository {

    // Riferimento al nodo root del database
    private DatabaseReference rootRef;
    // Riferimento al nodo del database a cui sono interessato
    private DatabaseReference rewardsRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo Reward
    private FirebaseQueryLiveData liveData;
    private LiveData<List<Reward>> rewardLiveData;

    public RewardRepository() {
        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del database a cui sono interessato
        rewardsRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.REWARDS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        Query orderedReward = rewardsRef.orderByChild(DatabaseConstants.REWARDS_HOMENAME_REWARDID_NAME);
        liveData = new FirebaseQueryLiveData(orderedReward);
        rewardLiveData = Transformations.map(liveData, new RewardRepository.Deserializer());
    }

    @NonNull
    public LiveData<List<Reward>> getRewardLiveData() {
        return rewardLiveData;
    }

    public void addReward(Reward newReward) {
        String key = rewardsRef.push().getKey();
        newReward.setId(key);
        rewardsRef.child(key).setValue(newReward);
    }

    public void deleteReward(String id){
        rewardsRef.child(id).removeValue();
    }

    public void editReward(Reward reward) {
        rewardsRef.child(reward.getId()).setValue(reward);
    }

    public void requestReward(Reward reward, String userId, String userName) {
        String rewardsPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                reward.getId();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                userId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(rewardsPath + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONID, userId);
        childUpdates.put(rewardsPath + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONNAME, userName);
        // I punti diminuiscono di una quantità pari ai punti associati al premio ottenuto
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_POINTS, Appartment.getInstance().getHomeUser(userId).getPoints() - reward.getPoints());
        rootRef.updateChildren(childUpdates);
    }

    public void cancelRequest(Reward reward) {
        Map<String, Object> childUpdates = new HashMap<>();
        String rewardsPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                reward.getId();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                reward.getReservationId();

        childUpdates.put(rewardsPath + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONID, null);
        childUpdates.put(rewardsPath + DatabaseConstants.SEPARATOR + DatabaseConstants.REWARDS_HOMENAME_REWARDID_RESERVATIONNAME, null);
        // Vengono riaggiunti i punti all'utente che aveva eseguito la richiesta
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_POINTS, Appartment.getInstance().getHomeUser(reward.getReservationId()).getPoints() + reward.getPoints());
        rootRef.updateChildren(childUpdates);
    }

    public void confirmRequest(Reward reward, String userId) {
        /*
        Precondizione: L'utente ha abbastanza punti per ritirare il premio, e il saldo punti al
        termine dell'operazione non è negativo.
         */
        Map<String, Object> childUpdates = new HashMap<>();
        String rewardsPath = DatabaseConstants.REWARDS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                reward.getId();
        String homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName() + DatabaseConstants.SEPARATOR +
                userId;
        childUpdates.put(rewardsPath, null);
        // I claimed rewards aumentano di uno
        childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_CLAIMEDREWARDS, Appartment.getInstance().getHomeUser(userId).getClaimedRewards() + 1);
        rootRef.updateChildren(childUpdates);
    }

    private class Deserializer implements Function<DataSnapshot, List<Reward>> {
        @Override
        public List<Reward> apply(DataSnapshot dataSnapshot) {
            List<Reward> rewards = new ArrayList<>();
            for (DataSnapshot rewardSnapshot : dataSnapshot.getChildren()) {
                Reward reward = rewardSnapshot.getValue(Reward.class);
                reward.setId(rewardSnapshot.getKey());
                rewards.add(reward);
            }
            return rewards;
        }
    }
}
