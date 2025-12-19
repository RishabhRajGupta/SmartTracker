package com.example.progresstracker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyProgressFragment extends Fragment {

    private EditText etContent, etTopics;
    private Button btnSave;
    private TextView tvDate;
    private DatabaseHelper dbHelper;
    private String currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_progress, container, false);

        dbHelper = new DatabaseHelper(getContext());
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        initViews(view);
        loadTodayEntry();

        return view;
    }

    private void initViews(View view) {
        tvDate = view.findViewById(R.id.tv_date);
        etContent = view.findViewById(R.id.et_content);
        etTopics = view.findViewById(R.id.et_topics);
        btnSave = view.findViewById(R.id.btn_save);

        tvDate.setText("Today: " + currentDate);

        btnSave.setOnClickListener(v -> saveProgress());
    }

    private void loadTodayEntry() {
        Cursor cursor = dbHelper.getDailyProgress(currentDate);
        if (cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex("content");
            int topicsIndex = cursor.getColumnIndex("topics");

            if (contentIndex != -1) {
                etContent.setText(cursor.getString(contentIndex));
            }
            if (topicsIndex != -1) {
                etTopics.setText(cursor.getString(topicsIndex));
            }
        }
        cursor.close();
    }

    private void saveProgress() {
        String content = etContent.getText().toString().trim();
        String topics = etTopics.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Please enter what you learned today", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.addDailyProgress(currentDate, content, topics);

        if (success) {
            Toast.makeText(getContext(), "Progress saved! 🎉", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show();
        }
    }
}