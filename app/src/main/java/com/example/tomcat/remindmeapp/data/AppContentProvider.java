package com.example.tomcat.remindmeapp.data;

import android.app.Activity;
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

import com.example.tomcat.remindmeapp.models.Actions;
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
    private ActionsDb actionsDb;

    public static final int REMINDERS = 100;
    public static final int REMINDER_WITH_ID = 101;

    public static final int PLACES = 200;
    public static final int PLACES_WITH_ID = 201;

    public static final int ACTIONS = 300;
    public static final int ACTIONS_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        remindersDb = new RemindersDb(context);
        placesDb = new PlacesDb(context);
        actionsDb = new ActionsDb(context);
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
        uriMatcher.addURI(AUTHORITY, RemindersContract.PATH_REMINDERS + "/*",
                REMINDER_WITH_ID);

        // ---------------------------------------------------------------------------------- PLACES
        // Directory
        uriMatcher.addURI(AUTHORITY, PlacesContract.PATH_PLACES,
                PLACES);
        // Single Item
        uriMatcher.addURI(AUTHORITY, PlacesContract.PATH_PLACES + "/*",
                PLACES_WITH_ID);

        // --------------------------------------------------------------------------------- Actions
        // Directory
        uriMatcher.addURI(AUTHORITY, ActionsContract.PATH_ACTIONS,
                ACTIONS);
        // Single Item
        uriMatcher.addURI(AUTHORITY, ActionsContract.PATH_ACTIONS + "/*",
                ACTIONS_WITH_ID);

        return uriMatcher;
    }

    // --------------------------------------------------------------------------------------------- Query
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = sUriMatcher.match(uri);
        final SQLiteDatabase dbRem = remindersDb.getReadableDatabase();
        final SQLiteDatabase dbPlaces = placesDb.getReadableDatabase();
        final SQLiteDatabase dbActions = actionsDb.getReadableDatabase();

        Cursor retCursor;
        switch (match) {
            case REMINDERS:
                //final SQLiteDatabase dbRem = remindersDb.getReadableDatabase();
                retCursor = dbRem.query(RemindersContract.RemindersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REMINDER_WITH_ID:
                //final SQLiteDatabase dbRem2 = remindersDb.getReadableDatabase();
                String id = uri.getPathSegments().get(1);
                String mSelection = selection + "=?";

                String[] mSelectionArgs = new String[] {id};
                retCursor = dbRem.query(RemindersContract.RemindersEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PLACES:
                //final SQLiteDatabase dbPlaces = placesDb.getReadableDatabase();
                retCursor = dbPlaces.query(PlacesContract.PlacesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PLACES_WITH_ID:
                //final SQLiteDatabase dbPlaces2 = placesDb.getReadableDatabase();
                String idPalce = uri.getPathSegments().get(1);
                String mSelectionPlace = selection + "=?";
                String[] mSelectionArgsPlace = new String[] {idPalce};
                retCursor = dbPlaces.query(PlacesContract.PlacesEntry.TABLE_NAME,
                        projection,
                        mSelectionPlace,
                        mSelectionArgsPlace,
                        null,
                        null,
                        sortOrder);
                break;

            case ACTIONS:
              //  final SQLiteDatabase dbActions = actionsDb.getReadableDatabase();
                retCursor = dbActions.query(ActionsContract.ActionsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case ACTIONS_WITH_ID:
                //final SQLiteDatabase dbActions2 = actionsDb.getReadableDatabase();
                String idAction = uri.getPathSegments().get(1);
                String mActionSelection = selection + "=?";
                String[] mActionSelectionArgs = new String[] {idAction};
                retCursor = dbActions.query(ActionsContract.ActionsEntry.TABLE_NAME,
                        projection,
                        mActionSelection,
                        mActionSelectionArgs,
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

            case ACTIONS:
                // --------------------------------------------- Inserting values into actions table
                final SQLiteDatabase dbActions = actionsDb.getWritableDatabase();
                long id_actions = dbActions.insert(
                        ActionsContract.ActionsEntry.TABLE_NAME, null, contentValues);
                if ( id_actions > 0 ) {
                    /*returnUri = ContentUris.withAppendedId(
                            PlacesContract.PlacesEntry.CONTENT_URI, id_actions);*/
                    returnUri = ContentUris.withAppendedId(
                            ActionsContract.ActionsEntry.CONTENT_URI, id_actions);
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
        int match = sUriMatcher.match(uri);

        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case REMINDER_WITH_ID:
                final SQLiteDatabase dbRem = remindersDb.getWritableDatabase();
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = dbRem.delete(RemindersContract.RemindersEntry.TABLE_NAME,
                        "_id=?", new String[]{id});
                break;

            case PLACES_WITH_ID:
                final SQLiteDatabase dbPlaces = placesDb.getWritableDatabase();
                // Get the task ID from the URI path
                String idPlaces = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = dbPlaces.delete(PlacesContract.PlacesEntry.TABLE_NAME,
                        "_id=?", new String[]{idPlaces});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case REMINDER_WITH_ID:
                final SQLiteDatabase dbRem = remindersDb.getWritableDatabase();
                String id = uri.getPathSegments().get(1);

                tasksUpdated = dbRem.update(RemindersContract.RemindersEntry.TABLE_NAME,
                        contentValues,
                        "_id=?", new String[]{id});

                break;

            case PLACES_WITH_ID:
                final SQLiteDatabase dbPlaces = placesDb.getWritableDatabase();
                String googleID = uri.getPathSegments().get(1);

                Log.d("AddPlace", "Place googleID " + googleID);

                tasksUpdated = dbPlaces.update(PlacesContract.PlacesEntry.TABLE_NAME,
                        contentValues,
                        "place_id=?", new String[]{googleID});

                break;

            case ACTIONS_WITH_ID:
                final SQLiteDatabase dbAction = actionsDb.getWritableDatabase();
                String idAction = uri.getPathSegments().get(1);

                tasksUpdated = dbAction.update(ActionsContract.ActionsEntry.TABLE_NAME,
                        contentValues,
                        "_id=?", new String[]{idAction});

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }


    // ********************************************************************************************* Get Name Place from db based on GOOGLE ID
    public static String getPlaceNameBasedGoogleID (Context context, String currPlaceID){

        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(currPlaceID).build();

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID,
                null,
                null);

        assert cursor != null;
        int columnInDB = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME);
        cursor.moveToFirst(); // MOVE TO FIRST
        String placeName = cursor.getString(columnInDB);
        cursor.close();

        return placeName;
    }

    // ********************************************************************************************* Get GoogleID based on database _ID
    public static String getGoogleIDbyID (Activity activity, int currID){

        String id = String.valueOf(currID);

        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(id).build();

        Cursor cursor = activity.getContentResolver().query(uri,
                null,
                PlacesContract.PlacesEntry._ID,
                null,
                null);

        assert cursor != null;
        int columnInDB = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID);
        cursor.moveToFirst(); // MOVE TO FIRST
        String googleID = cursor.getString(columnInDB);
        cursor.close();

        return googleID;
    }

    // ********************************************************************************************* Check if Google's place id is in DB
    public static boolean checkGoogleIdInDB(Activity activity, String currPlaceID){
        boolean dataExist;
        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(currPlaceID).build();

        Cursor cursor = activity.getContentResolver().query(uri,
                null,
                PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID,
                null,
                null);

        assert cursor != null;
        dataExist = cursor.getCount() > 0;

        cursor.close();

        return dataExist;
    }


    // ********************************************************************************************* Making Lists from Cursor
    public static List<Reminder> remindersListFromCursor(Cursor cursor){
        List<Reminder> mRemindersList = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Reminder reminder = new Reminder();

            int IDinDB = cursor.getColumnIndex(RemindersContract.RemindersEntry._ID);
            reminder.setRemIDinDB(cursor.getInt(IDinDB));

            int inOut = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT);
            reminder.setInOut(cursor.getInt(inOut));

            int placeID = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID);
            reminder.setPlaceID(cursor.getString(placeID));

            int name = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_NAME);
            reminder.setName(cursor.getString(name));

            int active = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMNM_ACTIVE);
            reminder.setActive(cursor.getInt(active));

            int settings = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS);
            reminder.setSettings(cursor.getInt(settings));

            int action = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION);
            reminder.setAction(cursor.getInt(action));

            int smsID = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMIND_SMS_ID);
            reminder.setSmsID(cursor.getInt(smsID));

            int notes = cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_NOTES);
            reminder.setNotes(cursor.getString(notes));

            mRemindersList.add(reminder);
        }
        return mRemindersList;
    }

    public static List<Places> placesListFromCursor(Cursor cursor){
        List<Places> mPlacesList = new ArrayList<>();

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

    public static List<Actions> actionsListFromCursor(Cursor cursor){
        List<Actions> mActionList = new ArrayList<>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Actions actions = new Actions();

            int actionIDinDB = cursor.getColumnIndex(ActionsContract.ActionsEntry._ID);
            actions.setActionIDinDB(cursor.getInt(actionIDinDB));

            int smsContact = cursor.getColumnIndex(ActionsContract.ActionsEntry.COLUMN_SMS_CONTACT);
            actions.setSmsContact(cursor.getString(smsContact));

            int smsNumber = cursor.getColumnIndex(ActionsContract.ActionsEntry.COLUMN_SMS_NUMBER);
            actions.setSmsNumber(cursor.getString(smsNumber));

            int smsMessage = cursor.getColumnIndex(ActionsContract.ActionsEntry.COLUMN_SMS_MESSAGE);
            actions.setSmsMessage(cursor.getString(smsMessage));

            mActionList.add(actions);
        }
        return mActionList;
    }

}
