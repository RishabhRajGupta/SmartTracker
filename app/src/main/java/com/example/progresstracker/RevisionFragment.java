package com.example.progresstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevisionFragment extends Fragment {
    private RecyclerView rvRevision;
    private TextView tvRevisionInfo;
    private DatabaseHelper dbHelper;
    private List<String> revisionTopics;
    private RevisionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_revision, container, false);

        dbHelper = new DatabaseHelper(getContext());

        initViews(view);
        loadRevisionTopics();

        return view;
    }

    private void initViews(View view) {
        rvRevision = view.findViewById(R.id.rv_revision);
        tvRevisionInfo = view.findViewById(R.id.tv_revision_info);

        rvRevision.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadRevisionTopics() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        revisionTopics = dbHelper.getTopicsDueForRevision(currentDate);

        if (revisionTopics.isEmpty()) {
            tvRevisionInfo.setText("No topics due for revision today! 🎉\n\nYou're all caught up!");
        } else {
            tvRevisionInfo.setText("Topics due for revision today:");
            adapter = new RevisionAdapter(revisionTopics, this::markAsRevised);
            rvRevision.setAdapter(adapter);
        }
    }

    private void markAsRevised(String topicWithInterval) {
        // Extract topic name from "Topic (Interval: X days)"
        String topic = topicWithInterval.split(" \\(")[0];
        String intervalStr = topicWithInterval.split("Interval: ")[1].split(" ")[0];
        int currentInterval = Integer.parseInt(intervalStr);

        dbHelper.updateRevisionSchedule(topic, currentInterval);
        Toast.makeText(getContext(), "Marked as revised! Next revision scheduled.", Toast.LENGTH_SHORT).show();
        loadRevisionTopics();
    }
}
