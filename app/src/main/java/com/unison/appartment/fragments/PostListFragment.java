package com.unison.appartment.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unison.appartment.adapters.MyPostRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.StorageConstants;
import com.unison.appartment.model.Post;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.ImageUtils;
import com.unison.appartment.viewmodel.PostViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.UUID;


/**
 * Fragment che rappresenta una lista di Post
 */
public class PostListFragment extends Fragment {
    // Numero di colonne della lista
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private PostViewModel viewModel;

    // Recyclerview e Adapter della recyclerview
    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnPostListFragmentInteractionListener listener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public PostListFragment() {
    }

    @SuppressWarnings("unused")
    public static PostListFragment newInstance(int columnCount) {
        PostListFragment fragment = new PostListFragment();
        // Parametri del fragment
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quando il fragment è creato recupero i parametri
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Imposto l'adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyPostRecyclerViewAdapter(listener);
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readPosts();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnPostListFragmentInteractionListener) {
            listener = (OnPostListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Metodo per leggere da Firebase Database la lista dei post
     */
    private void readPosts() {
        LiveData<List<Post>> postLiveData = viewModel.getPostLiveData();
        postLiveData.observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                myAdapter.submitList(posts);
                listener.onHomeListElementsLoaded(posts.size());
            }
        });
    }

    public void addPost(String content, int postType) {
        String nickname = Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getNickname();
        switch(postType) {
            case Post.TEXT_POST:
                addTextPost(content, nickname);
                break;
            case Post.IMAGE_POST:
                addImagePost(content, nickname);
                break;
            case Post.AUDIO_POST:
                addAudioPost(content, nickname);
                break;
            default:
                // TODO errore, non si deve entrare qui
        }
    }

    public void addTextPost(String content, String nickname) {
        Post post = new Post(Post.TEXT_POST, content, nickname, System.currentTimeMillis());
        viewModel.addPost(post);
    }

    public void addImagePost(String content, String nickname) {
        // Come prima cosa avviso il parent che sto caricando dei contenuti
        listener.loading(true);

        final Post post = new Post(Post.IMAGE_POST, content, nickname, System.currentTimeMillis());
        Glide.with(getContext()).asBitmap().load(post.getContent()).into(new CustomTarget<Bitmap>() {
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
                UploadTask uploadTask = postImageRef.putBytes(data);

                // Codice della guida per ottenere l'URL di download del media appena caricato
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            // TODO gestire errore upload
                        }
                        return postImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String imageUrl = task.getResult().toString();
                            post.setContent(imageUrl);
                            viewModel.addPost(post);

                            // Avviso il parent che il caricamento è terminato
                            listener.loading(false);
                        } else {
                            // TODO gestire errore upload
                        }
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public void addAudioPost(String content, String nickname) {
        // Come prima cosa avviso il parent che sto caricando dei contenuti
        listener.loading(true);

        final Post post = new Post(Post.AUDIO_POST, content, nickname, System.currentTimeMillis());
        final StorageReference postAudioRef = FirebaseStorage.getInstance().getReference().child(StorageConstants.POST_AUDIOS)
                .child(Appartment.getInstance().getHome().getName()).child(UUID.randomUUID().toString());
        Uri uri = Uri.fromFile(new File(content));
        UploadTask uploadTask = postAudioRef.putFile(uri);

        // Codice della guida per ottenere l'URL di download del media appena caricato
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    // TODO gestire errore upload
                }
                return postAudioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String audioUrl = task.getResult().toString();
                    post.setContent(audioUrl);
                    viewModel.addPost(post);

                    // Avviso il parent che il caricamento è terminato
                    listener.loading(false);
                } else {
                    // TODO gestire errore upload
                }
            }
        });
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnPostListFragmentInteractionListener {
        void onPostListFragmentOpenImage(ImageView image, String imageUri);

        /**
         * Callback invocato quando viene completato il caricamento della lista dei post.
         * @param elements Numero di elementi della lista.
         */
        void onHomeListElementsLoaded(int elements);

        /**
         * Callback usata per indicare se si stanno caricando dei contenuti e quindi bisogna
         * mostrare qualcosa all'utente (es progressbar)
         * @param loading true se si sta caricando, false altrimenti
         */
        void loading(boolean loading);
    }
}
