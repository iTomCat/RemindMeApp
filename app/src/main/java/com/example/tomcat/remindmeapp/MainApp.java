package com.example.tomcat.remindmeapp;

import android.app.Application;

import com.evernote.android.job.JobConfig;
import com.evernote.android.job.JobManager;
import com.example.tomcat.remindmeapp.geofences.GeoJobCreator;

public class MainApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new GeoJobCreator());
        //JobConfig.setAllowSmallerIntervalsForMarshmallow(true);  // For tests only
    }

}
