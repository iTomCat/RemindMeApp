package com.example.tomcat.remindmeapp.utilitis;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Button;

import com.example.tomcat.remindmeapp.R;

/**
 * useful tools methods :)
 */

public class Tools {

    // ********************************************************************************************* Convert Dp to Px
    private static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (Math.round(px));
    }

    // ********************************************************************************************* Checking if it is Tablet Mode with Two Pan
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


    // ********************************************************************************************* Set actions Add or Delete on a button
    public static void setButtonAddDelete(boolean add, Button button, Activity activity) {

        if (add) {
            button.setCompoundDrawablesWithIntrinsicBounds
                    (activity.getResources()
                            .getDrawable(R.drawable.ic_plus_blue), null, null, null);
        } else {
           button.setCompoundDrawablesWithIntrinsicBounds
                    (null, null, activity.getResources()
                            .getDrawable(R.drawable.ic_minus), null);

            int start = (int) activity.getResources().getDimension(R.dimen.start_padding);
            int end = (int) activity.getResources().getDimension(R.dimen.end_padding);

            button.setPadding(convertDpToPixel(start, activity), 0,
                    convertDpToPixel(end, activity), 0);
        }

        button.setSelected(add);
    }
}
