package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.R;
import com.unison.appartment.activities.FamilyMemberDetailActivity;
import com.unison.appartment.model.User;


/**
 * Fragment che presenta la lista dei membri della famiglia e le relative statistiche
 */
public class FamilyFragment extends Fragment implements FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener{

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

        // TODO: da fare per implementare la cancellazione di un elemento nella lista
        /*Button btnDeleteMember = myView.findViewById(R.id.fragment_family_member_btn_delete_member);
        btnDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyMemberListFragment fmlf = (FamilyMemberListFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_family_member_list);
                fmlf.removeUser();
            }
        });*/

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
    public void onFamilyMemberListFragmentOpenMember(User user) {
        Intent i = new Intent(getActivity(), FamilyMemberDetailActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}
