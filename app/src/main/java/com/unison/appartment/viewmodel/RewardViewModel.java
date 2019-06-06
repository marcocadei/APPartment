package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.Reward;
import com.unison.appartment.repository.RewardRepository;

import java.util.List;

public class RewardViewModel extends ViewModel {

    private RewardRepository repository;

    public RewardViewModel() {
        repository = new RewardRepository();
    }

    @NonNull
    public LiveData<List<Reward>> getRewardLiveData() {
        return repository.getRewardLiveData();
    }

    public void addReward(Reward newReward) {
        repository.addReward(newReward);
    }

    public void deleteReward(String id) {
        repository.deleteReward(id);
    }

    public void editReward(Reward reward) {
        repository.editReward(reward);
    }

    public void requestReward(Reward reward, String userId, String userName) {
        repository.requestReward(reward, userId, userName);
    }

    public void cancelRequest(Reward reward) {
        repository.cancelRequest(reward);
    }

    public void confirmRequest(Reward reward, String userId) {
        repository.confirmRequest(reward, userId);
    }
}
