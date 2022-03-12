package com.example.androidnotes;

import java.util.ArrayList;

public class NoteService {
    private static ArrayList<Note> notes = new ArrayList<>();
    static MainActivity mainActivity;

    public NoteService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static void setNotes(ArrayList<Note> newNotes) {
        notes = newNotes;
    }

    public static void addNote(Note note) {
        notes.add(note);
        mainActivity.saveNotes();
    }

    public static ArrayList<Note> getNotes() {
        return notes;
    }

    public static void deleteNote(int position) {
        notes.remove(position);
        mainActivity.saveNotes();
    }

    public static void updateNote(Note note, int position) {
        if (position != -1) {
            notes.add(note);
            notes.remove(position);
            mainActivity.saveNotes();
        }
    }

}
