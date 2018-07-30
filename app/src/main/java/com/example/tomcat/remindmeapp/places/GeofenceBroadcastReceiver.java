package com.example.tomcat.remindmeapp.places;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.RemindersFragment;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * BroadcastReceiver
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    final static int WHEN_ARRIVE = 0;
    final static int WHEN_GET_OUT = 1;
    private int CURRENT_STATE = AddReminderActivity.WHEN_ENTER;


    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor reminderCursor = RemindersFragment.mRemindersData;

        /*for(int i=0; i<reminderCursor.getCount(); i++) {
            RemindersFragment.mRemindersData.moveToPosition(i);

            final int reminderPlaceIDcolumn = reminderCursor.getColumnIndex
                    (RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID);
            String id = reminderCursor.getString(reminderPlaceIDcolumn);

            String requiredGoogleID = AppContentProvider.getGoogleIDbyID(getActivity(), position);
        }



        if (id.equals(requiredGoogleID)) {
            int reminderNameColumn = reminderCursor.getColumnIndex
                    (RemindersContract.RemindersEntry.COLUMN_NAME);
            String reminderName = reminderCursor.getString(reminderNameColumn);*/


        Log.i(TAG, "onReceive called");
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }


        int transitionType = Geofence.GEOFENCE_TRANSITION_ENTER;
        CURRENT_STATE = transitionType;
        //int geofenceTransition = geofencingEvent.getGeofenceTransition(); PATRZ PONIZEJ


        List<Geofence> aa = geofencingEvent.getTriggeringGeofences();
        aa.get(0).getRequestId();
        Log.d("GeofTest", "Size " + aa.size());
        Log.d("GeofTest", "Name " + aa.get(0).getRequestId());

        //TODO Loop foe all Places
        String geoGoogleID = aa.get(0).getRequestId();

        //Location bb = geofencingEvent.getTriggeringLocation();
        //Log.d("GeofTest", "Name2 " +  aa.equals(aa.get(0)));









        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        /*// Check which transition type has triggered this event
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("GeofTest", "Enter.............................................. ");

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("GeofTest", "Exit xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx ");

        } else {
            // Log the error.
            Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
            return;
        }*/

        // Send the notification
        //sendNotification(context, geofenceTransition);

        CURRENT_STATE = geofenceTransition; // Enter or Exit POwinno być OK

    }

    private void actionOnGeofences (Context context, String geoGoogleID){
        if (CURRENT_STATE == AddReminderActivity.WHEN_ENTER) {
            Log.d("GeofTest", "Enter.............................................. ");

        } else if (CURRENT_STATE == AddReminderActivity.WHEN_EXIT) {
            Log.d("GeofTest", "Exit xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx ");

        } else {
            // Log the error.
            Log.e(TAG, "Unknown geofenceTransition");
            return;
        }


        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(geoGoogleID).build();

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID,
                null,
                null);

        assert cursor != null;
        int ColumnInDB = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME);
        cursor.moveToFirst(); // MOVE TO FIRST
        String placeName = cursor.getString(ColumnInDB);
        cursor.close();

    }

}