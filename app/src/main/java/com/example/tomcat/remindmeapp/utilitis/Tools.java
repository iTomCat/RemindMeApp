package com.example.tomcat.remindmeapp.utilitis;

import android.content.Context;
import android.content.res.Configuration;
import com.example.tomcat.remindmeapp.R;

/**
 * useful tools methods :)
 */

public class Tools {

    // ********************************************************************************************* checking if it is Tablet Mode with Two Pan
    public static boolean twoPaneScreen(Context context){
        boolean mode;
        boolean landscapeMode =  context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        boolean tabletMode = context.getResources().getBoolean(R.bool.isTablet);
        mode = landscapeMode && tabletMode;
        return mode;
    }

    public static boolean landscapeMode(Context context){
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean tableteMode(Context context){
        return context. getResources().getBoolean(R.bool.isTablet);
    }
}
