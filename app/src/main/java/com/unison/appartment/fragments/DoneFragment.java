package com.unison.appartment.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.unison.appartment.activities.CompletedTaskDetailActivity;
import com.unison.appartment.R;
import com.unison.appartment.model.CompletedTask;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.viewmodel.TodoTaskViewModel;


public class DoneFragment extends Fragment implements AllCompletedTasksListFragment.OnAllCompletedTasksListFragmentInteractionListener {

    public final static String EXTRA_TASK_NAME = "taskName";
    public final static String EXTRA_OPERATION_TYPE = "operationType";
    public final static int OPERATION_DELETE = 0;

    private final static int DETAIL_COMPLETED_TASK_REQUEST_CODE = 1;

    private final static int ALL_COMPLETEDTASKS_POSITION = 0;
    private final static int RECENT_COMPLETEDTASKS_POSITION = 1;

    // Ultima voce selezionata nei tab
    private int lastPosition = ALL_COMPLETEDTASKS_POSITION;
    // Voce attualmente selezionata nei tab
    private int currentPosition = ALL_COMPLETEDTASKS_POSITION;

    private View emptyListLayout;

    // Questo ViewModel è necessario perché a partire da un'attività completata è possibile crearne
    // una nuova
    private TodoTaskViewModel viewModel;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public DoneFragment() {
    }

    public static DoneFragment newInstance() {
       return new DoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(TodoTaskViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myView = inflater.inflate(R.layout.fragment_done, container, false);

        emptyListLayout = myView.findViewById(R.id.fragment_done_layout_empty_list);
        TabLayout tabLayout = myView.findViewById(R.id.fragment_done_tabs);
        updateFragmentContent(tabLayout.getSelectedTabPosition(), false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateFragmentContent(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // TODO
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return myView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateFragmentContent(int position, boolean animation) {
        switch (position) {
            default:
            case ALL_COMPLETEDTASKS_POSITION:
                currentPosition = ALL_COMPLETEDTASKS_POSITION;
                switchToFragment(AllCompletedTasksListFragment.class, animation);
                break;
            case RECENT_COMPLETEDTASKS_POSITION:
                currentPosition = RECENT_COMPLETEDTASKS_POSITION;
                switchToFragment(RecentCompletedTasksListFragment.class, animation);
                break;
        }
    }

    private void switchToFragment(Class fragment, boolean animation) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        // Se è richiesta effettuo un'animazione nel passaggio da un fragment all'altro
        if (animation) {
            if (currentPosition > lastPosition) {
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
                ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            }
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
        i.putExtra(CompletedTaskDetailActivity.EXTRA_COMPLETED_TASK_OBJECT, completedTask);
        startActivityForResult(i, DETAIL_COMPLETED_TASK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_COMPLETED_TASK_REQUEST_CODE) {
            if (resultCode == CompletedTaskDetailActivity.RESULT_CREATED) {
                viewModel.addTask((UncompletedTask) data.getSerializableExtra(TodoFragment.EXTRA_NEW_TASK));
            }
            else if (resultCode == CompletedTaskDetailActivity.RESULT_OK) {
                if (data.getIntExtra(EXTRA_OPERATION_TYPE, -1) == OPERATION_DELETE) {
                    // FIXME codice completamente da ristrutturare se si eliminano i tab
                    // (Se invece si lasciano i tab cambiare i due CompletedTasksListFragment mettendo
                    // un'interfaccia o una superclasse comune perché così il codice fa schifo)

                    Fragment listFragment = getChildFragmentManager().findFragmentById(R.id.fragment_done_fragment_done_list);
                    String taskName = data.getStringExtra(EXTRA_TASK_NAME);
                    if (currentPosition == ALL_COMPLETEDTASKS_POSITION) {
                        ((AllCompletedTasksListFragment) listFragment).deleteCompletedTask(taskName);
                    }
                    else {
                        ((RecentCompletedTasksListFragment) listFragment).deleteCompletedTask(taskName);
                    }
                }
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
            emptyListLayout.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
        }
    }
}
