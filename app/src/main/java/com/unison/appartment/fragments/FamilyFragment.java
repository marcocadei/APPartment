package com.unison.appartment.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.unison.appartment.R;
import com.unison.appartment.activities.FamilyMemberDetailActivity;
import com.unison.appartment.model.HomeUser;

/**
 * Fragment che contiene la lista dei membri di una famiglia e le relative statistiche
 */
public class FamilyFragment extends Fragment implements FamilyMemberListFragment.OnFamilyMemberListFragmentInteractionListener{

    public final static String EXTRA_USER_ID = "userId";
    public final static String EXTRA_NEW_ROLE = "newRole";
    public final static String EXTRA_OPERATION_TYPE = "operationType";
    public final static int OPERATION_CHANGE_ROLE = 0;

    private static final int MEMBER_DETAIL_REQUEST_CODE = 1;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public FamilyFragment() {
    }

    public static FamilyFragment newInstance() {
        return new FamilyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_family, container, false);

        return myView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MEMBER_DETAIL_REQUEST_CODE) {
            FamilyMemberListFragment listFragment = (FamilyMemberListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_family_member_list);
            if (resultCode == Activity.RESULT_OK) {
                switch (data.getIntExtra(EXTRA_OPERATION_TYPE, -1)) {
                    case OPERATION_CHANGE_ROLE:
                        listFragment.changeRole(data.getStringExtra(EXTRA_USER_ID), data.getIntExtra(EXTRA_NEW_ROLE, -1));
                        break;

                    default:
                        Log.e(getClass().getCanonicalName(), "Operation type non riconosciuto");
                        break;
                }
            }
        }
    }

    @Override
    public void onFamilyMemberListFragmentOpenMember(HomeUser member) {
        Intent i = new Intent(getActivity(), FamilyMemberDetailActivity.class);
        i.putExtra(FamilyMemberDetailActivity.EXTRA_MEMBER_OBJECT, member);
        startActivityForResult(i, MEMBER_DETAIL_REQUEST_CODE);
    }

    @Override
    public void onFamilyMemberListElementsLoaded(int elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_family_progress);
        progressBar.setVisibility(View.GONE);
    }
}
