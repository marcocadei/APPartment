package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.repository.TodoTaskRepository;

import java.util.List;

public class TodoTaskViewModel extends ViewModel {

    private TodoTaskRepository repository;

    public TodoTaskViewModel() {
        repository = new TodoTaskRepository();
    }

    @NonNull
    public LiveData<List<UncompletedTask>> getTaskLiveData() {
        return repository.getTaskLiveData();
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        repository.addTask(newUncompletedTask);
    }

}
