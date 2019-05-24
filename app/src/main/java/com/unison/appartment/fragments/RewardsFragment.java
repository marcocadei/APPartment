package com.unison.appartment.fragments;

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
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.R;
import com.unison.appartment.activities.CreateRewardActivity;
import com.unison.appartment.activities.RewardDetailActivity;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.Reward;
import com.unison.appartment.state.Appartment;

/**
 * Le Activity che contengono questo fragment devono implementare l'interface
 * {@link RewardListFragment.OnRewardListFragmentInteractionListener} per gestire gli eventi di interazione
 */
public class RewardsFragment extends Fragment implements RewardListFragment.OnRewardListFragmentInteractionListener {

    private static final int ADD_REWARD_REQUEST_CODE = 1;
    private static final int DETAIL_REWARD_REQUEST_CODE = 2;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public RewardsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RewardsFragment.
     */
    public static RewardsFragment newInstance() {
        return new RewardsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        FloatingActionButton floatAdd = view.findViewById(R.id.fragments_reward_float_add);
        if (Appartment.getInstance().getUserHome().getRole() == Home.ROLE_SLAVE) {
            // Se l'utente è uno slave, non viene visualizzato il bottone per aggiungere un nuovo premio.
            floatAdd.hide();
        } else {
            /*
            In caso contrario, viene impostato l'onClickListener per il FAB che permette di aggiungere
            un nuovo premio.
             */
            floatAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), CreateRewardActivity.class);
                    startActivityForResult(i, ADD_REWARD_REQUEST_CODE);
                }
            });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REWARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                RewardListFragment listFragment = (RewardListFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
                listFragment.addReward((Reward) data.getSerializableExtra(CreateRewardActivity.EXTRA_NEW_REWARD));
            }
        } else if (requestCode == DETAIL_REWARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                RewardListFragment listFragment = (RewardListFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
                switch (data.getIntExtra(RewardDetailActivity.EXTRA_OPERATION_TYPE, -1)) {
                    case RewardDetailActivity.OPERATION_DELETE:
                        listFragment.deleteReward(data.getStringExtra(RewardDetailActivity.EXTRA_REWARD_ID));
                        break;
                    case RewardDetailActivity.OPERATION_RESERVE:
                        listFragment.requestReward(data.getStringExtra(RewardDetailActivity.EXTRA_REWARD_ID),
                                data.getStringExtra(RewardDetailActivity.EXTRA_USER_ID),
                                data.getStringExtra(RewardDetailActivity.EXTRA_USER_NAME));
                        break;
                    default:
                        Log.e(getClass().getCanonicalName(), "Operation type non riconosciuto");
                }
            }
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

    @Override
    public void onRewardListFragmentInteraction(Reward item) {
        Intent i = new Intent(getActivity(), RewardDetailActivity.class);
        i.putExtra(RewardDetailActivity.EXTRA_REWARD_OBJECT, item);
        startActivityForResult(i, DETAIL_REWARD_REQUEST_CODE);
    }

    @Override
    public void onRewardListElementsLoaded(long elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_rewards_progress);
        progressBar.setVisibility(View.GONE);
    }

}
