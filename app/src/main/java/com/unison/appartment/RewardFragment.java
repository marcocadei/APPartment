package com.unison.appartment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unison.appartment.model.Reward;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRewardListFragmentInteractionListener}
 * interface.
 */
public class RewardFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView.Adapter myAdapter;
    private RecyclerView myRecyclerView;

    private OnRewardListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RewardFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RewardFragment newInstance(int columnCount) {
        RewardFragment fragment = new RewardFragment();
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

        // FIXME da rimuovere
        // Elemento di prova iniziale
        if (Reward.getRewardsList().isEmpty()) {
            Reward.addReward(new Reward("ABC", 127));
            Reward.addReward(new Reward("Prenotato", 500));
            Reward.getReward(1).setRequested(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            myRecyclerView = recyclerView;
        }
        myAdapter = new MyRewardRecyclerViewAdapter(Reward.getRewardsList(), mListener);
        myRecyclerView.setAdapter(myAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnRewardListFragmentInteractionListener) {
            mListener = (OnRewardListFragmentInteractionListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement OnRewardListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        FIXME Rimuovere se non serve
//        mListener = null;
    }

    public void addReward(Reward newReward) {
        Reward.addReward(0, newReward);
        myAdapter.notifyItemInserted(0);
        myRecyclerView.scrollToPosition(0);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRewardListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onRewardListFragmentInteraction(Reward item);
    }
}
