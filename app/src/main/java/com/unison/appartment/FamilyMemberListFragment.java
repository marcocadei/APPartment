package com.unison.appartment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.model.User;

public class FamilyMemberListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    // RecyclerView e Adapter della recyclerView
    private RecyclerView.Adapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnFamilyMemberListFragmentInteractionListener listener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public FamilyMemberListFragment() {
    }

    @SuppressWarnings("unused")
    public static FamilyMemberListFragment newInstance(int columnCount) {
        FamilyMemberListFragment fragment = new FamilyMemberListFragment();
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
        View view = inflater.inflate(R.layout.fragment_family_member_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myAdapter = new MyFamilyMemberRecyclerViewAdapter(User.getUserList(), listener);
            myRecyclerView.setAdapter(myAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFamilyMemberListFragmentInteractionListener) {
            listener = (OnFamilyMemberListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFamilyMemberListFragmentListener errore in insert");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void addMember(User newUser) {
        User.addMember(0, newUser);
        myAdapter.notifyItemInserted(0);
        myRecyclerView.scrollToPosition(0);
    }

    public void removeMember(int position){
        User.removeMember(position);
        myAdapter.notifyItemRemoved(position);
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnFamilyMemberListFragmentInteractionListener {
        void onFamilyMemberListFragmentOpenMember(User user);
    }
}
