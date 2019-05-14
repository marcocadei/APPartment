package com.unison.appartment.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.fragments.HomeListFragment;
import com.unison.appartment.fragments.HomeListFragment.OnHomeListFragmentInteractionListener;
import com.unison.appartment.model.UserHome;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} che può visualizzare una lista di  {@link UserHome} e che effettua una
 * chiamata al {@link HomeListFragment.OnHomeListFragmentInteractionListener} specificato
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHomeRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeRecyclerViewAdapter.ViewHolderHome> {

    private final List<UserHome> homesList;
    private final HomeListFragment.OnHomeListFragmentInteractionListener mListener;

    public MyHomeRecyclerViewAdapter(List<UserHome> items, OnHomeListFragmentInteractionListener listener) {
        homesList = items;
        mListener = listener;
    }

    @Override
    public ViewHolderHome onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home, parent, false);
        return new ViewHolderHome(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderHome holder, int position) {
        final UserHome item = homesList.get(position);
        holder.mNameView.setText(homesList.get(position).getHomeName());
        holder.mRoleView.setText(homesList.get(position).getRole());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHomeListFragmentInteraction(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return homesList.size();
    }

    public class ViewHolderHome extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRoleView;

        public ViewHolderHome(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.fragment_home_text_name);
            mRoleView = view.findViewById(R.id.fragment_home_text_role);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
