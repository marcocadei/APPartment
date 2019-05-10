package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//FIXME Rimuovere se non serve
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardFragment.OnRewardListFragmentInteractionListener;
import com.unison.appartment.model.Reward;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRewardRecyclerViewAdapter extends RecyclerView.Adapter<MyRewardRecyclerViewAdapter.ViewHolderReward> {

    private final static int AVAILABLE_REWARD_ITEM_TYPE = 0;
    private final static int REQUESTED_REWARD_ITEM_TYPE = 1;

    private final List<Reward> rewardsList;
    private final OnRewardListFragmentInteractionListener mListener;

    public MyRewardRecyclerViewAdapter(List<Reward> items, OnRewardListFragmentInteractionListener listener) {
        rewardsList = items;
        mListener = listener;
    }

    @Override
    public ViewHolderReward onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == AVAILABLE_REWARD_ITEM_TYPE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_reward, parent, false);
            return new ViewHolderAvailableReward(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_requested_reward, parent, false);
            return new ViewHolderRequestedReward(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolderReward holder, int position) {
        final Reward rewardItem = rewardsList.get(position);
        holder.textNameView.setText(rewardItem.getName());
        if (holder.getItemViewType() == AVAILABLE_REWARD_ITEM_TYPE) {
            ((ViewHolderAvailableReward) holder).textPointsView.setText(String.format(Locale.getDefault(), "%d", rewardItem.getPoints()));
        }

        // FIXME Questo if serve a far sì che possano essere visualizzati i dettagli solo dei premi richiedibili:
        // cambiare se non serve. Oppure modificare il liste
//        if (!rewardItem.isRequested()) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onRewardListFragmentInteraction(rewardItem);
                    }
                }
            });
//        }
    }

    @Override
    public int getItemCount() {
        return rewardsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return rewardsList.get(position).isRequested() ? REQUESTED_REWARD_ITEM_TYPE : AVAILABLE_REWARD_ITEM_TYPE;
    }

    protected abstract class ViewHolderReward extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textNameView;

        private ViewHolderReward(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            textNameView = (TextView) itemView.findViewById(R.id.fragment_reward_text_name);
        }

    }

    public class ViewHolderAvailableReward extends ViewHolderReward {
        public final TextView textPointsView;

        public ViewHolderAvailableReward(View view) {
            super(view);
            textPointsView = (TextView) view.findViewById(R.id.fragment_reward_text_points_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textNameView.getText() + "'";
        }
    }

    public class ViewHolderRequestedReward extends ViewHolderReward {

        public ViewHolderRequestedReward(View view) {
            super(view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textNameView.getText() + "'";
        }
    }

}
