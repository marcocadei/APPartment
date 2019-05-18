package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.adapters.MyUserHomeRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.model.UserHome;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment che rappresenta una lista di oggetti UserHome
 * Le Activity che contengono questo fragment devono implementare l'interfaccia {@link OnHomeListFragmentInteractionListener}
 */
public class UserHomeListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private List<UserHome> userHomes;

    private RecyclerView.Adapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnHomeListFragmentInteractionListener mListener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public UserHomeListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserHomeListFragment newInstance(int columnCount) {
        UserHomeListFragment fragment = new UserHomeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myRecyclerView = recyclerView;
        }
        userHomes = new ArrayList<>();
        readUserHomes();

        myAdapter = new MyUserHomeRecyclerViewAdapter(userHomes, mListener);
        myRecyclerView.setAdapter(myAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeListFragmentInteractionListener) {
            mListener = (OnHomeListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Metodo per leggere da Firebase Database la lista di UserHome
     */
    private void readUserHomes() {
        String path = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    userHomes.add(postSnapshot.getValue(UserHome.class));
                    myAdapter.notifyDataSetChanged();
                    // FIXME vedere se si può fare l'animazione
                }
                mListener.onHomeListElementsLoaded(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mListener.onHomeListElementsLoaded(0);
                // TODO visualizzare snackbar che comunica l'errore con tasto per fare il reload
            }
        });
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnHomeListFragmentInteractionListener {
        /**
         * Callback invocato quando si seleziona una voce della lista delle case.
         * @param item Oggetto della lista selezionato.
         */
        void onHomeListFragmentInteraction(UserHome item);

        /**
         * Callback invocato quando viene completato il caricamento della lista delle case.
         * @param elements Numero di elementi della lista.
         */
        void onHomeListElementsLoaded(long elements);
    }
}