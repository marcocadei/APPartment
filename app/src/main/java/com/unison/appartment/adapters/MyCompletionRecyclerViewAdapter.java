package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.model.Completion;

import java.util.Date;

public class MyCompletionRecyclerViewAdapter extends ListAdapter<Completion, MyCompletionRecyclerViewAdapter.ViewHolderCompletion> {

    public MyCompletionRecyclerViewAdapter() {
        super(MyCompletionRecyclerViewAdapter.DIFF_CALLBACK);
    }

    @Override
    public ViewHolderCompletion onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_completion, parent, false);
        return new ViewHolderCompletion(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderCompletion holder, int position) {
        final Completion completion = getItem(position);
        Resources res = holder.itemView.getResources();

        java.text.DateFormat dateFormat = DateFormat.getDateFormat(holder.itemView.getContext());
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(holder.itemView.getContext());
        Date timestamp = new Date(completion.getCompletionDate());
        holder.completionDate.setText(res.getString(R.string.fragment_post_datetime_format, dateFormat.format(timestamp), timeFormat.format(timestamp)));

        holder.completionUser.setText(completion.getUser());
        holder.textStatusUpper.setText(String.valueOf(completion.getPoints()));
        holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_extra_large));
        holder.textStatusLower.setText(R.string.general_points_name);
    }

    public class ViewHolderCompletion extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView completionUser;
        public final TextView completionDate;
        public final TextView textStatusLower;
        public final TextView textStatusUpper;

        public ViewHolderCompletion(View view) {
            super(view);
            mView = view;
            completionUser = view.findViewById(R.id.fragment_completion_text_user);
            completionDate = view.findViewById(R.id.fragment_completion_text_date);
            textStatusUpper = view.findViewById(R.id.fragment_completion_task_points_value);
            textStatusLower = view.findViewById(R.id.fragment_completion_task_points_label);
        }
    }

    public static final DiffUtil.ItemCallback<Completion> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Completion>() {
                @Override
                public boolean areItemsTheSame(@NonNull Completion oldItem, @NonNull Completion newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull Completion oldItem, @NonNull Completion newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
