package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.unison.appartment.adapters.MyUserHomeRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.viewmodel.UserHomeViewModel;
import java.util.List;


/**
 * Fragment che rappresenta una lista di oggetti UserHome
 * Le Activity che contengono questo fragment devono implementare l'interfaccia {@link OnHomeListFragmentInteractionListener}
 */
public class UserHomeListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private UserHomeViewModel viewModel;

    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnHomeListFragmentInteractionListener mListener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public UserHomeListFragment() {
    }

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

        viewModel = ViewModelProviders.of(getActivity()).get(UserHomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyUserHomeRecyclerViewAdapter(mListener);
            /*
            Observer commentato perché tanto per aggiungere/togliere un elemento
            alla lista delle case bisogna necessariamente cambiare activity e quando si ritorna
            si visualizza la lista completa.
             */
            /*myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(0);
                }
            });*/
            myRecyclerView.setAdapter(myAdapter);

            readUserHomes();
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
        LiveData<List<UserHome>> taskLiveData = viewModel.getUserHomeLiveData();
        taskLiveData.observe(getViewLifecycleOwner(), new Observer<List<UserHome>>() {
            @Override
            public void onChanged(List<UserHome> userHomes) {
                myAdapter.submitList(userHomes);
                mListener.onHomeListElementsLoaded(userHomes.size());
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
