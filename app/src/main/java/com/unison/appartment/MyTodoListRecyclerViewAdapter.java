package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unison.appartment.model.Task;

import java.util.List;


public class MyTodoListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Task> tasks;

    public MyTodoListRecyclerViewAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_todo_task, parent, false);
        return new ViewHolderTask(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolderTask holderTask = (ViewHolderTask) holder;
        Task task = tasks.get(position);
        holderTask.taskName.setText(task.getName());
        holderTask.taskDescription.setText(task.getDescription());
        holderTask.taskPoints.setText(String.valueOf(task.getPoints()));
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
            taskName = view.findViewById(R.id.fragment_todo_task_name);
            taskDescription = view.findViewById(R.id.fragment_todo_task_description);
            taskPoints = view.findViewById(R.id.fragment_todo_task_points_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + taskName.getText() + "'";
        }
    }
}
