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
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter Adapter} che può visualizzare una lista di {@link UncompletedTask} e che effettua una
 * chiamata al {@link com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener listener} specificato.
 */
public class MyTodoListRecyclerViewAdapter extends ListAdapter<UncompletedTask, RecyclerView.ViewHolder> {

    private final OnTodoListFragmentInteractionListener listener;

    public MyTodoListRecyclerViewAdapter(/*List<UncompletedTask> tasks, */OnTodoListFragmentInteractionListener listener) {
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
//        final UncompletedTask uncompletedTask = tasks.get(position);
        final UncompletedTask uncompletedTask = getItem(position);

        holderTask.taskName.setText(uncompletedTask.getName());
        holderTask.taskDescription.setText(uncompletedTask.getDescription());
        holderTask.taskPoints.setText(String.valueOf(uncompletedTask.getPoints()));

        holderTask.mView.setOnClickListener(new View.OnClickListener() {
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
