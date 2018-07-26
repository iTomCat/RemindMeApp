package com.example.tomcat.remindmeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database with Actions such as send SMS
 */

public class ActionsDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "actionsDB.db";
    private static final int DATABASE_VERSION = 1;

    // --------------------------------------------------------------------------------- Constructor
    public ActionsDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ACTIONS_TABLE = "CREATE TABLE " + ActionsContract.ActionsEntry.TABLE_NAME + " (" +
                ActionsContract.ActionsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ActionsContract.ActionsEntry.COLUMN_SMS_CONTACT + " TEXT NOT NULL, " +
                ActionsContract.ActionsEntry.COLUMN_SMS_NUMBER+ " TEXT NOT NULL, " +
                ActionsContract.ActionsEntry.COLUMN_SMS_MESSAGE + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_ACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ActionsContract.ActionsEntry.TABLE_NAME);
        onCreate(db);
    }
}
