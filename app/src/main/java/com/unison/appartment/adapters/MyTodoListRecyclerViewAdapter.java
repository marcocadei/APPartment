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
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.UncompletedTask;
import com.unison.appartment.fragments.TodoListFragment.OnTodoListFragmentInteractionListener;
import com.unison.appartment.state.Appartment;

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

        Resources res = holder.itemView.getResources();
        /*
        Resetto le view ai valori di default (solo per i campi che non sono comunque resettati ad un
        altro valore) in modo che se il ViewHolder è stato riciclato non mi trovo risultati strani.
         */
        holder.itemIcon.setColorFilter(res.getColor(R.color.colorPrimaryDark, null));
        holder.taskName.setText(uncompletedTask.getName());
        holder.taskDescription.setText(uncompletedTask.getDescription());
        holder.textStatusUpper.setText(String.valueOf(uncompletedTask.getPoints()));
        holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_extra_large));
        holder.textStatusLower.setText(R.string.general_points_name);

        /*
        Il reset di icona e campi di testo sono inseriti in questo if per evitare glitch grafici
        nel caso in cui il task sia assegnato (quando viene aggiornato un elemento, il rispettivo
        list item è modificato due volte in pochi istanti originando un brutto effetto grafico).
         */
        if (!uncompletedTask.isAssigned()) {
            holder.itemIcon.setImageDrawable(res.getDrawable(R.drawable.ic_check, null));
            holder.taskAssignedUser.setVisibility(View.GONE);
        }
        else {
            holder.itemIcon.setImageDrawable(res.getDrawable(R.drawable.ic_check_circle, null));
            if (uncompletedTask.getAssignedUserId().equals(new FirebaseAuth().getCurrentUserUid())) {
                holder.taskAssignedUser.setText(res.getString(R.string.fragment_todo_text_assigned_user_self));
            } else {
                holder.taskAssignedUser.setText(res.getString(R.string.fragment_todo_text_assigned_user, uncompletedTask.getAssignedUserName()));
                holder.itemIcon.setColorFilter(res.getColor(R.color.darkGray, null));
                holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_medium));
                holder.textStatusUpper.setText(R.string.fragment_todo_text_status_assigned_to_other_row_1);
                holder.textStatusLower.setText(R.string.fragment_todo_text_status_assigned_to_other_row_2);
            }
            holder.taskAssignedUser.setVisibility(View.VISIBLE);
        }

        if (uncompletedTask.isMarked()) {
            holder.itemIcon.setImageDrawable(res.getDrawable(R.drawable.ic_hourglass_empty, null));
            holder.textStatusUpper.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.text_medium));
            if (Appartment.getInstance().getUserHome().getRole() == Home.ROLE_SLAVE) {
                if (uncompletedTask.getAssignedUserId().equals(new FirebaseAuth().getCurrentUserUid())) {
                    holder.textStatusUpper.setText(R.string.fragment_todo_text_status_requested_row_1);
                    holder.textStatusLower.setText(R.string.fragment_todo_text_status_requested_row_2);
                }
            } else {
                holder.textStatusUpper.setText(R.string.fragment_todo_text_status_pending_row_1);
                holder.textStatusLower.setText(R.string.fragment_todo_text_status_pending_row_2);
            }
        }

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
        public final TextView textStatusLower;
        public final TextView textStatusUpper;

        public ViewHolderTask(View view) {
            super(view);
            mView = view;
            itemIcon = view.findViewById(R.id.fragment_todo_img_check);
            taskName = view.findViewById(R.id.fragment_todo_text_task_name);
            taskAssignedUser = view.findViewById(R.id.fragment_todo_text_assigned_user);
            taskDescription = view.findViewById(R.id.fragment_todo_text_task_description);
            textStatusUpper = view.findViewById(R.id.fragment_todo_task_points_value);
            textStatusLower = view.findViewById(R.id.fragment_todo_task_points_label);
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
