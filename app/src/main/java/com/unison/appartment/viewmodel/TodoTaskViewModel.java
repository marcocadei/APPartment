package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    public MutableLiveData<Boolean> getErrorLiveData() {
        return repository.getErrorLiveData();
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        repository.addTask(newUncompletedTask);
    }

    public void editTask(UncompletedTask newUncompletedTask) {
        repository.editTask(newUncompletedTask);
    }

    public void deleteTask(String id, int taskVersion) {
        repository.deleteTask(id, taskVersion);
    }

    public void assignTask(String taskId, String userId, String userName, int taskVersion) {
        repository.assignTask(taskId, userId, userName, taskVersion);
    }

    public void removeAssignment(String taskId, String assignedUserId, int taskVersion) {
        repository.removeAssignment(taskId, assignedUserId, taskVersion);
    }

    public void removeAssignmentAndDelete(String taskId, String assignedUserId, int taskVersion) {
        repository.removeAssignmentAndDelete(taskId, assignedUserId, taskVersion);
    }

    public void switchAssignment(String taskId, String assignedUserId, String newAssignedUserId, String newAssignedUserName, int taskVersion) {
        repository.switchAssignment(taskId, assignedUserId, newAssignedUserId, newAssignedUserName, taskVersion);
    }

    public void markTask(String taskId, String userId, String userName, int taskVersion) {
        repository.markTask(taskId, userId, userName, taskVersion);
    }

    public void cancelCompletion(String taskId, String userId, int taskVersion) {
        repository.cancelCompletion(taskId, userId, taskVersion);
    }

    public void confirmCompletion(UncompletedTask task, String assignedUserId) {
        repository.confirmCompletion(task, assignedUserId);
    }
}
