package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//FIXME Rimuovere se non serve
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardListFragment.OnRewardListFragmentInteractionListener;
import com.unison.appartment.model.Reward;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link Reward} e che effettua una
 * chiamata al {@link OnRewardListFragmentInteractionListener listener} specificato
 */
public class MyRewardRecyclerViewAdapter extends ListAdapter<Reward, MyRewardRecyclerViewAdapter.ViewHolderReward> {

    private final static int AVAILABLE_REWARD_ITEM_TYPE = 0;
    private final static int REQUESTED_REWARD_ITEM_TYPE = 1;

    private final OnRewardListFragmentInteractionListener listener;

    public MyRewardRecyclerViewAdapter(OnRewardListFragmentInteractionListener listener) {
        super(MyRewardRecyclerViewAdapter.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderReward onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int resource;
        if (viewType == AVAILABLE_REWARD_ITEM_TYPE){
            resource = R.layout.fragment_reward;
        }
        else {
            resource = R.layout.fragment_requested_reward;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolderRequestedReward(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderReward holder, int position) {
        final Reward rewardItem = getItem(position);

        holder.textNameView.setText(rewardItem.getName());
        if (holder.getItemViewType() == AVAILABLE_REWARD_ITEM_TYPE) {
            ((ViewHolderAvailableReward) holder).textPointsView.setText(String.format(Locale.getDefault(), "%d", rewardItem.getPoints()));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onRewardListFragmentInteraction(rewardItem);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isRequested() ? REQUESTED_REWARD_ITEM_TYPE : AVAILABLE_REWARD_ITEM_TYPE;
    }

    protected abstract class ViewHolderReward extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textNameView;

        private ViewHolderReward(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            textNameView = itemView.findViewById(R.id.fragment_reward_text_name);
        }

    }

    public class ViewHolderAvailableReward extends ViewHolderReward {
        public final TextView textPointsView;

        public ViewHolderAvailableReward(View view) {
            super(view);
            textPointsView = view.findViewById(R.id.fragment_reward_text_points_value);
        }
    }

    public class ViewHolderRequestedReward extends ViewHolderReward {
        public ViewHolderRequestedReward(View view) {
            super(view);
        }
    }

    public static final DiffUtil.ItemCallback<Reward> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Reward>() {
                @Override
                public boolean areItemsTheSame(@NonNull Reward oldReward, @NonNull Reward newReward) {
//                    return oldReward.getId().equals(newReward.getId());
                    // FIXME solo temp
                    return oldReward.equals(newReward);
                }
                @Override
                public boolean areContentsTheSame(@NonNull Reward oldReward, @NonNull Reward newReward) {
                    return oldReward.equals(newReward);
                }
            };

}
