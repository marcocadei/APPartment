package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

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

import com.unison.appartment.adapters.MyFamilyMemberRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.viewmodel.HomeUserViewModel;

import java.util.List;

/**
 * Fragment che rappresenta la lista dei membri di una famiglia
 * Le Activity che contengono questo fragment devono implementare l'interfaccia {@link OnFamilyMemberListFragmentInteractionListener}
 */
public class FamilyMemberListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private HomeUserViewModel viewModel;

    private ListAdapter myAdapter;
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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Quando il fragment è creato recupero i parametri
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        viewModel = ViewModelProviders.of(getActivity()).get(HomeUserViewModel.class);
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
            myAdapter = new MyFamilyMemberRecyclerViewAdapter(/*listener*/);
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readFamilyMembers();
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
                    + " must implement OnFamilyMemberListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void readFamilyMembers() {
        LiveData<List<HomeUser>> rewardLiveData = viewModel.getHomeUserLiveData();
        rewardLiveData.observe(getViewLifecycleOwner(), new Observer<List<HomeUser>>() {
            @Override
            public void onChanged(List<HomeUser> familyMembers) {
                myAdapter.submitList(familyMembers);
                listener.onFamilyMemberListElementsLoaded(familyMembers.size());
            }
        });
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnFamilyMemberListFragmentInteractionListener {
        void onFamilyMemberListFragmentOpenMember(User user);
        void onFamilyMemberListElementsLoaded(int elements);
    }
}
