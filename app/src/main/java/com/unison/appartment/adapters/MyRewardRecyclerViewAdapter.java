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
import com.unison.appartment.state.MyApplication;

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
    public void onBindViewHolder(@NonNull final ViewHolderReward holder, int position) {
        final Reward rewardItem = getItem(position);

        holder.textName.setText(rewardItem.getName());
        holder.textDescription.setText(rewardItem.getDescription());
        if (holder.getItemViewType() == AVAILABLE_REWARD_ITEM_TYPE) {
            ((ViewHolderAvailableReward) holder).textPoints.setText(String.format(Locale.getDefault(), "%d", rewardItem.getPoints()));
        }
        else {
            ((ViewHolderRequestedReward) holder).textStatusUpper.setText(MyApplication.getAppContext().getResources().getString(R.string.fragment_reward_text_status_pending_row_1));
            ((ViewHolderRequestedReward) holder).textStatusLower.setText(MyApplication.getAppContext().getResources().getString(R.string.fragment_reward_text_status_pending_row_2));
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
        private View mView;
        private TextView textName;
        private TextView textDescription;

        private ViewHolderReward(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            textName = itemView.findViewById(R.id.fragment_reward_text_name);
            textDescription = itemView.findViewById(R.id.fragment_reward_text_description);
        }

    }

    public class ViewHolderAvailableReward extends ViewHolderReward {
        private final TextView textPoints;

        public ViewHolderAvailableReward(View view) {
            super(view);
            textPoints = view.findViewById(R.id.fragment_reward_text_points_value);
        }
    }

    public class ViewHolderRequestedReward extends ViewHolderReward {
        private final TextView textStatusLower;
        private final TextView textStatusUpper;

        public ViewHolderRequestedReward(View view) {
            super(view);
            textStatusLower = view.findViewById(R.id.fragment_reward_text_points_status_lower);
            textStatusUpper = view.findViewById(R.id.fragment_reward_text_points_status_upper);
        }
    }

    public static final DiffUtil.ItemCallback<Reward> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Reward>() {
                @Override
                public boolean areItemsTheSame(@NonNull Reward oldReward, @NonNull Reward newReward) {
                    return oldReward.getId().equals(newReward.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Reward oldReward, @NonNull Reward newReward) {
                    return oldReward.equals(newReward);
                }
            };

}
