package com.unison.appartment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.model.Member;


/**
 * Fragment che presenta la lista dei membri della famiglia e le relative statistiche
 */
public class FamilyFragment extends Fragment {

    private static final String FROM_FAMILY = "fromFamily";
    private static final int ADD_MEMBER_REQUEST_CODE = 1;

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

        FloatingActionButton floatNewMember = myView.findViewById(R.id.fragment_family_member_float_new_member);
        floatNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateMemberActivity.class);
                i.putExtra("origin", FROM_FAMILY);
                startActivityForResult(i, ADD_MEMBER_REQUEST_CODE);
            }

        });
        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            FamilyMemberListFragment fmlf = (FamilyMemberListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_family_member_list);
            fmlf.addMember((Member) data.getSerializableExtra("newMember"));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //TODO: fare il metodo che apre il "profilo" di un membro, passando con un intent alla relativa activity
    /*@Override
    public void onTodoListFragmentOpenTask(Task task) {
        Intent i = new Intent(getActivity(), TaskDetailActivity.class);
        i.putExtra("task", task);
        startActivity(i);
    }*/
}
