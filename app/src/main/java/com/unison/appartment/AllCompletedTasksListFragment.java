package com.unison.appartment;

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

import com.unison.appartment.dummy.DummyContent;
import com.unison.appartment.dummy.DummyContent.DummyItem;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.viewmodel.CompletedTaskViewModel;

import java.util.List;


public class AllCompletedTasksListFragment extends Fragment {
    // Numero di colonne della lista
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private CompletedTaskViewModel viewModel;

    // Recyclerview e Adapter della recyclerview
    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnAllCompletedTasksListFragmentInteractionListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllCompletedTasksListFragment() {
    }

    @SuppressWarnings("unused")
    public static AllCompletedTasksListFragment newInstance(int columnCount) {
        AllCompletedTasksListFragment fragment = new AllCompletedTasksListFragment();
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
        viewModel = ViewModelProviders.of(getActivity()).get(CompletedTaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

            myAdapter = new MyAllCompletedTasksRecyclerViewAdapter(listener);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void readCompletedTasks() {
        LiveData<List<CompletedTask>> taskLiveData = viewModel.getCompletedTaskLiveData();
        taskLiveData.observe(getViewLifecycleOwner(), new Observer<List<CompletedTask>>() {
            @Override
            public void onChanged(List<CompletedTask> completedTasks) {
                myAdapter.submitList(completedTasks);
                /*listener.onTodoListElementsLoaded(uncompletedTasks.size());
                Log.d("provaListAdapter", "aggiunto");*/
            }
        });
    }

    public interface OnAllCompletedTasksListFragmentInteractionListener {
        void onAllCompletedTasksListFragmentInteraction(DummyItem item);
    }
}
