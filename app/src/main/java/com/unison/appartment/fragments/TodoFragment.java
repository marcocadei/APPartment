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
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.R;
import com.unison.appartment.activities.CreateTaskActivity;
import com.unison.appartment.activities.TaskDetailActivity;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.state.Appartment;

/**
 * Fragment che rappresenta una lista di attività da svolgere
 */
public class TodoFragment extends Fragment implements TodoListFragment.OnTodoListFragmentInteractionListener {

    public final static String EXTRA_TASK_OBJECT = "taskObject";
    public final static String EXTRA_NEW_TASK = "newTask";
    public final static String EXTRA_TASK_ID = "taskId";
    public final static String EXTRA_USER_NAME = "userName";
    public final static String EXTRA_USER_ID = "userId";
    public final static String EXTRA_TASK = "task";
    public final static String EXTRA_OPERATION_TYPE = "operationType";
    public final static int OPERATION_DELETE = 0;
    public final static int OPERATION_ASSIGN = 1;
    public final static int OPERATION_REMOVE_ASSIGNMENT = 2;
    public final static int OPERATION_MARK_AS_COMPLETED = 3;
    public final static int OPERATION_UNMARK = 4;
    public final static int OPERATION_CONFIRM_COMPLETION = 5;

    private static final int ADD_TASK_REQUEST_CODE = 1;
    private static final int DETAIL_TASK_REQUEST_CODE = 2;

    private View emptyListLayout;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public TodoFragment() {
    }

    public static TodoFragment newInstance() {
        return new TodoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_todo, container, false);

        emptyListLayout = myView.findViewById(R.id.fragment_todo_layout_empty_list);

        final FloatingActionButton floatAddTask = myView.findViewById(R.id.fragment_todo_float_add_task);
        if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() == Home.ROLE_SLAVE) {
            // Se l'utente è uno slave, non viene visualizzato il bottone per aggiungere un nuovo task.
            floatAddTask.hide();
        } else {
            /*
            In caso contrario, viene impostato l'onClickListener per il FAB che permette di aggiungere
            un nuovo task.
             */
            floatAddTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), CreateTaskActivity.class);
                    startActivityForResult(i, ADD_TASK_REQUEST_CODE);
                }
            });
        }

        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                TodoListFragment tlf = (TodoListFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_todo_todolist);
                tlf.addTask((UncompletedTask) data.getSerializableExtra(EXTRA_NEW_TASK));
            }
        }
        else if (requestCode == DETAIL_TASK_REQUEST_CODE) {
            TodoListFragment listFragment = (TodoListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_todo_todolist);
            if (resultCode == TaskDetailActivity.RESULT_OK) {
                switch (data.getIntExtra(EXTRA_OPERATION_TYPE, -1)) {
                    case OPERATION_DELETE:
                        // FIXME così come pensato per il RewardsFragment, prima di fare la delete
                        // annullare il mark-as-complete e annullare anche l'assegnamento all'utente
                        listFragment.deleteTask(data.getStringExtra(EXTRA_TASK_ID));
                        break;
                    case OPERATION_ASSIGN:
                        listFragment.assignTask(data.getStringExtra(EXTRA_TASK_ID),
                                data.getStringExtra(EXTRA_USER_ID),
                                data.getStringExtra(EXTRA_USER_NAME));
                        break;
                    case OPERATION_REMOVE_ASSIGNMENT:
                        listFragment.removeAssignment(data.getStringExtra(EXTRA_TASK_ID));
                        break;
                    case OPERATION_MARK_AS_COMPLETED:
                        listFragment.markTask(data.getStringExtra(EXTRA_TASK_ID),
                                data.getStringExtra(EXTRA_USER_ID),
                                data.getStringExtra(EXTRA_USER_NAME));
                        break;
                    case OPERATION_UNMARK:
                        listFragment.cancelCompletion(data.getStringExtra(EXTRA_TASK_ID));
                        break;
                    case OPERATION_CONFIRM_COMPLETION:
                        listFragment.confirmCompletion((UncompletedTask)data.getSerializableExtra(EXTRA_TASK),
                                data.getStringExtra(EXTRA_USER_ID));
                        break;
                    default:
                        Log.e(getClass().getCanonicalName(), "Operation type non riconosciuto");
                }
            }
        }
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
    public void onTodoListFragmentOpenTask(UncompletedTask uncompletedTask) {
        Intent i = new Intent(getActivity(), TaskDetailActivity.class);
        i.putExtra(EXTRA_TASK_OBJECT, uncompletedTask);
        startActivityForResult(i, DETAIL_TASK_REQUEST_CODE);
    }

    @Override
    public void onTodoListElementsLoaded(long elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_todo_progress);
        progressBar.setVisibility(View.GONE);

        // Se gli elementi sono 0 allora mostro un testo che lo indichi all'utente
        if (elements == 0) {
            emptyListLayout.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
        }
    }
}
