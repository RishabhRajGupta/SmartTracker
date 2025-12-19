package com.example.progresstracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsFragment extends Fragment {

    private RecyclerView rvGoals;
    private FloatingActionButton fabAddGoal;
    private GoalsAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Goal> goalsList;
    private String currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        dbHelper = new DatabaseHelper(getContext());
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        goalsList = new ArrayList<>();

        initViews(view);
        loadGoals();

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

    private void loadGoals() {
        goalsList.clear();
        Cursor cursor = dbHelper.getAllGoals();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            boolean completedToday = dbHelper.isGoalCompletedToday(id, currentDate);

            goalsList.add(new Goal(id, title, description, completedToday));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
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
                        long id = dbHelper.addGoal(title, description);
                        if (id != -1) {
                            Toast.makeText(getContext(), "Goal added!", Toast.LENGTH_SHORT).show();
                            loadGoals();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please enter a goal title", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onGoalChecked(int goalId, boolean checked) {
        dbHelper.updateGoalProgress(goalId, currentDate, checked);
        Toast.makeText(getContext(), checked ? "Goal completed! ✓" : "Goal unchecked", Toast.LENGTH_SHORT).show();
    }

    private void onGoalDelete(int goalId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteGoal(goalId)) {
                        Toast.makeText(getContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
                        loadGoals();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}