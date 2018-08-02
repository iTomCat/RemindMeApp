package com.example.tomcat.remindmeapp.geofences;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class GeoJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case GeoSyncJob.TAG:
                return new GeoSyncJob();
            default:
                return null;
        }
    }
}
