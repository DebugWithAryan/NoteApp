package com.collegegraduate.notesapp;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private Context context;

    public NoteAdapter(Context context) {
        this.context = context;
        this.notes = new ArrayList<>();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.dateTextView.setText(note.getFormattedDate());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("note", note);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void addNote(Note note) {
        notes.add(0, note);
        notifyItemInserted(0);
    }

    public void updateNote(Note updatedNote) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updatedNote.getId())) {
                notes.set(i, updatedNote);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeNote(String noteId) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(noteId)) {
                notes.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title);
            dateTextView = itemView.findViewById(R.id.note_date);
        }
    }
}
