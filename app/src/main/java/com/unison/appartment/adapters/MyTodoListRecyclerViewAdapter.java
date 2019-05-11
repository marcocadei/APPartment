package com.unison.appartment.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.model.Task;
import com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener;

import java.util.List;


public class MyTodoListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Task> tasks;
    private final OnTodoListFragmentInteractionListener listener;

    public MyTodoListRecyclerViewAdapter(List<Task> tasks, OnTodoListFragmentInteractionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        this.notifyDataSetChanged();
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
        final Task task = tasks.get(position);
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

    @Override
    public int getItemCount() {
        return tasks.size();
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
}
