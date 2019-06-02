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

import com.unison.appartment.adapters.MyCompletionRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.model.Completion;
import com.unison.appartment.viewmodel.CompletionViewModel;

import java.util.List;

public class CompletionListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private CompletionViewModel viewModel;

    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public CompletionListFragment() {
    }

    @SuppressWarnings("unused")
    public static CompletionListFragment newInstance(int columnCount) {
        CompletionListFragment fragment = new CompletionListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        viewModel = ViewModelProviders.of(getActivity()).get(CompletionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completion_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyCompletionRecyclerViewAdapter();
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readCompletions();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void readCompletions() {
        LiveData<List<Completion>> rewardLiveData = viewModel.getCompletionLiveData();
        rewardLiveData.observe(getViewLifecycleOwner(), new Observer<List<Completion>>() {
            @Override
            public void onChanged(List<Completion> completions) {
                myAdapter.submitList(completions);
//                listener.onRewardListElementsLoaded(rewards.size());
            }
        });
    }
}
