package com.example.progresstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private List<Goal> goals;
    private OnGoalCheckedListener checkedListener;
    private OnGoalDeleteListener deleteListener;

    public interface OnGoalCheckedListener {
        void onChecked(int goalId, boolean checked);
    }

    public interface OnGoalDeleteListener {
        void onDelete(int goalId);
    }

    public GoalsAdapter(List<Goal> goals, OnGoalCheckedListener checkedListener, OnGoalDeleteListener deleteListener) {
        this.goals = goals;
        this.checkedListener = checkedListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbGoal;
        TextView tvTitle, tvDescription;
        ImageButton btnDelete;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            cbGoal = itemView.findViewById(R.id.cb_goal);
            tvTitle = itemView.findViewById(R.id.tv_goal_title);
            tvDescription = itemView.findViewById(R.id.tv_goal_description);
            btnDelete = itemView.findViewById(R.id.btn_delete_goal);
        }

        public void bind(Goal goal) {
            tvTitle.setText(goal.getTitle());
            tvDescription.setText(goal.getDescription());
            cbGoal.setChecked(goal.isCompletedToday());

            cbGoal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkedListener != null) {
                    checkedListener.onChecked(goal.getId(), isChecked);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(goal.getId());
                }
            });
        }
    }
}
