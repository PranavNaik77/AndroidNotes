package com.example.androidnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    RecyclerView notesView;
    NotesRecyclerViewAdapter notesAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create activity" + this.toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesView = findViewById(R.id.noteList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        new NoteService(this);
        getNotesFromJson();

        notesView.setLayoutManager(linearLayoutManager);
        notesAdapter = new NotesRecyclerViewAdapter(NoteService.getNotes(), this);
        setTitle("Android Notes (" + notesAdapter.getItemCount() + ")");
        notesView.setAdapter(notesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("called once");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.headermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Log.d("Menu ", "Info menu button pressed!!");
                Intent info = new Intent(this, AboutActivity.class);
                startActivity(info);
                return true;
            case R.id.add:
                Log.d("Menu ", "add menu button pressed!!");
                Intent addNote = new Intent(this, AddNoteActivity.class);
                startActivity(addNote);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Intent edit = new Intent(this, AddNoteActivity.class);
        int position = notesView.getChildAdapterPosition(view);

        edit.putExtra("note", NoteService.getNotes().get(position));
        edit.putExtra("position", position);

        startActivity(edit);
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("delete", "delete note");

        Note note = NoteService.getNotes().get(notesView.getChildAdapterPosition(view));

        new AlertDialog.Builder(this).setTitle("Confirm Delete `" + note.title + "` ?")
                .setMessage("Are you sure you want to delete `"+ note.title +"` ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NoteService.deleteNote(notesView.getChildAdapterPosition(view));
                        setTitle("Android Notes (" + notesAdapter.getItemCount() + ")");
                        notesAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No", null).show();

        return true;
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    public void saveNotes() {
        Log.d(TAG, "saveNote: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.NotesJson), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(NoteService.getNotes());
            printWriter.close();
            fos.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public ArrayList<Note> getNotesFromJson() {

        Log.d(TAG, "loadFile: Loading JSON File");
        ArrayList<Note> notes = new ArrayList<>();

        try {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.NotesJson));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("title");
                String desc = jsonObject.getString("description");
                String date = jsonObject.getString("date");

                notes.add(new Note(title, desc, date));
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File Not Found: JSON File not found");
        } catch (Exception e) {
            e.printStackTrace();
        }

        NoteService.setNotes(notes);
        return notes;
    }
}