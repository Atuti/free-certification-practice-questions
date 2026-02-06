package com.example.notekeeper2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;

import java.util.List;

//import static com.example.noteKeeper2.NoteActivity.NOTE_POSITION;

public class NoteListActivity extends AppCompatActivity {


    private RecyclerView mNotesRecyclerView;
    private NoteRecyclerAdapter mNotesNoteRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeDisplayValues();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeDisplayValues() {
        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        mNotesRecyclerView = findViewById(R.id.recycler_note);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNotesRecyclerView.setLayoutManager(layoutManager);
        mNotesNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        mNotesRecyclerView.setAdapter(mNotesNoteRecyclerAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mNotesNoteRecyclerAdapter.notifyDataSetChanged();
    }

}