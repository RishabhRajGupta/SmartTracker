package com.example.progresstracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

public class NotesFragment extends Fragment {

    private RecyclerView rvNotes;
    private FloatingActionButton fabAddNote;
    private NotesAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Note> notesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        dbHelper = new DatabaseHelper(getContext());
        notesList = new ArrayList<>();

        initViews(view);
        loadNotes();

        return view;
    }

    private void initViews(View view) {
        rvNotes = view.findViewById(R.id.rv_notes);
        fabAddNote = view.findViewById(R.id.fab_add_note);

        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotesAdapter(notesList, this::openDriveLink);
        rvNotes.setAdapter(adapter);

        fabAddNote.setOnClickListener(v -> showAddNoteDialog());
    }

    private void loadNotes() {
        notesList.clear();
        Cursor cursor = dbHelper.getAllNotes();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String driveLink = cursor.getString(cursor.getColumnIndex("drive_link"));

            notesList.add(new Note(id, date, title, driveLink));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showAddNoteDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_note, null);
        EditText etTitle = dialogView.findViewById(R.id.et_note_title);
        EditText etDriveLink = dialogView.findViewById(R.id.et_drive_link);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        new AlertDialog.Builder(getContext())
                .setTitle("Add Note Link")
                .setMessage("Upload your notes to Drive first, then paste the link here")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String driveLink = etDriveLink.getText().toString().trim();

                    if (!title.isEmpty() && !driveLink.isEmpty()) {
                        long id = dbHelper.addNote(currentDate, title, driveLink);
                        if (id != -1) {
                            Toast.makeText(getContext(), "Note link saved!", Toast.LENGTH_SHORT).show();
                            loadNotes();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openDriveLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }
}