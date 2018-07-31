package com.example.tomcat.remindmeapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.tomcat.remindmeapp.data.ActionsContract;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by tomas on 30.07.2018.
 */

public class TestingBroad {
    Activity activity;
    Context context;
    private Cursor currentCursor;
    //String currID;
    private int numberOfGeofencies = -1;
    ArrayList<String> geoList;

    private int geofenceTransition;
    private int currGeofencePos = 0;
    int currentCursorPos = 0;

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 116;

    String testGeoID = "ChIJY2m4QCfMFkcRppUtdIZptFo";
    String testGeoID1 = "ChIJqeqUZyfMFkcRyfn6mCOzkvo";  // Smak  Pizzy
    //String testGeoID1 = "ChIJY2m4QCfMFkcRppUtdIZptFo1";

    enum S {START, ACTIVE, IN_OUT, SETTINGS, REMIND_OK, END}
    private S state = S.START;


    public TestingBroad(Activity activity, Context context){
        this.activity = activity;
        this.context = context;

        // List<Geofence> aa = geofencingEvent.getTriggeringGeofences();
        geoList  = new ArrayList<>();
        geoList.add(testGeoID);
        geoList.add(testGeoID1);  // Potem sprawdzić dla dwóch

        /*for (String  geo : geoList) {
            Log.d("RecPlace", "Loop " + geo);
        }*/

        numberOfGeofencies = geoList.size();

        //currID = geoList.get(0);  //<<<<<<<<<<<<<< LOOPOWAc
       // geofencePlace(numberOfGeofencies);

        //TODO zrobić potem sprawdzanie dla każdego miejsca po koloei. Na end następne miejsce


        //int transitionType = Geofence.GEOFENCE_TRANSITION_ENTER;
        //CURRENT_STATE = transitionType;
        geofenceTransition = Geofence.GEOFENCE_TRANSITION_EXIT; // pobierać z int geofenceTransition = geofencingEvent.getGeofenceTransition()

        // ------------------------------------------------ Adding data from the place to the cursor
        //currentCursor = cursorWithPlaceIDdata(currID);

        //geofencePlace(currGeofencePos);
        reminderState();



        //if (currentCursor.getCount() > 0) reminderState(currentCursor);

       /* //Cursor currentCursor = cursorWithPlaceIDdata("ChIJY2m4QCfMFkcRppUtdIZptFo1");
        assert currentCursor != null;
        int ColumnInDB = currentCursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME);
        currentCursor.moveToFirst(); // MOVE TO FIRST
        Log.d("RecPlace", "currentCursor " + currentCursor.getCount());

        if (currentCursor.getCount() > 0) {

            String placeName = currentCursor.getString(ColumnInDB);
            Log.d("RecPlace", "placeName " + placeName);
        }

        currentCursor.close();*/

        //currentCursor.close();

    }

    private String geofencePlace(int currNum){
        Log.d("RecPlace", "currGeofencePos cxxxxxxxxxxxxxxxxxxxxxxxxx> " + currNum);
        if (numberOfGeofencies > 0 && currNum < numberOfGeofencies) {
            String currID = geoList.get(currNum);  //<<<<<<<<<<<<<< LOOPOWAc
            Log.d("RecPlace", "currID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + currID);
            return currID;
        }else{
            return null;
        }
    }


    // ********************************************************************************************* Check state
    private void reminderState(){
        switch (state) {
            case START: { //---------------------------------------- Is the place is in the reminder
                String currID = geofencePlace(currGeofencePos);
                Log.d("TwoPlaces", "INIT..................currentCursorPos  " + currentCursorPos);
                currentCursor = cursorWithPlaceIDdata(currID, currentCursorPos);

                Log.d("TwoPlaces", "INIT............................Pos  " + currentCursor.getPosition());

                //state = (currentCursor.getCount() > 0) ? S.ACTIVE : S.END;
                state = (currentCursor.getPosition() >= 0 && currentCursor.getPosition()< currentCursor.getCount()) ? S.ACTIVE : S.END;
                nextStep();
                break;
            }
            case ACTIVE: { //---------------------------------------------------------------- ACTIVE
                int columnActive = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMNM_ACTIVE);
                //currentCursor.moveToFirst();
                int active = currentCursor.getInt(columnActive);

                Log.d("RecPlace", "active " + active);

                state = (active == AddReminderActivity.REMIND_IS_ACTIVE) ? S.IN_OUT : S.END;
                nextStep();

                break;
            }

            case IN_OUT: { //---------------------------------------------------------------- IN OUT
                int columnINOut = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT);
                int inOrOut = currentCursor.getInt(columnINOut);

                state = (geofenceTransition == inOrOut) ? S.SETTINGS : S.END;
                nextStep();

                break;
            }

            case SETTINGS: { //------------------------------------------------------------ SETTINGS
                int columnINOut = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS);
                int settings = currentCursor.getInt(columnINOut);

                if((settings & AddReminderActivity.REMIND_ONCE)  > 0){
                    Log.d("RecPlace", "once ");
                    writeAsInactive();
                    state = S.REMIND_OK ;

                }else if ((settings & AddReminderActivity.REMIND_ALWAYS) > 0){
                    Log.d("RecPlace", "Always" );
                    state = S.REMIND_OK ;

                }else if ((settings & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0){
                    Log.d("RecPlace", "WEEK DAYS " + checkDayWeek(settings));
                    state = (checkDayWeek(settings)) ? S.REMIND_OK : S.END;
                }

                nextStep();
                break;
            }

            case REMIND_OK: { //----------------------------------------------------------------- OK

                // --------------------------------------------------------------  SEND NOTIFICATION
                int columnName = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_NAME);
                String title = currentCursor.getString(columnName);
                String place = AppContentProvider.getPlaceNameBasedGoogleID(activity,
                        geofencePlace(currGeofencePos));
                String notifiEnd = null;
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    notifiEnd = activity.getString(R.string.notification_in);
                    } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    notifiEnd = activity.getString(R.string.notification_out);
                }
                String descr = activity.getString(R.string.notification) + " " + notifiEnd + " " + place;
                sendNotification(context, title, descr);

                // ------------------------------------------------------------------------ SEND SMS
                int columnAction = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION);
                int action = currentCursor.getInt(columnAction);

                if(action == 1) {
                    int columnSMSid = currentCursor.getColumnIndex
                            (RemindersContract.RemindersEntry.COLUMN_REMIND_SMS_ID);
                    int smsID = currentCursor.getInt(columnSMSid);
                    sendSMS(smsID);
                }
                    //TODO Send Sms

                //Log.d("RecPlace", "REMIND OK SEND NOTIFICATION! " + title);
                Log.d("TwoPlaces", "REMIND OK SEND NOTIFICATION! " + title + " Pos: " + currentCursorPos);

                checkForAnotherReminderInThisPlace();
                //nextGeofence();
                break;
            }

            case END: { //--------------------- Conditions have not been met, reminder is not active
                // TODO tu sprawdzać czy są kolejne miejsca w geoList
                Log.d("TwoPlaces", "END ");
                nextGeofence();
                break;
            }

            default: {
            }

            currentCursor.close();
        }
    }


    private void checkForAnotherReminderInThisPlace(){
            currentCursorPos++;
            state = S.START;
            Log.d("TwoPlaces", "SEND STATE " + currentCursorPos);
            reminderState();
    }


    private void nextGeofence(){
        currGeofencePos++;
        Log.d("RecPlace", "currGeofencePos............. " + currGeofencePos);
        if( currGeofencePos <= numberOfGeofencies){ //
            currentCursorPos = 0;
            state = S.START;
            reminderState();
            Log.d("RecPlace", "START................................... " + currGeofencePos);
        }
    }

    private void nextStep(){
        reminderState();
    }


    private boolean checkDayWeek(int selectedDay){
        boolean dayIsMarked;

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);  //SUN = 1, MON = 2... SAT = 7

        // --------------------------- SUN  MON  TUE  WED  THU  FRI  SAT
        final int []WEEK_DAYS_VALUE = {512,  8,   16,  32,  64, 128, 256};
        int currDay = WEEK_DAYS_VALUE[today - 1];

        dayIsMarked = (currDay & selectedDay) > 0;
        return dayIsMarked;
    }


    private Cursor cursorWithPlaceIDdata(String currPlaceID, int currPos){
        Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(currPlaceID).build();


        Cursor cursor = activity.getContentResolver().query(uri,
                null,
                RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID,
                null,
                null);

        cursor.moveToPosition(currPos);

        Log.d("TwoPlaces", "Cursor count " + cursor.getCount());
        return cursor;
    }


    /*private Cursor cursorWithPlaceIDdata(String currPlaceID){
        Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(currPlaceID).build();


        Cursor cursor = activity.getContentResolver().query(uri,
                null,
                RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID,
                null,
                null);

        Log.d("TwoPlaces", "Cursor " + cursor.getCount());
        return cursor;
    }
*/
    private void writeAsInactive(){ // if Remind me only Once
        ContentValues contentValues = new ContentValues();
        contentValues.put(RemindersContract.RemindersEntry.COLUMNM_ACTIVE,
                AddReminderActivity.REMIND_IS_INACTIVE);

        int columnID = currentCursor.getColumnIndex
                (RemindersContract.RemindersEntry._ID);
        int reminderID = currentCursor.getInt(columnID);

        String stringId = Integer.toString(reminderID);
        Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        context.getContentResolver().update(uri, contentValues, null, null);
    }

    // ********************************************************************************************* Notification
    private void sendNotification(Context context, String title, String place) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_remind)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_remind_circle))
                .setContentTitle(title);

        // Continue building the notification
        builder.setContentText(place);
        builder.setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int mNotificationId = random.nextInt(9999 - 1000) + 1000;

        // Issue the notification
        mNotificationManager.notify(mNotificationId, builder.build());
    }

    // ********************************************************************************************* Send SMS
    private void sendSMS(int smsID){

        String id = String.valueOf(smsID);

        Uri uri = ActionsContract.ActionsEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(id).build();

        Cursor cursor = activity.getContentResolver().query(uri,
                null,
                ActionsContract.ActionsEntry._ID,
                null,
                null);

        assert cursor != null;
        int columnNumber = cursor.getColumnIndex(ActionsContract.ActionsEntry.COLUMN_SMS_NUMBER);
        int columnmessage = cursor.getColumnIndex(ActionsContract.ActionsEntry.COLUMN_SMS_MESSAGE);
        cursor.moveToFirst(); // MOVE TO FIRST
        String phoneNumber = cursor.getString(columnNumber);
        String message = cursor.getString(columnmessage);
        cursor.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }

        Log.d("SmsTest", "SMS " + phoneNumber );
    }
}
