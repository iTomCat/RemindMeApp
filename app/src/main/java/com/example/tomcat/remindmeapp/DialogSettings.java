package com.example.tomcat.remindmeapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Dialog Settings - Cyclic or one-time reminder.
 */

public class DialogSettings extends DialogFragment {

    private static final String TAG = "DataSettings";
    private LinearLayout reviewsLayout;
    private int DATA_SETTINGS;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ViewGroup nullParent = null;
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_settings, nullParent);
        reviewsLayout = view.findViewById(R.id.week_days_layout);

        final RadioButton rad1 = view.findViewById(R.id.rad_only_once);
        rad1.setOnClickListener(new radioButtonsListener());
        rad1.setId(AddReminderActivity.REMIND_ONCE);

        final RadioButton rad2 = view.findViewById(R.id.rad_always);
        rad2.setOnClickListener(new radioButtonsListener());
        rad2.setId(AddReminderActivity.REMIND_ALWAYS);

        final RadioButton rad3 = view.findViewById(R.id.rad_week_days);
        rad3.setOnClickListener(new radioButtonsListener());
        rad3.setId(AddReminderActivity.REMIND_ON_SELECTED_DAYS);

        resetBits();

        // Get data from Bundle
        DATA_SETTINGS = getArguments().getInt(AddReminderActivity.REMINDER_SETTINGS);

        setRadioButtons(view);
        addDaysOfTheWeek();

       // -------------------------------------------------------------------------- Positive Button
        builder.setView(view);
        //builder.setTitle("Title");
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Set data to Activity
                        ((AddReminderActivity) getActivity()).doPositiveClick(DATA_SETTINGS);
                        getDialog().dismiss();
                    }
                }
        );

        // ------------------------------------------------------------------------- Negative Button
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getDialog().dismiss();
                    }
                }
        );
        return builder.create();
    }

    // ********************************************************************************************* Set RadioButtons
    private void setRadioButtons(View view){
        for(int currData=1; currData<=AddReminderActivity.REMIND_ON_SELECTED_DAYS; currData++) {
            final RadioGroup rg = view.findViewById(R.id.radio_group);
            RadioButton aa = rg.findViewById(currData);
            if(((DATA_SETTINGS & currData) >0) && (aa != null)){
                rg.check(currData);
                showHideWeekDays(currData);
            }
        }
    }

    //********************************************************************************************** Radio Buttons Listener
    private class radioButtonsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int selOption = v.getId();
            resetBits();
            DATA_SETTINGS = DATA_SETTINGS + selOption;
            showHideWeekDays(selOption);

            Log.d(TAG, "Data: " + DATA_SETTINGS
                    + "  BIN: " + Integer.toBinaryString(DATA_SETTINGS));
        }
    }
    
    private void resetBits(){
        if ((DATA_SETTINGS & AddReminderActivity.REMIND_ONCE) > 0) {
            DATA_SETTINGS = DATA_SETTINGS - AddReminderActivity.REMIND_ONCE;
        }
        if ((DATA_SETTINGS & AddReminderActivity.REMIND_ALWAYS) > 0) {
            DATA_SETTINGS = DATA_SETTINGS - AddReminderActivity.REMIND_ALWAYS;
        }
        if ((DATA_SETTINGS & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0) {
            DATA_SETTINGS = DATA_SETTINGS - AddReminderActivity.REMIND_ON_SELECTED_DAYS;
        }
    }

    //********************************************************************************************** Days of the week Buttons Listener
    private class weekDaysListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.isSelected()){
                v.setSelected(false);
            }else{
                v.setSelected(true);
            }

            // ----------------------------------------------------------------------  Data Settings
            if(v.isSelected()){
                DATA_SETTINGS = DATA_SETTINGS +  v.getId();
            }else if ((DATA_SETTINGS & v.getId()) > 0){
                DATA_SETTINGS = DATA_SETTINGS -  v.getId();
            }

            Log.d(TAG, "Data: " + DATA_SETTINGS
                    + "  BIN: " + Integer.toBinaryString(DATA_SETTINGS));

            dayColorSettings(v);
        }
    }

    // --------------------------------------------------------------------------------------------- Adding Week Days to Layout
    private void addDaysOfTheWeek(){
        boolean isSelected;

        for(int i=0; i<=6; i++) {
            View weekView = View.inflate(getActivity(), R.layout.week_item, null);

            // Read states
            int currDay = AddReminderActivity.WEEK_DAYS[i];
            isSelected = (DATA_SETTINGS & currDay) > 0;

            weekView.setSelected(isSelected);
            TextView day = weekView.findViewById(R.id.day_name);

            String dayName = "day_" + Integer.toString(i);
            int dayNameRef = getResources().getIdentifier
                    (dayName, "string", getActivity().getPackageName());
            String originalText = getString(dayNameRef);
            day.setText(originalText);

            weekView.setOnClickListener(new weekDaysListener());
            weekView.setId(AddReminderActivity.WEEK_DAYS[i]);
            reviewsLayout.addView(weekView);

            dayColorSettings(weekView);
        }
    }

    // ---------------------------------------------------------------------------------------------  Displaying the selected days of the week and setting data
    private void dayColorSettings(View selectedView){
        ImageView curr_day_circle = selectedView.findViewById(R.id.day_circle_iv);

        if(selectedView.isSelected()){
            curr_day_circle.setColorFilter(getResources().getColor(R.color.colorPrimary));
        }else{
            curr_day_circle.setColorFilter(getResources().getColor(R.color.light_gray));
        }
    }

    private void showHideWeekDays(int selOption){
        final boolean showDaysButtons;
        if(selOption == AddReminderActivity.REMIND_ON_SELECTED_DAYS){
            showDaysButtons = true;
            reviewsLayout.setAlpha(1f);
        }else{
            showDaysButtons = false;
            reviewsLayout.setAlpha(0.2f);
        }

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disableDaysButtons(showDaysButtons);
            }
        }, 200);
    }

    private void disableDaysButtons(boolean showDaysButtons){
        for ( int i = 0; i < reviewsLayout.getChildCount();  i++ ){
            View currView = reviewsLayout.getChildAt(i);
            currView.setEnabled(showDaysButtons);
        }
    }
}
