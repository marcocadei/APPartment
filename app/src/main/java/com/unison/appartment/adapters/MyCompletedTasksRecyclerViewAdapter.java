package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.fragments.AllCompletedTasksListFragment.OnAllCompletedTasksListFragmentInteractionListener;
import com.unison.appartment.model.CompletedTask;

public class MyCompletedTasksRecyclerViewAdapter extends ListAdapter<CompletedTask, MyCompletedTasksRecyclerViewAdapter.ViewHolderCompletedTask> {

    private final OnAllCompletedTasksListFragmentInteractionListener mListener;

    public MyCompletedTasksRecyclerViewAdapter(OnAllCompletedTasksListFragmentInteractionListener listener) {
        super(MyCompletedTasksRecyclerViewAdapter.DIFF_CALLBACK);
        mListener = listener;
    }

    @Override
    public ViewHolderCompletedTask onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_completedtask, parent, false);
        return new ViewHolderCompletedTask(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderCompletedTask holder, int position) {
        final CompletedTask completedTask = getItem(position);
        Resources res = holder.itemView.getResources();

       holder.taskName.setText(completedTask.getName());
       holder.taskDescription.setText(completedTask.getLastDescription());
       holder.textStatusUpper.setText(String.valueOf(completedTask.getLastPoints()));
       holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_extra_large));
       holder.textStatusLower.setText(R.string.general_points_name);

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
    }

    public class ViewHolderCompletedTask extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView taskName;
        public final TextView taskDescription;
        public final TextView textStatusLower;
        public final TextView textStatusUpper;

        public ViewHolderCompletedTask(View view) {
            super(view);
            mView = view;
            taskName = view.findViewById(R.id.fragment_done_text_task_name);
            taskDescription = view.findViewById(R.id.fragment_done_text_task_description);
            textStatusUpper = view.findViewById(R.id.fragment_done_task_points_value);
            textStatusLower = view.findViewById(R.id.fragment_done_task_points_label);
        }
    }

    public static final DiffUtil.ItemCallback<CompletedTask> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CompletedTask>() {
                @Override
                public boolean areItemsTheSame(@NonNull CompletedTask oldItem, @NonNull CompletedTask newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull CompletedTask oldItem, @NonNull CompletedTask newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
