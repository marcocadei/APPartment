package com.unison.appartment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment che rappresenta l'intera bacheca
 */
public class MessagesFragment extends Fragment implements InsertPostFragment.OnInsertPostFragmentListener{

    /*private OnFragmentInteractionListener mListener;*/

    public MessagesFragment() {
        // Required empty public constructor
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /*mListener = null;*/
    }

    @Override
    public void onInsertPostFragmentSendText(String message) {
        ListPostFragment pf = (ListPostFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addTextPost(message);
    }

    @Override
    public void onInsertPostFragmentSendImage(Uri selectedImage) {
        ListPostFragment pf = (ListPostFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addImagePost(selectedImage);
    }

    @Override
    public void onInsertPostFragmentSendAudio(String fileName) {
        ListPostFragment pf = (ListPostFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragment_messages_fragment_list_post);
        pf.addAudioPost(fileName);
    }
}
