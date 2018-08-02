package com.example.tomcat.remindmeapp.geofences;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.places.PlacesFragment;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Refreshing the list of geofences
 */

public class GeoSyncJob extends Job {

    static final String TAG = "job_demo_tag";

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        String placeID;

        final Geofencing mGeofencing = new Geofencing(getContext(), PlacesFragment.mClient);
        mGeofencing.unRegisterAllGeofences();

        List<String> guids = new ArrayList<>();
        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uri,
                new String[]{PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID},
                null,
                null,
                null);

        assert cursor != null;

        int columnID = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID);
        //cursor.moveToFirst();
        while (cursor.moveToNext()) {
            placeID = cursor.getString(columnID);
            guids.add(placeID);
        }

        cursor.close();

        PendingResult<PlaceBuffer> placeResult = com.google.android.gms.location.places.Places.GeoDataApi.getPlaceById(PlacesFragment.mClient,
                guids.toArray(new String[guids.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                mGeofencing.updateGeofencesList(places);
                mGeofencing.registerAllGeofences();
            }
        });
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(GeoSyncJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(3), TimeUnit.DAYS.toMillis(1))
                //.setPeriodic(TimeUnit.SECONDS.toMillis(60), TimeUnit.SECONDS.toMillis(30)) // for tests
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}