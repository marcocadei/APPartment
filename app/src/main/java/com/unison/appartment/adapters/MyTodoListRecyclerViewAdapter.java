package com.unison.appartment.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.fragments.HomeListFragment;
import com.unison.appartment.model.Task;
import com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener;
import com.unison.appartment.model.UserHome;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} che può visualizzare una lista di  {@link Task} e che effettua una
 * chiamata al {@link com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener} specificato
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTodoListRecyclerViewAdapter extends ListAdapter<Task, RecyclerView.ViewHolder> {

    private final OnTodoListFragmentInteractionListener listener;

    public MyTodoListRecyclerViewAdapter(/*List<Task> tasks, */OnTodoListFragmentInteractionListener listener) {
        super(MyTodoListRecyclerViewAdapter.DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_todo_task, parent, false);
        return new ViewHolderTask(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ViewHolderTask holderTask = (ViewHolderTask) holder;
//        final Task task = tasks.get(position);
        final Task task = getItem(position);

        holderTask.taskName.setText(task.getName());
        holderTask.taskDescription.setText(task.getDescription());
        holderTask.taskPoints.setText(String.valueOf(task.getPoints()));

        holderTask.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTodoListFragmentOpenTask(task);
                }
            }
        });
    }

    public class ViewHolderTask extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView taskName;
        public final TextView taskDescription;
        public final TextView taskPoints;

        public ViewHolderTask(View view) {
            super(view);
            mView = view;
            taskName = view.findViewById(R.id.fragment_todo_text_task_name);
            taskDescription = view.findViewById(R.id.fragment_todo_text_task_description);
            taskPoints = view.findViewById(R.id.fragment_todo_task_points_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + taskName.getText() + "'";
        }
    }

    public static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Task>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Task oldTask, @NonNull Task newTask) {
                    // Le proprietà possono cambiare, ma l'id rimane lo stesso
                    return oldTask.getId().equals(newTask.getId());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Task oldTask, @NonNull Task newTask) {
                    return oldTask.equals(newTask);
                }
            };
}
