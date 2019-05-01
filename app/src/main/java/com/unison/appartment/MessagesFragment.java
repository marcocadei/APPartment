package com.unison.appartment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.InsertPostFragment.OnInsertPostFragmentListener;

/**
 * Fragment che rappresenta l'intera bacheca
 */
public class MessagesFragment extends Fragment implements OnInsertPostFragmentListener {

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
}
