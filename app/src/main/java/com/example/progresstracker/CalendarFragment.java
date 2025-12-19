package com.example.progresstracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvStats, tvSelectedDate;
    private DatabaseHelper dbHelper;
    private Set<String> completedDates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        dbHelper = new DatabaseHelper(getContext());
        completedDates = new HashSet<>();

        initViews(view);
        loadCompletedDates();
        updateStats();

        return view;
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        tvStats = view.findViewById(R.id.tv_stats);
        tvSelectedDate = view.findViewById(R.id.tv_selected_date);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            showDateInfo(date);
        });
    }

    private void loadCompletedDates() {
        completedDates.clear();
        Cursor cursor = dbHelper.getAllCompletedDates();

        while (cursor.moveToNext()) {
            String date = cursor.getString(0);
            completedDates.add(date);
        }
        cursor.close();
    }

    private void updateStats() {
        int totalDays = completedDates.size();
        tvStats.setText("Total Active Days: " + totalDays + " 🔥\nKeep up the great work!");
    }

    private void showDateInfo(String date) {
        if (completedDates.contains(date)) {
            Cursor cursor = dbHelper.getDailyProgress(date);
            if (cursor.moveToFirst()) {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                tvSelectedDate.setText("✓ " + date + "\n\n" + content);
                tvSelectedDate.setBackgroundColor(Color.parseColor("#E8F5E9"));
            }
            cursor.close();
        } else {
            tvSelectedDate.setText(date + "\n\nNo activity recorded");
            tvSelectedDate.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }
    }
}