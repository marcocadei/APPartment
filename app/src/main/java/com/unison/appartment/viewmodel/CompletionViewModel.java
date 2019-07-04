package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.Completion;
import com.unison.appartment.repository.CompletionRepository;

import java.util.List;

public class CompletionViewModel extends ViewModel {

    private CompletionRepository repository;

    public CompletionViewModel() {
        repository = new CompletionRepository();
    }

    @NonNull
    public LiveData<List<Completion>> getCompletionLiveData() {
        return repository.getCompletionLiveData();
    }

    public void clearHistory() {
        repository.clearHistory();
    }
}
