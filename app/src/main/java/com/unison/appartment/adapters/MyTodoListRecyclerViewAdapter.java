package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener;
import com.unison.appartment.state.MyApplication;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link UncompletedTask} e che effettua una
 * chiamata al {@link com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener listener} specificato.
 */
public class MyTodoListRecyclerViewAdapter extends ListAdapter<UncompletedTask, MyTodoListRecyclerViewAdapter.ViewHolderTask> {

    private final OnTodoListFragmentInteractionListener listener;

    public MyTodoListRecyclerViewAdapter(/*List<UncompletedTask> tasks, */OnTodoListFragmentInteractionListener listener) {
        super(MyTodoListRecyclerViewAdapter.DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderTask onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_todo_task, parent, false);
        return new ViewHolderTask(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTask holder, int position) {
        final UncompletedTask uncompletedTask = getItem(position);

        if (uncompletedTask.isAssigned()) {
            Resources res = MyApplication.getAppContext().getResources();
            if (uncompletedTask.getAssignedUserId().equals(new FirebaseAuth().getCurrentUserUid())) {
                holder.taskAssignedUser.setText(res.getString(R.string.fragment_todo_text_assigned_user_self));
            }
            else {
                holder.taskAssignedUser.setText(res.getString(R.string.fragment_todo_text_assigned_user, uncompletedTask.getAssignedUserName()));
            }
            holder.taskAssignedUser.setVisibility(View.VISIBLE);

            if (uncompletedTask.isMarked()) {
                holder.itemIcon.setImageDrawable(res.getDrawable(R.drawable.ic_hourglass_empty, null));
            }
            else {
                holder.itemIcon.setColorFilter(res.getColor(R.color.darkGray, null));
            }
        }
        holder.taskName.setText(uncompletedTask.getName());
        holder.taskDescription.setText(uncompletedTask.getDescription());
        holder.taskPoints.setText(String.valueOf(uncompletedTask.getPoints()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTodoListFragmentOpenTask(uncompletedTask);
                }
            }
        });
    }

    public class ViewHolderTask extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView itemIcon;
        public final TextView taskName;
        public final TextView taskAssignedUser;
        public final TextView taskDescription;
        public final TextView taskPoints;

        public ViewHolderTask(View view) {
            super(view);
            mView = view;
            itemIcon = view.findViewById(R.id.fragment_todo_img_check);
            taskName = view.findViewById(R.id.fragment_todo_text_task_name);
            taskAssignedUser = view.findViewById(R.id.fragment_todo_text_assigned_user);
            taskDescription = view.findViewById(R.id.fragment_todo_text_task_description);
            taskPoints = view.findViewById(R.id.fragment_todo_task_points_value);
        }
    }

    public static final DiffUtil.ItemCallback<UncompletedTask> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UncompletedTask>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UncompletedTask oldUncompletedTask, @NonNull UncompletedTask newUncompletedTask) {
                    // Le proprietà possono cambiare, ma l'id rimane lo stesso
                    return oldUncompletedTask.getId().equals(newUncompletedTask.getId());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull UncompletedTask oldUncompletedTask, @NonNull UncompletedTask newUncompletedTask) {
                    return oldUncompletedTask.equals(newUncompletedTask);
                }
            };
}
