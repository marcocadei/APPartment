package com.unison.appartment.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.state.Appartment;

public class MoneyRewardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public MoneyRewardFragment() {
    }

    public static MoneyRewardFragment newInstance() {
        return new MoneyRewardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_money_reward, container, false);
        TextView textDescription = view.findViewById(R.id.fragment_money_reward_text_description);
        textDescription.setText(getString(R.string.fragment_money_reward_text_description, Appartment.getInstance().getHome().getConversionFactor()));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    // (questo serve, è il metodo da agganciare al bottone)
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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
    // TODO questo fragment ce lo deve avere il listener? in teoria no perchè al parent non deve parlare?SBAGLIATO
    // TODO   invece sì il listener serve!!! da agganciare ad onButtonPressed quando si pigia il bottone per far partire un intent e chiamare l'activity per richiedere il premio in denaro
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
