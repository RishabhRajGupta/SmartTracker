package com.example.progresstracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progresstracker.network.ApiClient;
import com.example.progresstracker.network.GoalApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalsFragment extends Fragment {

    private static final String TAG = "GoalsFragment";

    private RecyclerView rvGoals;
    private FloatingActionButton fabAddGoal;
    private GoalsAdapter adapter;
    private List<Goal> goalsList;
    private GoalApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        apiService = ApiClient.getClient().create(GoalApiService.class);
        goalsList = new ArrayList<>();

        initViews(view);
        loadGoalsFromBackend();

        return view;
    }

    private void initViews(View view) {
        rvGoals = view.findViewById(R.id.rv_goals);
        fabAddGoal = view.findViewById(R.id.fab_add_goal);

        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GoalsAdapter(goalsList, this::onGoalChecked, this::onGoalDelete);
        rvGoals.setAdapter(adapter);

        fabAddGoal.setOnClickListener(v -> showAddGoalDialog());
    }

    private void loadGoalsFromBackend() {
        apiService.getAllGoals().enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goalsList.clear();
                    goalsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load goals. Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to load goals", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Log.e(TAG, "Error loading goals", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddGoalDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_goal, null);
        EditText etTitle = dialogView.findViewById(R.id.et_goal_title);
        EditText etDescription = dialogView.findViewById(R.id.et_goal_description);

        new AlertDialog.Builder(getContext())
                .setTitle("Add New Goal")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (!title.isEmpty()) {
                        Goal newGoal = new Goal(title, description);
                        createGoalOnBackend(newGoal);
                    } else {
                        Toast.makeText(getContext(), "Please enter a goal title", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createGoalOnBackend(Goal goal) {
        apiService.createGoal(goal).enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Goal added!", Toast.LENGTH_SHORT).show();
                    loadGoalsFromBackend();
                } else {
                    // Improved error logging
                    Log.e(TAG, "Failed to add goal. Response code: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(getContext(), "Failed to add goal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                // Improved error logging
                Log.e(TAG, "Error adding goal", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onGoalChecked(Goal goal, boolean isChecked) {
        goal.setCompleted(isChecked);
        updateGoalOnBackend(goal);
    }

    private void updateGoalOnBackend(Goal goal) {
        apiService.updateGoalStatus(goal.getId(), goal).enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (!response.isSuccessful()) {
                    goal.setCompleted(!goal.isCompleted());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Failed to update goal status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                goal.setCompleted(!goal.isCompleted());
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onGoalDelete(long goalId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteGoalFromBackend(goalId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteGoalFromBackend(long goalId) {
        apiService.deleteGoal(goalId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
                    loadGoalsFromBackend();
                } else {
                    Log.e(TAG, "Failed to delete goal. Code: " + response.code());
                    Toast.makeText(getContext(), "Failed to delete goal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting goal", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}