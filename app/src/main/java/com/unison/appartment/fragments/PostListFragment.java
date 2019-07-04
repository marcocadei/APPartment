package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.unison.appartment.adapters.MyPostRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Post;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.viewmodel.PostViewModel;
import java.util.List;


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
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        
        viewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Controllo se è in corso un caricamento e in caso informo il parent che agirà di conseguenza
        // mostrando ad esempio una progress bar
        viewModel.getLoadingLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                listener.loading(loading);
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean error) {
                listener.onPostListError(error);
            }
        });

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
    public void onAttach(@NonNull Context context) {
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
        final Post post;
        String nickname = Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getNickname();
        switch(postType) {
            case Post.TEXT_POST:
                post = new Post(Post.TEXT_POST, content, nickname, System.currentTimeMillis());
                break;
            case Post.IMAGE_POST:
                post = new Post(Post.IMAGE_POST, content, nickname, System.currentTimeMillis());
                break;
            case Post.AUDIO_POST:
                post = new Post(Post.AUDIO_POST, content, nickname, System.currentTimeMillis());
                break;
            default:
                Log.e(getClass().getCanonicalName(), "Post type non valido");
                post = null;
        }
        viewModel.addPost(post);
    }

    public void deletePost(Post post) {
        viewModel.deletePost(post);
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

        void deletePost(Post post);

        void onDowngrade();

        void onPostListError(boolean error);
    }
}
