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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.viewmodel.TodoTaskViewModel;
import com.unison.appartment.adapters.MyTodoListRecyclerViewAdapter;
import com.unison.appartment.R;

import java.util.List;

/**
 * Fragment che rappresenta una lista di task
 */
public class TodoListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private TodoTaskViewModel viewModel;

    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnTodoListFragmentInteractionListener listener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public TodoListFragment() {
    }

    @SuppressWarnings("unused")
    public static TodoListFragment newInstance(int columnCount) {
        TodoListFragment fragment = new TodoListFragment();
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

        viewModel = ViewModelProviders.of(getActivity()).get(TodoTaskViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean error) {
                listener.onTodoListError(error);
            }
        });

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyTodoListRecyclerViewAdapter(/*UncompletedTask.TASKS*//*uncompletedTasks,*/ listener);
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    super.onItemRangeInserted(positionStart, itemCount);
                    // Finché i task sono ordinati per data di inserimento positionStart è sempre uguale a 0
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readUncompletedTasks();
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnTodoListFragmentInteractionListener) {
            listener = (OnTodoListFragmentInteractionListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTodoListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void readUncompletedTasks() {
        LiveData<List<UncompletedTask>> taskLiveData = viewModel.getTaskLiveData();
        taskLiveData.observe(getViewLifecycleOwner(), new Observer<List<UncompletedTask>>() {
            @Override
            public void onChanged(List<UncompletedTask> uncompletedTasks) {
                myAdapter.submitList(uncompletedTasks);
                listener.onTodoListElementsLoaded(uncompletedTasks.size());
                Log.d("provaListAdapter", "aggiunto");
            }
        });
    }

    public void refresh() {
        myAdapter.notifyDataSetChanged();
    }

    public void addTask(UncompletedTask newUncompletedTask) {
        viewModel.addTask(newUncompletedTask);
    }

    public void editTask(UncompletedTask newUncompletedTask) {
        viewModel.editTask(newUncompletedTask);
    }

    public void deleteTask(String id) {
        viewModel.deleteTask(id);
    }

    public void assignTask(String taskId, String userId, String userName) {
        viewModel.assignTask(taskId, userId, userName);
    }

    public void removeAssignment(String taskId, String assignedUserId) {
        viewModel.removeAssignment(taskId, assignedUserId);
    }

    public void markTask(String taskId, String userId, String userName) {
        viewModel.markTask(taskId, userId, userName);
    }

    public void cancelCompletion(String taskId, String userId) {
        viewModel.cancelCompletion(taskId, userId);
    }

    public void confirmCompletion(UncompletedTask task, String assignedUserId) {
        viewModel.confirmCompletion(task, assignedUserId);
    }

    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnTodoListFragmentInteractionListener {
        void onTodoListFragmentOpenTask(UncompletedTask uncompletedTask);
        void onTodoListElementsLoaded(long elements);
        void onTodoListError(boolean error);
    }
}
