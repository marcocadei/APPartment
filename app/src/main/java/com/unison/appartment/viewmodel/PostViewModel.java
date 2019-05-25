package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unison.appartment.model.Post;
import com.unison.appartment.repository.PostRepository;

import java.util.List;

public class PostViewModel extends ViewModel {

    private PostRepository repository;

    public PostViewModel() {
        repository = new PostRepository();
    }

    @NonNull
    public LiveData<List<Post>> getPostLiveData() {
        return repository.getPostLiveData();
    }
}
