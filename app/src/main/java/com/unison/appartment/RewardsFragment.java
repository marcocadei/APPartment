package com.unison.appartment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unison.appartment.model.Reward;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the interface
 * to handle interaction events.
 * Use the {@link RewardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardsFragment extends Fragment implements RewardFragment.OnRewardListFragmentInteractionListener {

    private static final int ADD_REWARD_REQUEST_CODE = 0xA1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    FIXME Rimuovere se non serve
//    private OnFragmentInteractionListener mListener;

    public RewardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RewardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardsFragment newInstance(String param1, String param2) {
        RewardsFragment fragment = new RewardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        /*
        Impostazione dell'onClickListener per il floating action button che permette di aggiungere
        un nuovo premio.
         */
        FloatingActionButton floatAdd = view.findViewById(R.id.fragments_reward_float_add);
        floatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateRewardActivity.class);
                startActivityForResult(i, ADD_REWARD_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REWARD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                RewardFragment fragmentRewardList = (RewardFragment) getChildFragmentManager()
                        .findFragmentById(R.id.fragment_rewards_fragment_reward_list);
                fragmentRewardList.addReward((Reward)data.getSerializableExtra("rewardObject"));
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        FIXME Rimuovere se non serve
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        FIXME Rimuovere se non serve
//        mListener = null;
    }

    @Override
    public void onRewardListFragmentInteraction(Reward item) {
        Intent i = new Intent(getActivity(), RewardDetailActivity.class);
        i.putExtra("rewardObject", item);
        startActivity(i);
    }


//    FIXME Rimuovere se non serve
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
