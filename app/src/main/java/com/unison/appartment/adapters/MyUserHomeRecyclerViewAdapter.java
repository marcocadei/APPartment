package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.fragments.UserHomeListFragment;
import com.unison.appartment.fragments.UserHomeListFragment.OnHomeListFragmentInteractionListener;
import com.unison.appartment.model.UserHome;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link UserHome} e che effettua una
 * chiamata al {@link UserHomeListFragment.OnHomeListFragmentInteractionListener listener} specificato.
 */
public class MyUserHomeRecyclerViewAdapter extends ListAdapter<UserHome, RecyclerView.ViewHolder> {

    private final UserHomeListFragment.OnHomeListFragmentInteractionListener mListener;

    public MyUserHomeRecyclerViewAdapter(OnHomeListFragmentInteractionListener listener) {
        super(MyUserHomeRecyclerViewAdapter.DIFF_CALLBACK);
        mListener = listener;
    }

    @Override
    public ViewHolderHome onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_userhome, parent, false);
        return new ViewHolderHome(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ViewHolderHome holderHome = (ViewHolderHome) holder;
        // final UserHome item = homesList.get(position);
        final UserHome userHome = getItem(position);
        String[] roles = holderHome.mView.getContext().getResources().getStringArray(R.array.desc_userhomes_uid_homename_role_values);
        holderHome.mNameView.setText(userHome.getHomename());
        holderHome.mRoleView.setText(roles[userHome.getRole()]);

        holderHome.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHomeListFragmentInteraction(userHome);
                }
            }
        });
    }

    public class ViewHolderHome extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRoleView;

        public ViewHolderHome(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.fragment_userhome_text_name);
            mRoleView = view.findViewById(R.id.fragment_userhome_text_role);
        }
    }

    public static final DiffUtil.ItemCallback<UserHome> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UserHome>() {
                @Override
                public boolean areItemsTheSame(@NonNull UserHome oldItem, @NonNull UserHome newItem) {
                    return oldItem.getHomename().equals(newItem.getHomename());
                }

                @Override
                public boolean areContentsTheSame(@NonNull UserHome oldItem, @NonNull UserHome newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
