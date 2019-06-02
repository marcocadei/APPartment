package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.unison.appartment.CompletedTaskDetailActivity;
import com.unison.appartment.R;
import com.unison.appartment.activities.CreateTaskActivity;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.viewmodel.TodoTaskViewModel;


public class DoneFragment extends Fragment implements AllCompletedTasksListFragment.OnAllCompletedTasksListFragmentInteractionListener {

    public final static String EXTRA_COMPLETEDTASK_OBJECT = "completedTaskObject";
    private static final int DETAIL_COMPLETED_TASK_REQUEST_CODE = 1;

    private TextView emptyTodoListTitle;
    private TextView emptyTodoListText;
    private TabLayout tabLayout;
    private final static int ALL_COMPLETEDTASKS_POSITION = 0;
    private final static int RECENT_COMPLETEDTASKS_POSITION = 1;

    // Ultima voce selezionata nella bottom navigation
    private int lastPosition = ALL_COMPLETEDTASKS_POSITION;
    // Voce attualmente selezionata nella bottom navigation
    private int currentPosition = ALL_COMPLETEDTASKS_POSITION;

    // Questo ViewModel è necessario perché a partire da un'attività completata è possibile crearne
    // una nuova
    private TodoTaskViewModel viewModel;


    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public DoneFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RewardsFragment.
     */
    public static DoneFragment newInstance(String param1, String param2) {
       return new DoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TodoTaskViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.fragment_done, container, false);
        emptyTodoListTitle = myView.findViewById(R.id.fragment_done_empty_completedtask_list_title);
        emptyTodoListText = myView.findViewById(R.id.fragment_done_empty_completedtask_list_text);
        tabLayout = myView.findViewById(R.id.fragment_done_tabs);
        updateFragmentContent(tabLayout.getSelectedTabPosition());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateFragmentContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Inflate the layout for this fragment
        return myView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateFragmentContent(int position) {
        switch (position) {
            default:
            case ALL_COMPLETEDTASKS_POSITION:
                currentPosition = ALL_COMPLETEDTASKS_POSITION;
                switchToFragment(AllCompletedTasksListFragment.class);
                break;
            case RECENT_COMPLETEDTASKS_POSITION:
                currentPosition = RECENT_COMPLETEDTASKS_POSITION;
                switchToFragment(RecentCompletedTasksListFragment.class);
                break;
        }
    }

    private void switchToFragment(Class fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (currentPosition > lastPosition) {
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        }
        lastPosition = currentPosition;
        try {
            ft.replace(R.id.fragment_done_fragment_done_list, (Fragment) fragment.newInstance());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        ft.commit();
    }

    @Override
    public void onAllCompletedTasksListFragmentInteraction(CompletedTask completedTask) {
        Intent i = new Intent(getActivity(), CompletedTaskDetailActivity.class);
        i.putExtra(EXTRA_COMPLETEDTASK_OBJECT, completedTask);
        startActivityForResult(i, DETAIL_COMPLETED_TASK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_COMPLETED_TASK_REQUEST_CODE) {
            if (resultCode == CompletedTaskDetailActivity.RESULT_CREATED) {
                viewModel.addTask((UncompletedTask) data.getSerializableExtra(TodoFragment.EXTRA_NEW_TASK));
            }
        }
    }

    @Override
    public void onAllCompletedTasksListElementsLoaded(int elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_done_progress);
        progressBar.setVisibility(View.GONE);

        // Se gli elementi sono 0 allora mostro un testo che lo indichi all'utente
        if (elements == 0) {
            emptyTodoListTitle.setVisibility(View.VISIBLE);
            emptyTodoListText.setVisibility(View.VISIBLE);
        } else {
            emptyTodoListTitle.setVisibility(View.GONE);
            emptyTodoListText.setVisibility(View.GONE);
        }
    }
}
