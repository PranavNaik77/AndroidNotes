package com.example.androidnotes;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private List<Note> notes;
    private MainActivity mainActivity;

    public NotesRecyclerViewAdapter(List<Note> notes, MainActivity mainActivity) {
        this.notes = notes;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notelist, parent, false);

        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);

        return new ViewHolder(view);
    }

    private String getObfuscatedDesc(String desc) {
        String description = desc.replaceAll("\n", " ");
        if (description.length() > 80) {
            return description.substring(0, 80) + "...";
        }
        return description;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d("helper", "in BindVeiwHolder()");

        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.date.setText(note.getDate());
        holder.description.setText(getObfuscatedDesc(note.getDescription()));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);
        }

    }
}

