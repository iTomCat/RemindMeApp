package com.example.tomcat.remindmeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tomcat.remindmeapp.data.RemindersContract.*;

/**
 * SQLite database
 */

public class RemindersDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movieDB.db";
    private static final int DATABASE_VERSION = 1;

    // --------------------------------------------------------------------------------- Constructor
    public RemindersDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REMINDERS_TABLE = "CREATE TABLE " + RemindersEntry.TABLE_NAME + " (" +
                RemindersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindersEntry.COLUMN_IN_OR_OUT + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_PLACES_ID + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                RemindersEntry.COLUMNM_ACTIVE + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_REMIND_SETTINGS + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_REMIND_ACTION + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_NOTES + " TEXT " +
                "); ";

        db.execSQL(SQL_CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
