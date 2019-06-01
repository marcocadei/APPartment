package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.repository.CompletedTaskRepository;

import java.util.List;

public class CompletedTaskViewModel extends ViewModel {

    private CompletedTaskRepository repository;

    public CompletedTaskViewModel() {
        repository = new CompletedTaskRepository();
    }

    @NonNull
    public LiveData<List<CompletedTask>> getCompletedTaskLiveData() {
        return repository.getCompletedTaskLiveData();
    }
}
