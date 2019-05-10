package com.unison.appartment.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unison.appartment.adapters.MyPostRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.activities.MainActivity;
import com.unison.appartment.model.AudioPost;
import com.unison.appartment.model.ImagePost;
import com.unison.appartment.model.Post;
import com.unison.appartment.model.TextPost;

import java.util.Date;

/**
 * Fragment che rappresenta la lista di post
 */
public class PostListFragment extends Fragment {
    // Numero di colonne della lista
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    // Recyclerview e Adapter della recyclerview
    private RecyclerView.Adapter myAdapter;
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

            myAdapter = new MyPostRecyclerViewAdapter(Post.getPostList(), listener);
            myRecyclerView.setAdapter(myAdapter);
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
    }

    /**
     * Metodi implementati da noi
     */
    // TODO sostituire costante con nome dell'utente realmente loggato
    public void addTextPost(String message){
        TextPost textPost = new TextPost(MainActivity.LOGGED_USER, new Date(), message);
        addPost(textPost);
    }

    public void addImagePost(Uri selectedImage) {
        ImagePost imagePost = new ImagePost(MainActivity.LOGGED_USER, new Date(), selectedImage);
        addPost(imagePost);
    }

    public void addAudioPost(String fileName) {
        AudioPost audioPost = new AudioPost(MainActivity.LOGGED_USER, new Date(), fileName);
        addPost(audioPost);
    }

    private void addPost(Post post) {
        Post.addPost(0, post);
        myAdapter.notifyItemInserted(0);
        myRecyclerView.scrollToPosition(0);
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnPostListFragmentInteractionListener {
        void onPostListFragmentOpenImage(ImageView image, Uri imageUri);
    }
}
