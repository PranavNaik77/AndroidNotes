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

    RecyclerView recyclerview;
    NotesAdapter notesAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create activity" + this.toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerview = findViewById(R.id.ListofNotes);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        NoteService noteservice = new NoteService(this);    //  binding the service and activity
        getListFromJson();     //(getting the notes data in list format)

        recyclerview.setLayoutManager(linearLayoutManager);    //(getting the manager of the view)
        notesAdapter = new NotesAdapter(NoteService.getNotes(), this);  //(Adapter handle the data collection and bind it to the view and recycler recycles the space as needed)
        setTitle("Android Notes (" + notesAdapter.getItemCount() + ")");
        recyclerview.setAdapter(notesAdapter); //(both recyclerview and notesAdapter are ready now)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("called once");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_of_header, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.notes_info) {
            Log.d("Menu ", "Info menu button pressed!!");
            Intent notes_info = new Intent(this, AboutActivity.class);
            startActivity(notes_info);
            return true;
        }
            else if(item.getItemId()==R.id.notes_add){
                Log.d("Menu ", "add menu button pressed!!");
                Intent notes_add = new Intent(this, AddNoteActivity.class);
                startActivity(notes_add);
                return true;
        }
            else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        Intent edit = new Intent(this, AddNoteActivity.class);
        int position = recyclerview.getChildAdapterPosition(view);

        edit.putExtra("note", NoteService.getNotes().get(position));
        edit.putExtra("position", position);

        startActivity(edit);
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("delete", "delete note");

        Note note = NoteService.getNotes().get(recyclerview.getChildAdapterPosition(view));

        new AlertDialog.Builder(this).setTitle("Confirm Delete `" + note.title + "` ?")
                .setMessage("Are you sure you want to delete `"+ note.title +"` ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NoteService.deleteNote(recyclerview.getChildAdapterPosition(view));
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
            FileOutputStream outputstrem = getApplicationContext().     // getApplication context is used to return the context which is linked to the Application which holds all activities running inside it
                    openFileOutput(getString(R.string.JsonNotes), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(outputstrem);
            printWriter.print(NoteService.getNotes());
            printWriter.close();
            outputstrem.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public ArrayList<Note> getListFromJson() {

        Log.d(TAG, "loadFile: Loading JSON File");
        ArrayList<Note> total_notes = new ArrayList<>();

        try {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.JsonNotes));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);    // converting notes to string
            }

            JSONArray jsonArray = new JSONArray(sb.toString()); // string to JSONArray

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i); //JSONArray to JSONObject

                String title = jsonObject.getString("title");
                String desc = jsonObject.getString("description");
                String date = jsonObject.getString("date");

                total_notes.add(new Note(title, desc, date));    //adding it to the list
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File Not Found: JSON File not found");
        } catch (Exception e) {
            e.printStackTrace();
        }

        NoteService.setNotes(total_notes);
        return total_notes;
    }
}