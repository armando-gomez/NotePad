package com.armandogomez.notepad;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private static final int CODE_FOR_NEW = 200;
    private static final int CODE_FOR_EDIT = 300;
    private static final int RESULT_BAD = 404;

    private Menu menu;
    private List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        noteAdapter = new NoteAdapter(noteList, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadJSON();

        setTitle(getApplicationContext().getString(R.string.app_name) + " (" + noteAdapter.getItemCount() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                this.openAboutActivity();
                return true;
            case R.id.menu_add:
                this.openEditActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent,CODE_FOR_NEW);
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        if(!n.getTitle().isEmpty()) {
            intent.putExtra("CURR_TITLE", n.getTitle());
            intent.putExtra("CURR_BODY", n.getBody());
            intent.putExtra("POS", pos);
        }
        startActivityForResult(intent, CODE_FOR_EDIT);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Do you want to delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(pos);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODE_FOR_NEW) {
            if(resultCode == RESULT_OK) {
                String title = data.getStringExtra("NOTE_TITLE");
                String body = data.getStringExtra("NOTE_BODY");
                addNote(title, body, new Date());
            }
        } else if(requestCode == CODE_FOR_EDIT) {
            if(resultCode == RESULT_OK) {
                if(data.hasExtra("NOTE_POS")) {
                    String title = data.getStringExtra("NOTE_TITLE");
                    String body = data.getStringExtra("NOTE_BODY");
                    int pos = data.getIntExtra("NOTE_POS", -1);
                    editNote(title, body, new Date(), pos);
                }
            }
        }

        if (resultCode == RESULT_BAD) {
            Toast.makeText(this, "Saved Unsuccessful: No Title entered", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList() {
        noteAdapter.notifyDataSetChanged();
        setTitle(getApplicationContext().getString(R.string.app_name) + " (" + noteAdapter.getItemCount() + ")");
    }

    private void editNote(String title, String body, Date date, int pos) {
        Note n = noteList.get(pos);
        n.setTitle(title);
        n.setBody(body);
        n.setDate(date);
        updateList();
    }

    private void addNote(String title, String body, Date date) {
        Note n = new Note(title, body, date);
        noteList.add(n);
        updateList();
    }

    private void deleteNote(int pos) {
        noteList.remove(pos);
        updateList();
    }

    private void loadJSON() {
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            for(int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String title = jsonObject.getString("titleText");
                String body = jsonObject.getString("bodyText");
                String date_string = jsonObject.getString("dateText");
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a");
                Date date = sdf.parse(date_string);
                Note n = new Note(title, body, date);
                noteList.add(n);
            }
        } catch (FileNotFoundException e) {
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            saveNotes();
        } catch(IOException | JSONException e) {
            Toast.makeText(this, "Not saved", Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    private void saveNotes() throws IOException, JSONException {
        FileOutputStream fos = getApplicationContext()
                .openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

        JSONArray jsonArray = new JSONArray();

        for(Note n: noteList) {
            JSONObject noteJSON = new JSONObject();
            noteJSON.put("titleText", n.getTitle());
            noteJSON.put("bodyText", n.getBody());
            noteJSON.put("dateText", n.getDate());
            jsonArray.put(noteJSON);
        }

        String jsonText = jsonArray.toString();
        fos.write(jsonText.getBytes());
        fos.close();
    }
}
