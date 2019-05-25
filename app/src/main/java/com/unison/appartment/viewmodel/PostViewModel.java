package com.unison.appartment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.unison.appartment.model.Post;
import com.unison.appartment.repository.PostRepository;

import java.util.List;

public class PostViewModel {

    private PostRepository repository;

    public PostViewModel() {
        repository = new PostRepository();
    }

    @NonNull
    public LiveData<List<Post>> getUserHomeLiveData() {
        return repository.getPostLiveData();
    }
}
