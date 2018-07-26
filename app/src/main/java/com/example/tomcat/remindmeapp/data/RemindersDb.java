package com.example.tomcat.remindmeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tomcat.remindmeapp.data.RemindersContract.*;

/**
 * SQLite database with Reminders
 */

public class RemindersDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "remindersDB.db";
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
                RemindersEntry.COLUMN_PLACES_DB_ID + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RemindersEntry.COLUMNM_ACTIVE + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_REMIND_SETTINGS + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_REMIND_ACTION + " INTEGER NOT NULL, " +
                RemindersEntry.COLUMN_REMIND_SMS_ID + " INTEGER, " +
                RemindersEntry.COLUMN_NOTES + " TEXT " +
                "); ";

        db.execSQL(SQL_CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RemindersContract.RemindersEntry.TABLE_NAME);
        onCreate(db);
    }
}
