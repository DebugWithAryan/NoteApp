package com.collegegraduate.notesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton addButton = findViewById(R.id.fab_add_note);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);

        loadNotes();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                } catch (Exception e) {
                    Log.e(TAG, "Error launching NoteDetailActivity", e);
                    Toast.makeText(MainActivity.this,
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Note note = (Note) data.getSerializableExtra("note");
            boolean isDeleted = data.getBooleanExtra("isDeleted", false);

            if (requestCode == REQUEST_CODE_ADD_NOTE) {
                noteList.add(0, note);
                noteAdapter.addNote(note);
                recyclerView.smoothScrollToPosition(0);
                saveNotes();
                Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                if (isDeleted) {
                    for (int i = 0; i < noteList.size(); i++) {
                        if (noteList.get(i).getId().equals(note.getId())) {
                            noteList.remove(i);
                            noteAdapter.removeNote(note.getId());
                            break;
                        }
                    }
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < noteList.size(); i++) {
                        if (noteList.get(i).getId().equals(note.getId())) {
                            noteList.set(i, note);
                            noteAdapter.updateNote(note);
                            break;
                        }
                    }
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
                }
                saveNotes();
            }
        }
    }

    private void loadNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("notes_pref", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notes", null);
        Type type = new TypeToken<ArrayList<Note>>() {}.getType();

        noteList = gson.fromJson(json, type);

        if (noteList == null) {
            noteList = new ArrayList<>();
        }

        noteAdapter.setNotes(noteList);
    }

    private void saveNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences("notes_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(noteList);
        editor.putString("notes", json);
        editor.apply();
    }
}
