package com.example.progresstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private OnNoteLinkClickListener linkClickListener;

    public interface OnNoteLinkClickListener {
        void onLinkClick(String url);
    }

    public NotesAdapter(List<Note> notes, OnNoteLinkClickListener linkClickListener) {
        this.notes = notes;
        this.linkClickListener = linkClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageButton btnOpenLink;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvDate = itemView.findViewById(R.id.tv_note_date);
            btnOpenLink = itemView.findViewById(R.id.btn_open_link);
        }

        public void bind(Note note) {
            tvTitle.setText(note.getTitle());
            tvDate.setText(note.getDate());

            btnOpenLink.setOnClickListener(v -> {
                if (linkClickListener != null) {
                    linkClickListener.onLinkClick(note.getDriveLink());
                }
            });

            itemView.setOnClickListener(v -> {
                if (linkClickListener != null) {
                    linkClickListener.onLinkClick(note.getDriveLink());
                }
            });
        }
    }
}
