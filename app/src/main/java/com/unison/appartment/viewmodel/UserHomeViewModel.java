package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.UserHome;
import com.unison.appartment.repository.UserHomeRepository;

import java.util.List;

public class UserHomeViewModel extends ViewModel {

    private UserHomeRepository repository;

    public UserHomeViewModel() {
        repository = new UserHomeRepository();
    }

    @NonNull
    public LiveData<List<UserHome>> getUserHomeLiveData() {
        return repository.getUserHomeLiveData();
    }
}
