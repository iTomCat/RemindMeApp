package com.example.tomcat.remindmeapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.data.RemindersContract.RemindersEntry;

import butterknife.ButterKnife;

/**
 * Add Reminder Activity
 */

public class AddReminderActivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);

        /*String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
        return;
    }*/

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_DESCRIPTION, "OK");

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, contentValues);

        Log.d("BeseOK", "URI:" + RemindersContract.RemindersEntry.CONTENT_URI + "   contentValues: "
                + contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }

    }
}
