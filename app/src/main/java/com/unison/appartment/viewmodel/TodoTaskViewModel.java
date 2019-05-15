package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.repository.TodoTaskRepository;
import com.unison.appartment.model.Task;

import java.util.List;

public class TodoTaskViewModel extends ViewModel {

    private TodoTaskRepository repository;

    public TodoTaskViewModel() {
        repository = new TodoTaskRepository();
    }

    @NonNull
    public LiveData<List<Task>> getTaskLiveData() {
        return repository.getTaskLiveData();
    }

    public void addTask(Task newTask) {
        repository.addTask(newTask);
    }

}
