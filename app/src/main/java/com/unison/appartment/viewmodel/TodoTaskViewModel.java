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

    public void deleteTask(String id) {
        repository.deleteTask(id);
    }

    public void assignTask(String taskId, String userId, String userName) {
        repository.assignTask(taskId, userId, userName);
    }

    public void removeAssignment(String taskId) {
        repository.removeAssignment(taskId);
    }

    public void markTask(String taskId, String userId, String userName) {
        repository.markTask(taskId, userId, userName);
    }

}
