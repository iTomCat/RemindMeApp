package com.example.tomcat.remindmeapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database with Places
 */

public class PlacesDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "placesDB.db";
    private static final int DATABASE_VERSION = 1;


    public PlacesDb(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlacesContract.PlacesEntry.TABLE_NAME + " (" +
                PlacesContract.PlacesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID + " TEXT NOT NULL, " +
                PlacesContract.PlacesEntry.COLUMN_PLACE_NAME + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_PLACES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlacesContract.PlacesEntry.TABLE_NAME);
        onCreate(db);
    }
}
