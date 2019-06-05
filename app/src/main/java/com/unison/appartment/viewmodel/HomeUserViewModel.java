package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.HomeUser;
import com.unison.appartment.repository.HomeUserRepository;

import java.util.List;

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
}
