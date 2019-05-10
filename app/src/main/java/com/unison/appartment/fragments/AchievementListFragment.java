package com.unison.appartment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.adapters.MyAchievementRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.model.Achievement;

public class AchievementListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private RecyclerView.Adapter myAdapter;
    private RecyclerView myRecyclerView;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public AchievementListFragment() {
    }

    @SuppressWarnings("unused")
    public static AchievementListFragment newInstance(int columnCount) {
        AchievementListFragment fragment = new AchievementListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myAdapter = new MyAchievementRecyclerViewAdapter(Achievement.achievementList);
            myRecyclerView.setAdapter(myAdapter);
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
}
