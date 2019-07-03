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
import com.google.android.material.snackbar.Snackbar;
import com.unison.appartment.R;
import com.unison.appartment.activities.CreateRewardActivity;
import com.unison.appartment.activities.RewardDetailActivity;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.Reward;
import com.unison.appartment.state.Appartment;


public class RewardsFragment extends Fragment implements RewardListFragment.OnRewardListFragmentInteractionListener {

    public final static String EXTRA_NEW_REWARD = "newReward";
    public final static String EXTRA_REWARD_DATA = "rewardData";
    public final static String EXTRA_USER_NAME = "userName";
    public final static String EXTRA_USER_ID = "userId";
    public final static String EXTRA_OPERATION_TYPE = "operationType";
    public final static int OPERATION_DELETE = 0;
    public final static int OPERATION_RESERVE = 1;
    public final static int OPERATION_CANCEL = 2;
    public final static int OPERATION_CONFIRM = 3;

    private static final int ADD_REWARD_REQUEST_CODE = 1;
    private static final int DETAIL_REWARD_REQUEST_CODE = 2;

    private View emptyListLayout;

    private boolean snackbarShown = false;

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

        emptyListLayout = view.findViewById(R.id.fragment_rewards_layout_empty_list);

        final FloatingActionButton floatAdd = view.findViewById(R.id.fragments_reward_float_add);
        if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() == Home.ROLE_SLAVE) {
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
                    if (Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() == Home.ROLE_SLAVE) {
                        floatAdd.hide();
                        RewardListFragment listFragment = (RewardListFragment) getChildFragmentManager()
                                .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
                        listFragment.refresh();
                        View snackbarView = getActivity().findViewById(R.id.fragment_rewards);
                        Snackbar.make(snackbarView, getString(R.string.snackbar_downgrade_error_message),
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        Intent i = new Intent(getContext(), CreateRewardActivity.class);
                        startActivityForResult(i, ADD_REWARD_REQUEST_CODE);
                    }
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
                listFragment.addReward((Reward) data.getParcelableExtra(EXTRA_NEW_REWARD));
            }
        } else if (requestCode == DETAIL_REWARD_REQUEST_CODE) {
            RewardListFragment listFragment = (RewardListFragment) getChildFragmentManager()
                    .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
            if (resultCode == RewardDetailActivity.RESULT_OK) {
                switch (data.getIntExtra(EXTRA_OPERATION_TYPE, -1)) {
                    case OPERATION_DELETE:
                        Reward rewardToDelete = data.getParcelableExtra(EXTRA_REWARD_DATA);
                        if (rewardToDelete.isRequested()) {
                            listFragment.cancelAndDelete(rewardToDelete);
                        }
                        else {
                            listFragment.deleteReward(rewardToDelete.getId(), rewardToDelete.getVersion());
                        }
                        break;
                    case OPERATION_RESERVE:
                        listFragment.requestReward((Reward)data.getParcelableExtra(EXTRA_REWARD_DATA),
                                data.getStringExtra(EXTRA_USER_ID),
                                data.getStringExtra(EXTRA_USER_NAME));
                        break;
                    case OPERATION_CANCEL:
                        listFragment.cancelRequest((Reward)data.getParcelableExtra(EXTRA_REWARD_DATA));
                        break;
                    case OPERATION_CONFIRM:
                        Reward rewardToConfirm = data.getParcelableExtra(EXTRA_REWARD_DATA);
                        if (!rewardToConfirm.isRequested()) {
                            listFragment.requestAndConfirm(rewardToConfirm, data.getStringExtra(EXTRA_USER_ID),
                                    data.getStringExtra(EXTRA_USER_NAME));
                        }
                        else {
                            listFragment.confirmRequest(rewardToConfirm, data.getStringExtra(EXTRA_USER_ID));
                        }
                        break;
                    default:
                        Log.e(getClass().getCanonicalName(), "Operation type non riconosciuto");
                }
            } else if (resultCode == RewardDetailActivity.RESULT_EDITED) {
                listFragment.editReward((Reward)data.getParcelableExtra(EXTRA_NEW_REWARD));
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
        i.putExtra(RewardDetailActivity.EXTRA_REWARD_OBJECT, item);
        startActivityForResult(i, DETAIL_REWARD_REQUEST_CODE);
    }

    @Override
    public void onRewardListElementsLoaded(long elements) {
        // Sia che la lista abbia elementi o meno, una volta fatta la lettura la
        // progress bar deve interrompersi
        ProgressBar progressBar = getView().findViewById(R.id.fragment_rewards_progress);
        progressBar.setVisibility(View.GONE);

        // Se gli elementi sono 0 allora mostro un testo che lo indichi all'utente
        if (elements == 0) {
            emptyListLayout.setVisibility(View.VISIBLE);
        } else {
            emptyListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRewardListError(boolean error) {
        // È necessario controllare se la snackbar è già mostrata perché alcune operazioni
        // in realtà fanno chiamate diverse al repository e ognuna dà errore. Pertanto senza
        // questo controllo si vederebbe uno strano glitch dovuto al fatto che si mostrano
        // molto velocemente due (o più) snackbar di fila
        if (error && !snackbarShown) {
            View snackbarView = getActivity().findViewById(R.id.fragment_rewards);
            final Snackbar snackbar = Snackbar.make(snackbarView, getString(R.string.snackbar_rewards_error_message),
                    Snackbar.LENGTH_LONG);
            // snackbarShown mi serve che diventi subito true, ma con la callback non lo diventa subito
            // perché c'è l'animazione
            snackbarShown = true;
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    snackbarShown = false;
                }

                @Override
                public void onShown(Snackbar transientBottomBar) {
                    snackbarShown = true;
                }
            });
            snackbar.show();
        }
    }
}
