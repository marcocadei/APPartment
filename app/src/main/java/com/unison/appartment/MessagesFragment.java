package com.unison.appartment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unison.appartment.InsertPostFragment.OnInsertPostFragmentListener;
import com.unison.appartment.PostListFragment.OnPostListFragmentInteractionListener;

/**
 * Fragment che rappresenta l'intera bacheca
 */
public class MessagesFragment extends Fragment implements OnInsertPostFragmentListener, OnPostListFragmentInteractionListener {

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public MessagesFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onInsertPostFragmentSendText(String message) {
        PostListFragment pf = (PostListFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addTextPost(message);
    }

    @Override
    public void onInsertPostFragmentSendImage(Uri selectedImage) {
        PostListFragment pf = (PostListFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addImagePost(selectedImage);
    }

    @Override
    public void onInsertPostFragmentSendAudio(String fileName) {
        PostListFragment pf = (PostListFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addAudioPost(fileName);
    }

    @Override
    public void onPostListFragmentOpenImage(ImageView image, Uri imageUri) {
        Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(), image, ViewCompat.getTransitionName(image));
        i.putExtra("imageUri", imageUri.toString());
        startActivity(i, options.toBundle());
    }
}
