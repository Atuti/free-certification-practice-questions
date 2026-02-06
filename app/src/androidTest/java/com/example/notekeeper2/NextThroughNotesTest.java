package com.example.notekeeper2;

//import static org.junit.jupiter.api.Assertions.*;
import org.junit.Rule;
import org.junit.Test;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import java.util.List;

import org.junit.Assert;


public class NextThroughNotesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new
            ActivityTestRule<>(MainActivity.class);

    @Test
    public void NextThroughNotes () {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes2));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));


        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        int index = 0;
        NoteInfo note = notes.get(index);

        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(note.getCourse().getTitle())));
        onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
        onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

    }
}