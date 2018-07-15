package com.example.tomcat.remindmeapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.tomcat.remindmeapp.data.RemindersContract.RemindersEntry.TABLE_NAME;

/**
 * Content Provider for reminders and places
 */

public class AppContentProvider extends ContentProvider{
    private RemindersDb remindersDb;
    public static final int REMINDERS = 100;
    public static final int REMINDER_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        remindersDb = new RemindersDb(context);
        //TODO Tu dodać drugą bazę
        return true;
    }

    // --------------------------------------------------------------------------------------------- URI Matcher
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Directory
        uriMatcher.addURI(RemindersContract.AUTHORITY, RemindersContract.PATH_REMINDERS,
                REMINDERS);

        // Single Item
        uriMatcher.addURI(RemindersContract.AUTHORITY, RemindersContract.PATH_REMINDERS + "/#",
                REMINDER_WITH_ID);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //TODO Przenieść chyba do maych w URI MATCHER
        final SQLiteDatabase db = remindersDb.getWritableDatabase();

        // URI matching code to identify the match for the reminders directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case REMINDERS:
                // Inserting values into reminders table
                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(RemindersContract.RemindersEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // COMPLETED (4) Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        assert getContext() != null;
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
