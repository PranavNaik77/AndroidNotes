package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private EditText notes_title;
    private EditText notes_description;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        notes_title = findViewById(R.id.note_Title);
        notes_description = findViewById(R.id.note_description);

        note = (Note) getIntent().getSerializableExtra("note");

        if (note != null) {
            notes_title.setText(note.getTitle());
            notes_description.setText(note.getDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.savemenu, menu);
        return true;
    }

    private String getDate() {
        String updated_time = new SimpleDateFormat("E MMM dd, hh.mm aa ").format(new Date());   //getting today's date and time in string format
        return updated_time;
    }

    private String getTextValue(EditText editText) {
        String text = editText.getText().toString();
        return text;
    }

    private void showEmptyTitleAlert(Intent main) {
        new AlertDialog.Builder(this)
                .setTitle("Please enter title")
                .setMessage("Note will not be saved without a title")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(main);
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    private void Saving_and_Rendering_to_main(String title, String desc) {
        if (note != null) {
            if (!note.title.equals(title) || !note.description.equals(desc)) {
                note.setTitle(title);
                note.setDescription(desc);
                note.setDate(getDate());
                NoteService.updateNote(note, getIntent().getIntExtra("position", -1));
            }
        } else {
            note = new Note(title, desc, getDate());
            NoteService.addNote(note);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.save) {

            String title = getTextValue(findViewById(R.id.note_Title));

            Intent main = new Intent(this, MainActivity.class);

            if (title.isEmpty()) {
                showEmptyTitleAlert(main);
                return super.onOptionsItemSelected(item);
            }

            String desc = getTextValue(findViewById(R.id.note_description));

            Saving_and_Rendering_to_main(title, desc);
            startActivity(main);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEdited(String title, String desc) {

        if (note == null) {
            return !title.isEmpty() || !desc.isEmpty();
        }

        return !title.equals(note.title) || !desc.equals(note.description);
    }



    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);

        String title = getTextValue(findViewById(R.id.note_Title));
        String desc = getTextValue(findViewById(R.id.note_description));

        if (isEdited(title, desc)) {
            new AlertDialog.Builder(this)
                    .setTitle("Save note ?")
                    .setMessage("Your note is not saved, save note `" + getTextValue(findViewById(R.id.note_Title)) + "` ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (title.isEmpty()) {
                                Toast.makeText(AddNoteActivity.this, "Please enter Note title", Toast.LENGTH_SHORT).show();
                            } else {
                                Saving_and_Rendering_to_main(title, desc);
                                startActivity(main);
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(main);
                }
            }).show();
        } else {
            startActivity(main);
        }
    }
}