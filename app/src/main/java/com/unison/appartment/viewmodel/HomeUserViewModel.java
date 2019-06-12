package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.HomeUser;
import com.unison.appartment.repository.HomeUserRepository;

import java.util.List;
import java.util.Set;

public class HomeUserViewModel extends ViewModel {

    private HomeUserRepository repository;

    public HomeUserViewModel() {
        repository = new HomeUserRepository();
    }

    @NonNull
    public LiveData<List<HomeUser>> getHomeUserLiveData() {
        return repository.getHomeUserLiveData();
    }

    public void changeRole(String userId, int newRole) {
        repository.changeRole(userId, newRole);
    }

    public void leaveHome(String userId, Set<String> requestedRewards, Set<String> assignedTasks, @Nullable String newOwnerId) {
        repository.leaveHome(userId, requestedRewards, assignedTasks, newOwnerId);
    }

    public void deleteHome() {
        repository.deleteHome();
    }
}
