package com.example.notekeeper2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import static com.example.notekeeper2.DataManager.*;

//import static com.example.notekeeper2.NoteListActivity.NOTE_POSITION;

public class NoteActivity extends AppCompatActivity {

    public static final int POSITION_NOT_SET = -1;
    public static final String NOTE_POSITION = "com.example.notekeeper2.NOTE_POSITION";
    private NoteInfo mNote;
    private int mNotePosition;
    private Spinner mSpinnerCourses;
    private List<CourseInfo> mCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;

    NoteActivityViewModel mViewModel;
    private boolean mIsCancelling;
    private Boolean mIsNewNote ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);



        mSpinnerCourses = findViewById(R.id.spinner_courses);
        mCourses = getInstance().getCourses();
        ArrayAdapter<CourseInfo> coursesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mCourses);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(coursesAdapter);

        readDisplayStateValues();
        if (mViewModel.mIsNewlyCreated){
            saveOriginalNoteValues();
        }

        if(mViewModel.mIsNewlyCreated && savedInstanceState != null) {
            mViewModel.restoreState(savedInstanceState);
        }

        mViewModel.mIsNewlyCreated = false;


        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if (!mIsNewNote)
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        }
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling) {
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else
                restoreOriginalValues();

        } else {
            saveNote();
        }

    }

    private void saveNote() {

        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            mViewModel.saveState(outState);
        }
    }

    private void restoreOriginalValues() {
        CourseInfo course = getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNote.getTitle();
        mViewModel.mOriginalNoteText = mNote.getText();

            }

    private void displayNote(Spinner mSpinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
        int courseIndex = mCourses.indexOf(mNote.getCourse());
        mSpinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());

    }

    private void createNewNote() {
        mNotePosition = getInstance().createNewNote();
//        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

//        NoteInfo note = DataManager.getInstance().getNotes().get(newNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendEmail();
            return true;
        } else if(id == R.id.action_next) {
            moveNext();
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        int lastNoteIndex = DataManager.getInstance().getNotes().size() -1;
//
//        menu.getItem(R.id.action_next).setEnabled(mNotePosition < lastNoteIndex);

        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = getInstance().getNotes().size()-1;
        item.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);

    }

    private void moveNext() {
        saveNote();

        ++mNotePosition;

        mNote = getInstance().getNotes().get(mNotePosition);
        saveOriginalNoteValues();
        displayNote(mSpinnerCourses,mTextNoteTitle,mTextNoteText);
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check out what I learned : " + mTextNoteText.getText();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}