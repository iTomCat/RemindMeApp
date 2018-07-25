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
import android.util.Log;

import com.example.tomcat.remindmeapp.models.Places;
import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Content Provider for reminders and places
 */

public class AppContentProvider extends ContentProvider{
    static final String AUTHORITY = "com.example.tomcat.remindmeapp";
    private RemindersDb remindersDb;
    private PlacesDb placesDb;

    public static final int REMINDERS = 100;
    public static final int REMINDER_WITH_ID = 101;

    public static final int PLACES = 200;
    public static final int PLACES_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        remindersDb = new RemindersDb(context);
        placesDb = new PlacesDb(context);
        //TODO Tu dodać drugą bazę
        return true;
    }

    // --------------------------------------------------------------------------------------------- URI Matcher
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // ------------------------------------------------------------------------------- REMINDERS
        // Directory
        uriMatcher.addURI(AUTHORITY, RemindersContract.PATH_REMINDERS,
                REMINDERS);
        // Single Item
        uriMatcher.addURI(AUTHORITY, RemindersContract.PATH_REMINDERS + "/#",
                REMINDER_WITH_ID);

        // ---------------------------------------------------------------------------------- PLACES
        // Directory
        uriMatcher.addURI(AUTHORITY, PlacesContract.PATH_PLACES,
                PLACES);
        // Single Item
        uriMatcher.addURI(AUTHORITY, PlacesContract.PATH_PLACES + "/#",
                PLACES_WITH_ID);

        return uriMatcher;
    }

    // --------------------------------------------------------------------------------------------- Query
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case REMINDERS:
                final SQLiteDatabase dbRem = remindersDb.getReadableDatabase();
                retCursor = dbRem.query(RemindersContract.RemindersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PLACES:
                final SQLiteDatabase dbPlaces = placesDb.getReadableDatabase();
                retCursor = dbPlaces.query(PlacesContract.PlacesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        assert getContext() != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    // --------------------------------------------------------------------------------------------- Insert
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // URI matching code to identify the match for the reminders directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case REMINDERS:
                // ------------------------------------------- Inserting values into reminders table
                final SQLiteDatabase dbRem = remindersDb.getWritableDatabase();
                long id_reminders = dbRem.insert(
                        RemindersContract.RemindersEntry.TABLE_NAME, null, contentValues);
                if ( id_reminders > 0 ) {
                    returnUri = ContentUris.withAppendedId(
                            RemindersContract.RemindersEntry.CONTENT_URI, id_reminders);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case PLACES:
                // ---------------------------------------------- Inserting values into places table
                final SQLiteDatabase dbPla = placesDb.getWritableDatabase();
                long id_places = dbPla.insert(
                        PlacesContract.PlacesEntry.TABLE_NAME, null, contentValues);
                if ( id_places > 0 ) {
                    returnUri = ContentUris.withAppendedId(
                            PlacesContract.PlacesEntry.CONTENT_URI, id_places);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
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
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


    // ********************************************************************************************* Making Lists from Cursor
    public static List<Reminder> remindersListFromCursor(Cursor cursor){
        List<Reminder> mRemindersList = new ArrayList<>();

        Log.d("DataBD", "inOut " + cursor.getCount() + "  pos: " + cursor.getPosition()
                + "  col: " + cursor.getColumnName(1));

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Reminder reminder = new Reminder();

            int inOut = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT);
            reminder.setInOut(cursor.getInt(inOut));

            int placeID = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_PLACES_DB_ID);
            reminder.setPlaceID(cursor.getInt(placeID));

            int name = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_NAME);
            reminder.setName(cursor.getString(name));

            int active = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMNM_ACTIVE);
            reminder.setActive(cursor.getInt(active));

            int settings = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS);
            reminder.setSettings(cursor.getInt(settings));

            int action = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION);
            reminder.setAction(cursor.getInt(action));

            int notes = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_NOTES);
            reminder.setNotes(cursor.getString(notes));

            mRemindersList.add(reminder);
        }
        return mRemindersList;
    }

    public static List<Places> placesListFromCursor(Cursor cursor){
        List<Places> mPlacesList = new ArrayList<>();

        Log.d("DataBD", "daaa " + cursor.getCount() + "  pos: " + cursor.getPosition()
                + "  col: " + cursor.getColumnName(1));

        cursor.moveToFirst();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Places place = new Places();

            int placeIDinDB = cursor.getColumnIndex(PlacesContract.PlacesEntry._ID);
            place.setPlaceIDinDB(cursor.getInt(placeIDinDB));

            int placeGoogleId = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID);
            place.setPlaceGoogleID(cursor.getString(placeGoogleId));

            int placeName = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME);
            place.setPlaceName(cursor.getString(placeName));

            mPlacesList.add(place);
        }
        return mPlacesList;
    }

}
