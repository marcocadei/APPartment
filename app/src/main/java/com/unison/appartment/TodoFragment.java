package com.unison.appartment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.model.Task;


/**
 * Fragment che rappresenta la lista delle attività da svolgere
 */
public class TodoFragment extends Fragment {

    private static final int ADD_TASK_REQUEST_CODE = 1;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public TodoFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
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
        final View myView = inflater.inflate(R.layout.fragment_todo, container, false);

        final FloatingActionButton floatAddTask = myView.findViewById(R.id.fragment_todo_float_add_task);
        floatAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateTaskActivity.class);
                startActivityForResult(i, ADD_TASK_REQUEST_CODE);
            }
        });

        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            TodoListFragment tlf = (TodoListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_todo_todolist);
            tlf.addTask((Task) data.getSerializableExtra("newTask"));
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
}
