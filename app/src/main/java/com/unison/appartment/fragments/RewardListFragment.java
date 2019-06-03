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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.adapters.MyRewardRecyclerViewAdapter;
import com.unison.appartment.R;
import com.unison.appartment.model.Reward;
import com.unison.appartment.viewmodel.RewardViewModel;

import java.util.List;

/**
 * Fragment che rappresenta una lista di Reward
 * Le Activity che contengono questo fragment devono implementare l'interfaccia {@link RewardListFragment.OnRewardListFragmentInteractionListener}
 */
public class RewardListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private RewardViewModel viewModel;

    private ListAdapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnRewardListFragmentInteractionListener listener;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public RewardListFragment() {
    }

    @SuppressWarnings("unused")
    public static RewardListFragment newInstance(int columnCount) {
        RewardListFragment fragment = new RewardListFragment();
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

        viewModel = ViewModelProviders.of(getActivity()).get(RewardViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            myRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            myAdapter = new MyRewardRecyclerViewAdapter(listener);
            myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    myRecyclerView.smoothScrollToPosition(positionStart);
                }
            });
            myRecyclerView.setAdapter(myAdapter);

            readRewards();
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnRewardListFragmentInteractionListener) {
            listener = (OnRewardListFragmentInteractionListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement OnRewardListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void readRewards() {
        LiveData<List<Reward>> rewardLiveData = viewModel.getRewardLiveData();
        rewardLiveData.observe(getViewLifecycleOwner(), new Observer<List<Reward>>() {
            @Override
            public void onChanged(List<Reward> rewards) {
                myAdapter.submitList(rewards);
                listener.onRewardListElementsLoaded(rewards.size());
            }
        });
    }

    public void addReward(Reward newReward) {
        viewModel.addReward(newReward);
    }

    public void deleteReward(String id) {
        viewModel.deleteReward(id);
    }

    public void editReward(Reward reward) {
        viewModel.editReward(reward);
    }

    public void requestReward(String rewardId, String userId, String userName){
        viewModel.requestReward(rewardId, userId, userName);
    }

    public void cancelRequest(String rewardId){
        viewModel.cancelRequest(rewardId);
    }

    public void confirmRequest(Reward reward, String userId) {
        viewModel.confirmRequest(reward, userId);
    }


    /**
     * Questa interfaccia deve essere implementata dalle activity che contengono questo
     * fragment, per consentire al fragment di comunicare eventuali interazioni all'activity
     * che a sua volta può comunicare con altri fragment
     */
    public interface OnRewardListFragmentInteractionListener {
        void onRewardListFragmentInteraction(Reward item);
        void onRewardListElementsLoaded(long elements);
    }
}
