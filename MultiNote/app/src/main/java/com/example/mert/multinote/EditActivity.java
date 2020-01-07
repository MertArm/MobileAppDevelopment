package com.example.mert.multinote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editDesc;
    private Notes note;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        editTitle =  findViewById(R.id.editTitle);
        editDesc = findViewById(R.id.editDesc);
        note = new Notes(null, null, null);

        Intent intent = getIntent();
        if(intent.hasExtra("CURRENT_NOTE")) {
            note = (Notes) intent.getSerializableExtra("CURRENT_NOTE");
            editTitle.setText(note.getTitle());
            editDesc.setText(note.getDescription());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.saveButton:
                if(editTitle.getText().toString().length() == 0) {
                    Toast.makeText(this, "Cannot save an untitled note", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                else {
                    Notes newNote = new Notes(editTitle.getText().toString(), editDesc.getText().toString(), getCurrentTime());
                    Intent intent = new Intent();
                    intent.putExtra((note.getTitle() == null ? "NEW_NOTE" : "UPDATED_NOTE"), newNote);
                    setResult(RESULT_OK, intent);

                    finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getCurrentTime() {
        DateFormat date = new SimpleDateFormat("E MMM d, hh:mm a");
        return date.format(new Date());
    }

    public void onBackPressed() {
        if (editTitle.getText().toString().trim().isEmpty() || (editTitle.getText().toString().equals(note.getTitle()) && editDesc.getText().toString().equals(note.getDescription()))) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your note is not saved!\nSave note \'" + editTitle.getText().toString() + "\'?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Notes newNote = new Notes(editTitle.getText().toString(), editDesc.getText().toString(), getCurrentTime());
                    Intent intent = new Intent();
                    intent.putExtra((note.getTitle() == null ? "NEW_NOTE" : "UPDATED_NOTE"), newNote);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}