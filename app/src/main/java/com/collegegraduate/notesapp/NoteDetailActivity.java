
package com.collegegraduate.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Note currentNote;
    private boolean isNewNote = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleEditText = findViewById(R.id.edit_title);
        contentEditText = findViewById(R.id.edit_content);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("note")) {
            currentNote = (Note) getIntent().getSerializableExtra("note");
            isNewNote = false;
            populateFields();
        } else {
            currentNote = new Note();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Note");
            }
        }
    }

    private void populateFields() {
        titleEditText.setText(currentNote.getTitle());
        contentEditText.setText(currentNote.getContent());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_detail, menu);
        if (isNewNote) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            if (deleteItem != null) {
                deleteItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            checkAndSave();
            return true;
        } else if (id == R.id.action_save) {
            saveNote();
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkAndSave();
    }

    private void checkAndSave() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (!title.isEmpty() || !content.isEmpty()) {
            // Ask if user wants to save changes
            new AlertDialog.Builder(this)
                    .setTitle("Save Changes")
                    .setMessage("Do you want to save changes to this note?")
                    .setPositiveButton("Save", (dialog, which) -> saveNote())
                    .setNegativeButton("Discard", (dialog, which) -> finish())
                    .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            finish();
        }
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setTimestamp(System.currentTimeMillis());

        Intent resultIntent = new Intent();
        resultIntent.putExtra("note", currentNote);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("note", currentNote);
        resultIntent.putExtra("isDeleted", true);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
