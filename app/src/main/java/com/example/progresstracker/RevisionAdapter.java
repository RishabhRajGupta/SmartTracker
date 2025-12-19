package com.example.progresstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RevisionAdapter extends RecyclerView.Adapter<RevisionAdapter.RevisionViewHolder> {

    private List<String> topics;
    private OnTopicRevisedListener revisedListener;

    public interface OnTopicRevisedListener {
        void onRevised(String topic);
    }

    public RevisionAdapter(List<String> topics, OnTopicRevisedListener revisedListener) {
        this.topics = topics;
        this.revisedListener = revisedListener;
    }

    @NonNull
    @Override
    public RevisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revision, parent, false);
        return new RevisionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevisionViewHolder holder, int position) {
        String topic = topics.get(position);
        holder.bind(topic);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class RevisionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopic;
        Button btnMarkRevised;

        public RevisionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tv_revision_topic);
            btnMarkRevised = itemView.findViewById(R.id.btn_mark_revised);
        }

        public void bind(String topic) {
            tvTopic.setText(topic);

            btnMarkRevised.setOnClickListener(v -> {
                if (revisedListener != null) {
                    revisedListener.onRevised(topic);
                }
            });
        }
    }
}
