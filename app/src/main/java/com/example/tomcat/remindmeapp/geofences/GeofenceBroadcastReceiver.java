package com.example.tomcat.remindmeapp.geofences;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.MainActivity;
import com.example.tomcat.remindmeapp.R;
import com.example.tomcat.remindmeapp.data.ActionsContract;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * BroadcastReceiver
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    private Context context;
    private Cursor currentCursor;
    //String currID;
    private int numberOfGeofencies = -1;
    List<Geofence> geoList;
    MediaPlayer mediaPlayer;

    private int geofenceTransition;
    private int currGeofencePos = 0;
    private int currentCursorPos = 0;

    private static final int PERMISSIONS_REQUEST_SEND_SMS = 116;

    enum S {START, ACTIVE, IN_OUT, SETTINGS, REMIND_OK, END}
    private S state = S.START;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        mediaPlayer = MediaPlayer.create(context, R.raw.gong);

        Log.i(TAG, "onReceive called");
        // Get the Geofence Event from the Intent sent through
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        geoList = geofencingEvent.getTriggeringGeofences();
        geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> geoList = geofencingEvent.getTriggeringGeofences();
        numberOfGeofencies = geoList.size();
        reminderState();
    }

    private String geofencePlace(int currNum){
        if (numberOfGeofencies > 0 && currNum < numberOfGeofencies) {
            return geoList.get(currNum).getRequestId();
        }else{
            return null;
        }
    }


    // ********************************************************************************************* Check state
    private void reminderState(){
        switch (state) {
            case START: { //------------------------------------------------------------------------ Is the place is in the reminder
                String currID = geofencePlace(currGeofencePos);
                currentCursor = cursorWithPlaceIDdata(currID, currentCursorPos);

                state = (currentCursor.getPosition() >= 0 && currentCursor.getPosition()< currentCursor.getCount())
                        ? S.ACTIVE : S.END;
                nextStep();
                break;
            }
            case ACTIVE: { //----------------------------------------------------------------------- ACTIVE
                int columnActive = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMNM_ACTIVE);
                int active = currentCursor.getInt(columnActive);

                state = (active == AddReminderActivity.REMIND_IS_ACTIVE) ? S.IN_OUT : S.END;
                nextStep();
                break;
            }

            case IN_OUT: { //----------------------------------------------------------------------- IN OR OUT
                int columnINOut = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT);
                int inOrOut = currentCursor.getInt(columnINOut);

                state = (geofenceTransition == inOrOut) ? S.SETTINGS : S.END;
                nextStep();

                break;
            }

            case SETTINGS: { //--------------------------------------------------------------------- SETTINGS
                int columnINOut = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS);
                int settings = currentCursor.getInt(columnINOut);

                if((settings & AddReminderActivity.REMIND_ONCE)  > 0){
                    writeAsInactive();
                    state = S.REMIND_OK ;

                }else if ((settings & AddReminderActivity.REMIND_ALWAYS) > 0){
                    state = S.REMIND_OK ;

                }else if ((settings & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0){
                    state = (checkDayWeek(settings)) ? S.REMIND_OK : S.END;
                }

                nextStep();
                break;
            }

            case REMIND_OK: { //-------------------------------------------------------------------- OK (all reminder alerts met)

                // --------------------------------------------------------------  SEND NOTIFICATION
                int columnName = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_NAME);
                String title = currentCursor.getString(columnName);
                String place = AppContentProvider.getPlaceNameBasedGoogleID(context,
                        geofencePlace(currGeofencePos));
                String notifiEnd = null;
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    notifiEnd = context.getString(R.string.notification_in);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    notifiEnd = context.getString(R.string.notification_out);
                }
                String descr = context.getString(R.string.notification) + " " + notifiEnd + " " + place;
                sendNotification(context, title, descr);

                // ------------------------------------------------------------------------ SEND SMS
                int columnAction = currentCursor.getColumnIndex
                        (RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION);
                int action = currentCursor.getInt(columnAction);

                if(action == 1) {
                    int columnSMSid = currentCursor.getColumnIndex
                            (RemindersContract.RemindersEntry.COLUMN_REMIND_SMS_ID);
                    int smsID = currentCursor.getInt(columnSMSid);
                    sendSMS(context, smsID);
                }

                // ---------------------------------------------------------------------- Play Sound
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
                    }
                });
                checkForAnotherReminderForThisPlace();
                break;
            }

            case END: { //-------------------------------------------------------------------------- Conditions have not been met, reminder is not active
                if (currentCursorPos < numberOfGeofencies){
                    checkForAnotherReminderForThisPlace();
                }else{
                    checkNextGeofence();
                }

                break;
            }
            default: {
            }

            currentCursor.close();
        }
    }


    private void checkForAnotherReminderForThisPlace(){
        currentCursorPos++;
        state = S.START;
        reminderState();
    }


    private void checkNextGeofence(){
        currGeofencePos++;
        if( currGeofencePos <= numberOfGeofencies){ //
            currentCursorPos = 0;
            state = S.START;
            reminderState();
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


    // --------------------------------------------------------- Cursor with data based on Google Id
    private Cursor cursorWithPlaceIDdata(String currPlaceID, int currPos){
        Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(currPlaceID).build();

        Cursor cursor = context.getContentResolver().query(uri,
                null,
                RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID,
                null,
                null);

        assert cursor != null;
        cursor.moveToPosition(currPos);

        return cursor;
    }



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
    private void sendNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_remind : R.drawable.ic_remind_circle;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Random random = new Random();
        int mNotificationId = random.nextInt(9999 - 1000) + 1000;


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(message);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            //channel.setSound(defaultSoundUri, null);
            channel.setShowBadge(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(mNotificationId, notification);
        } else {
            @SuppressWarnings("deprecation")
            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    //.setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setLights(Color.BLUE, 3000, 3000);


            assert notificationManager != null;
            notificationManager.notify(mNotificationId, notificationBuilder.build());
        }
    }

    // ********************************************************************************************* Send SMS
    public static void sendSMS(Context context, int smsID){
        String id = String.valueOf(smsID);
        Uri uri = ActionsContract.ActionsEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(id).build();

        Cursor cursor = context.getContentResolver().query(uri,
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
                context.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }
}