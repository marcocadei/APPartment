package com.unison.appartment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInsertPostFragmentListener} interface
 * to handle interaction events.
 * Use the {@link InsertPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertPostFragment extends Fragment {
    // Questo fragment non ha parametri
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";*/

    /*private String mParam1;
    private String mParam2;*/

    private OnInsertPostFragmentListener mListener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public InsertPostFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InsertPostFragment.
     */
    public static InsertPostFragment newInstance(String param1, String param2) {
        InsertPostFragment fragment = new InsertPostFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View myView =  inflater.inflate(R.layout.fragment_insert_post, container, false);

        ImageButton btnSendText = myView.findViewById(R.id.fragment_insert_post_btn_send_text);
        btnSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    EditText inputText = myView.findViewById(R.id.fragment_insert_post_input_text);
                    mListener.onInsertPostFragmentSendText(inputText.getText().toString());
                }
            }
        });

        return myView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInsertPostFragmentListener) {
            mListener = (OnInsertPostFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInsertPostFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnInsertPostFragmentListener {
        void onInsertPostFragmentSendText(String message);
    }
}
