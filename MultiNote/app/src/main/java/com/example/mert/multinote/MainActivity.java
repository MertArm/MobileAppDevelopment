package com.example.mert.multinote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private Notes currentNote;
    private List<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new AsyncReader().execute(getString(R.string.file_name));
    }

    private class AsyncReader extends AsyncTask<String, Notes, List<Notes>> {
        @Override protected List<Notes> doInBackground(String... params) {
            String fileName = params[0];

            try {
                InputStream is = getApplicationContext().openFileInput(fileName);
                JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

                reader.beginArray();
                while(reader.hasNext()) {
                    publishProgress(loadNote(reader));
                }
                reader.endArray();
                reader.close();
            }
            catch(FileNotFoundException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            return notesList;
        }

        protected void onProgressUpdate(Notes... loadedNotes) {
            Notes loadedNote = loadedNotes[0];
            notesList.add(loadedNote);
        }

        protected void onPostExecute(List<Notes> loadedNoteList) {
            notesAdapter.notifyDataSetChanged();
        }
    }

    private Notes loadNote(JsonReader reader) {
        String title = "", description = "", date = "";
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("title")) {
                    title = reader.nextString();
                }
                else if(name.equals("description")) {
                    description = reader.nextString();
                }
                else if(name.equals("date")) {
                    date = reader.nextString();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        if(title == null || description == null || date == null) {
            return null;
        }

        return new Notes(title, description, date);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFile();
    }

    private void saveFile() {
        try {
            FileOutputStream fos = getApplicationContext()
                    .openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();

            for(Notes n: notesList)
                saveNote(writer, n);

            writer.endArray();
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNote(JsonWriter writer, Notes note) {
        try {
            writer.beginObject();
            writer.name("title").value(note.getTitle());
            writer.name("description").value(note.getDescription());
            writer.name("date").value(note.getDate());
            writer.endObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.getMenuInflater().inflate(R.menu.newnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.info_page:
                Intent infoIntent = new Intent(this, AboutActivity.class);
                startActivity(infoIntent);
                return true;

            case R.id.edit_note:
                Intent newNoteIntent = new Intent(this, EditActivity.class);
                startActivityForResult(newNoteIntent, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        currentNote = notesList.get(position);
        Intent modifyNoteIntent = new Intent(MainActivity.this, EditActivity.class);
        modifyNoteIntent.putExtra("CURRENT_NOTE", currentNote);
        startActivityForResult(modifyNoteIntent, 2);
    }

    public boolean onLongClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        currentNote = notesList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note \'" + currentNote.getTitle() + "\'?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                notesList.remove(currentNote);
                currentNote = null;
                notesAdapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Note has been deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void onActivityResult(int req, int res, Intent intent) {
        if(req == 2) {
            if(res == RESULT_OK) {
                Notes updatedNote = (Notes) intent.getSerializableExtra("UPDATED_NOTE");
                notesList.remove(currentNote);
                currentNote = updatedNote;
                notesList.add(0, currentNote);
                notesAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if(req == 1) {
            if(res == RESULT_OK) {
                Notes newNote = (Notes) intent.getSerializableExtra("NEW_NOTE");
                currentNote = newNote;
                notesList.add(0, newNote);
                notesAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            }
        }
    }
}