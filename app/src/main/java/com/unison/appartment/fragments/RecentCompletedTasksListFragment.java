package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.unison.appartment.R;
import com.unison.appartment.adapters.MyCompletedTasksRecyclerViewAdapter;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.viewmodel.CompletedTaskViewModel;
import com.unison.appartment.fragments.AllCompletedTasksListFragment.OnAllCompletedTasksListFragmentInteractionListener;

import java.util.List;


public class RecentCompletedTasksListFragment extends Fragment {

    // Numero di colonne della lista
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private CompletedTaskViewModel viewModel;

    // Recyclerview e Adapter della recyclerview
    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnAllCompletedTasksListFragmentInteractionListener listener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public RecentCompletedTasksListFragment() {
    }

    @SuppressWarnings("unused")
    public static RecentCompletedTasksListFragment newInstance(int columnCount) {
        RecentCompletedTasksListFragment fragment = new RecentCompletedTasksListFragment();
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

        viewModel = ViewModelProviders.of(getActivity()).get(CompletedTaskViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completedtask_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyCompletedTasksRecyclerViewAdapter(listener);
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readCompletedTasks();
        }
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnAllCompletedTasksListFragmentInteractionListener) {
            listener = (OnAllCompletedTasksListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onAllCompletedTasksListElementsLoaded");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void readCompletedTasks() {
        LiveData<List<CompletedTask>> taskLiveData = viewModel.getRecentCompletedTaskLiveData();
        taskLiveData.observe(getViewLifecycleOwner(), new Observer<List<CompletedTask>>() {
            @Override
            public void onChanged(List<CompletedTask> completedTasks) {
                myAdapter.submitList(completedTasks);
                listener.onAllCompletedTasksListElementsLoaded(completedTasks.size());
            }
        });
    }
}
