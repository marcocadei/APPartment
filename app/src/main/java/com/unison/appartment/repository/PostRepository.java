package com.unison.appartment.repository;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.StorageConstants;
import com.unison.appartment.livedata.FirebaseQueryLiveData;
import com.unison.appartment.model.Post;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.state.MyApplication;
import com.unison.appartment.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostRepository {
    // Riferimento al nodo root del database
    private DatabaseReference rootRef;
    // Nodo del database a cui sono interessato
    private DatabaseReference postRef;
    // Livedata che rappresenta i dati nel nodo del database considerato che vengono convertiti
    // tramite un Deserializer in ogetti di tipo UncompletedTask
    private FirebaseQueryLiveData liveData;
    private LiveData<List<Post>> postLiveData;

    private String currentUserUid;
    private String postPath;
    private String homeUserPath;
    private String homeUserRefPath;

    private MutableLiveData<Boolean> loading;
    private MutableLiveData<Boolean> error;

    public PostRepository() {
        String homeName = Appartment.getInstance().getHome().getName();
        currentUserUid = new FirebaseAuth().getCurrentUserUid();
        postPath = DatabaseConstants.SEPARATOR + DatabaseConstants.POSTS +
                DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName();
        homeUserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + currentUserUid;
        homeUserRefPath = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + currentUserUid + DatabaseConstants.SEPARATOR +
                DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_POSTS;

        // Riferimento al nodo root del database
        rootRef = FirebaseDatabase.getInstance().getReference();
        // Riferimento al nodo del Database interessato (i task non completati della casa corrente)
        postRef = FirebaseDatabase.getInstance().getReference(postPath);
        Query orderedPosts = postRef.orderByChild(DatabaseConstants.POSTS_HOMENAME_POSTID_TIMESTAMP);
        liveData = new FirebaseQueryLiveData(orderedPosts);
        postLiveData = Transformations.map(liveData, new PostRepository.Deserializer());

        loading = new MutableLiveData<>();
        error = new MutableLiveData<>();
    }

    @NonNull
    public LiveData<List<Post>> getPostLiveData() {
        return postLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loading;
    }

    public LiveData<Boolean> getErrorLiveData() {
        return error;
    }

    public void addPost(Post newPost) {
        // Segnalo l'inizio del caricamento
        loading.setValue(true);

        switch (newPost.getType()){
            case Post.TEXT_POST:
                // Nel caso del testo il post non deve essere modificato
                addUpdatedPost(newPost);
                break;
            case Post.IMAGE_POST:
                addImagePost(newPost);
                break;
            case Post.AUDIO_POST:
                addAudioPost(newPost);
                break;
        }
    }

    public void addImagePost(final Post newPost) {
        Glide.with(MyApplication.getAppContext()).asBitmap().load(newPost.getContent()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                // Ridemnsiono l'immagine (in generale la rimpicciolisco)
                resource = ImageUtils.resize(resource, ImageUtils.MAX_WIDTH, ImageUtils.MAX_HEIGHT);
                // Comprimo l'immagine
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data = baos.toByteArray();

                // UUID genera un nome univoco per il file che sto caricando
                final StorageReference postImageRef = FirebaseStorage.getInstance().getReference().child(StorageConstants.POST_IMAGES).
                        child(Appartment.getInstance().getHome().getName()).child(UUID.randomUUID().toString());
                final String storagePath = postImageRef.getPath();
                UploadTask uploadTask = postImageRef.putBytes(data);

                // Codice della guida per ottenere l'URL di download del media appena caricato
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            return null;
                        }
                        return postImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String imageUrl = task.getResult().toString();
                            newPost.setContent(imageUrl);
                            newPost.setStoragePath(storagePath);
                            addUpdatedPost(newPost);
                        } else {
                            // C'è un errore e quindi lo notifico, ma subito dopo l'errore non c'è più
                            error.setValue(true);
                            error.setValue(false);
                            loading.setValue(false);
                        }
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public void addAudioPost(final Post newPost) {
        final StorageReference postAudioRef = FirebaseStorage.getInstance().getReference().child(StorageConstants.POST_AUDIOS)
                .child(Appartment.getInstance().getHome().getName()).child(UUID.randomUUID().toString());
        final String storagePath = postAudioRef.getPath();
        Uri uri = Uri.fromFile(new File(newPost.getContent()));
        UploadTask uploadTask = postAudioRef.putFile(uri);

        // Codice della guida per ottenere l'URL di download del media appena caricato
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    return null;
                }
                return postAudioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String audioUrl = task.getResult().toString();
                    newPost.setContent(audioUrl);
                    newPost.setStoragePath(storagePath);
                    addUpdatedPost(newPost);
                } else {
                    error.setValue(true);
                    error.setValue(false);
                    loading.setValue(false);
                }
            }
        });
    }

    private void addUpdatedPost(Post newPost) {
        Map<String, Object> childUpdates = new HashMap<>();

        String key = postRef.push().getKey();
        newPost.setId(key);
        newPost.setTimestamp((-1) * newPost.getTimestamp());
        childUpdates.put(postPath + DatabaseConstants.SEPARATOR +  key, newPost);

        // Aggiorno le statistiche
        switch (newPost.getType()){
            case Post.TEXT_POST:
                childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_TEXTPOSTS,
                        Appartment.getInstance().getHomeUser(currentUserUid).getTextPosts() + 1);
                break;
            case Post.IMAGE_POST:
                childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_IMAGEPOSTS,
                        Appartment.getInstance().getHomeUser(currentUserUid).getImagePosts() + 1);
                break;
            case Post.AUDIO_POST:
                childUpdates.put(homeUserPath + DatabaseConstants.SEPARATOR + DatabaseConstants.HOMEUSERS_HOMENAME_UID_AUDIOPOSTS,
                        Appartment.getInstance().getHomeUser(currentUserUid).getAudioPosts() + 1);
                break;
        }

        // Salvo il riferimento al post in home-users-refs
        childUpdates.put(homeUserRefPath + DatabaseConstants.SEPARATOR + key, true);

        rootRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.setValue(false);
            }
        });
    }

    public void deletePost(Post post) {
        // Elimino anche il media memorizzato nello storage associato al post, se c'è
        if (post.getStoragePath() != null) {
            StorageReference postRef = FirebaseStorage.getInstance().getReference(post.getStoragePath());
            postRef.delete();
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(postPath + DatabaseConstants.SEPARATOR + post.getId(), null);
        // Rimuovo il riferimento al post da home-users-refs
        childUpdates.put(homeUserRefPath + DatabaseConstants.SEPARATOR + post.getId(), null);

        rootRef.updateChildren(childUpdates);
    }

    private class Deserializer implements Function<DataSnapshot, List<Post>> {
        @Override
        public List<Post> apply(DataSnapshot dataSnapshot) {
            List<Post> posts = new ArrayList<>();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Post post = postSnapshot.getValue(Post.class);
                post.setId(postSnapshot.getKey());
                post.setTimestamp((-1) * post.getTimestamp());
                posts.add(post);
            }
            return posts;
        }
    }
}
