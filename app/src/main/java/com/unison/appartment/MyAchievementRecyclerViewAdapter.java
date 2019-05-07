package com.unison.appartment;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unison.appartment.model.Achievement;

import java.util.List;


public class MyAchievementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Achievement> achievements;

    public MyAchievementRecyclerViewAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_achievement, parent, false);
        return new ViewHolderAchievement(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ViewHolderAchievement holderAchievement = (ViewHolderAchievement) holder;
        final Achievement achievement = achievements.get(position);
        holderAchievement.achievementName.setText(achievement.getName());
        holderAchievement.achievementDescription.setText(achievement.getDescription());
        /*holderAchievement.achievementImage.setText(String.valueOf(achievement.getImage()));*/

        /*holderAchievement.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTodoListFragmentOpenTask(task);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public class ViewHolderAchievement extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView achievementName;
        public final TextView achievementDescription;
        public final ImageView achievementImage;

        public ViewHolderAchievement(View view) {
            super(view);
            mView = view;
            achievementName = view.findViewById(R.id.fragment_achievement_text_name);
            achievementDescription = view.findViewById(R.id.fragment_achievement_text_description);
            achievementImage = view.findViewById(R.id.fragment_achievement_img_logo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + achievementName.getText() + "'";
        }
    }
}
