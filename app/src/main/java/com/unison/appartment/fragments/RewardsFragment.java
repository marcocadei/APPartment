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

    // FIXME poi probabilmente con la lettura dal db questo diventerà un REWARD_ID o REWARD_NAME
    public final static String EXTRA_REWARD_OBJECT = "rewardObject";
    public final static String EXTRA_NEW_REWARD = "newReward";
    public final static String EXTRA_REWARD_ID = "rewardId";
    public final static String EXTRA_USER_NAME = "userName";
    public final static String EXTRA_USER_ID = "userId";
    public final static String EXTRA_OPERATION_TYPE = "operationType";
    public final static int OPERATION_DELETE = 0;
    public final static int OPERATION_RESERVE = 1;
    public final static int OPERATION_CANCEL = 2;
    public final static int OPERATION_CONFIRM = 3;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        FloatingActionButton floatAdd = view.findViewById(R.id.fragments_reward_float_add);
        if (Appartment.getInstance().getHomeUser().getRole() == Home.ROLE_SLAVE) {
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
                listFragment.addReward((Reward) data.getSerializableExtra(EXTRA_NEW_REWARD));
            }
        } else if (requestCode == DETAIL_REWARD_REQUEST_CODE) {
            RewardListFragment listFragment = (RewardListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
            if (resultCode == RewardDetailActivity.RESULT_OK) {
                switch (data.getIntExtra(EXTRA_OPERATION_TYPE, -1)) {
                    case OPERATION_DELETE:
                        // FIXME per ora la cancelRequest è inutile perchè il delete cancella già tutto il nodo, però
                        // in futuro vogliamo che con il cancel l'utente venga notificato o qualcos'altro
                        // Se si decide di lasciare il cancel perché serve, farsi mandare un altro extra
                        // per controllare se il premio è stato richiesto
                        listFragment.cancelRequest(data.getStringExtra(EXTRA_REWARD_ID));
                        listFragment.deleteReward(data.getStringExtra(EXTRA_REWARD_ID));
                        break;
                    case OPERATION_RESERVE:
                        listFragment.requestReward(data.getStringExtra(EXTRA_REWARD_ID),
                                data.getStringExtra(EXTRA_USER_ID),
                                data.getStringExtra(EXTRA_USER_NAME));
                        break;
                    case OPERATION_CANCEL:
                        listFragment.cancelRequest(data.getStringExtra(EXTRA_REWARD_ID));
                        break;
                    case OPERATION_CONFIRM:
                        listFragment.confirmRequest(data.getStringExtra(EXTRA_REWARD_ID),
                                data.getStringExtra(EXTRA_USER_ID));
                        break;
                    default:
                        Log.e(getClass().getCanonicalName(), "Operation type non riconosciuto");
                }
            } else if (resultCode == RewardDetailActivity.RESULT_EDITED) {
                Log.d("MODIFICATO", ((Reward)data.getSerializableExtra(EXTRA_NEW_REWARD)).getId());
                listFragment.editReward((Reward)data.getSerializableExtra(EXTRA_NEW_REWARD));
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
    public void onRewardListFragmentInteraction(Reward item) {
        Intent i = new Intent(getActivity(), RewardDetailActivity.class);
        i.putExtra(EXTRA_REWARD_OBJECT, item);
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
