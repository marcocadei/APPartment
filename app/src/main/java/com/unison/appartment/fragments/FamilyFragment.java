package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.unison.appartment.R;
import com.unison.appartment.activities.FamilyMemberDetailActivity;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;


/**
 * Fragment che contiene la lista dei membri di una famiglia e le relative statistiche
 */
public class FamilyFragment extends Fragment implements FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener{

    public final static String EXTRA_MEMBER_OBJECT = "memberObject";

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public FamilyFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static FamilyFragment newInstance(String param1, String param2) {
        FamilyFragment fragment = new FamilyFragment();
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
        final View myView = inflater.inflate(R.layout.fragment_family, container, false);

        return myView;
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
    public void onFamilyMemberListFragmentOpenMember(HomeUser member) {
        Intent i = new Intent(getActivity(), FamilyMemberDetailActivity.class);
        i.putExtra(EXTRA_MEMBER_OBJECT, member);
        startActivity(i);
    }

    @Override
    public void onFamilyMemberListElementsLoaded(int elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_family_progress);
        progressBar.setVisibility(View.GONE);
    }
}
