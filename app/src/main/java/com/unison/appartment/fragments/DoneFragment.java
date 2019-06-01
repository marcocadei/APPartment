package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.unison.appartment.R;
import com.unison.appartment.model.CompletedTask;


public class DoneFragment extends Fragment implements AllCompletedTasksListFragment.OnAllCompletedTasksListFragmentInteractionListener {

    private TextView emptyTodoListTitle;
    private TextView emptyTodoListText;
    private TabLayout tabLayout;
    private final static int ALL_COMPLETEDTASKS_POSITION = 0;
    private final static int RECENT_COMPLETEDTASKS_POSITION = 1;


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
        AllCompletedTasksListFragment listFragment = (AllCompletedTasksListFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_done_fragment_done_list);
        switch (position) {
            default:
            case ALL_COMPLETEDTASKS_POSITION:
                switchToFragment(AllCompletedTasksListFragment.class);
                break;
            case RECENT_COMPLETEDTASKS_POSITION:
                switchToFragment(RecentCompletedTasksListFragment.class);
                break;
        }
    }

    private void switchToFragment(Class fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        /*if (currentPosition > lastPosition) {
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        }
        lastPosition = currentPosition;*/
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
