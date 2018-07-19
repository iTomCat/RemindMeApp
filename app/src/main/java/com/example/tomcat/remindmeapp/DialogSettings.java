package com.example.tomcat.remindmeapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    //private RadioButton radd;
   private LinearLayout reviewsLayout;

    private int SETTINGS_DATA;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //int title = getArguments().getInt("title"); // get from bundle

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


        //44

        resetBits();
        //SETTINGS_DATA = 44;
        SETTINGS_DATA = 514;

        //teraz po wejsciu odpowiedzni select radio button
        Log.d("AddRem", "START Data Decr: " + SETTINGS_DATA + "  BIN: "
                + Integer.toBinaryString(SETTINGS_DATA));showHideWeekDays(44);



        if ((SETTINGS_DATA & AddReminderActivity.REMIND_ONCE) > 0) {
            Log.d("AddRem", "nono " + SETTINGS_DATA);
        }

        if ((SETTINGS_DATA & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0) {
            Log.d("AddRem", "OKOKOKOKOK " + SETTINGS_DATA);
        }



        /*switch(44) {
                case (AddReminderActivity.REMIND_ONCE):
                    Log.d("AddRem", "nono " + SETTINGS_DATA);
                    break;
                case (AddReminderActivity.REMIND_ALWAYS):
                    Log.d("AddRem", "nono " + SETTINGS_DATA);
                    break;
                case (AddReminderActivity.REMIND_ON_SELECTED_DAYS):
                    Log.d("AddRem", "OKOKOKOKOK " + SETTINGS_DATA);
                    break;
            }*/


       /* for(int i=0; i<=6; i++) {

            int currDay = AddReminderActivity.WEEK_DAYS[i];
            if ((SETTINGS_DATA & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0) {
                Log.d("AddRem", "OKOKOKOKOK " + SETTINGS_DATA);
            }
        }*/


            //dayColorSettings(weekView);









        addDaysOfTheWeek();



        // ustawanie odpowiedzniego radiobuttona
        for(int i=1; i<=AddReminderActivity.REMIND_ON_SELECTED_DAYS; i++) {
            final RadioGroup rg = view.findViewById(R.id.radio_group);
            RadioButton aa = rg.findViewById(i);
            if(((SETTINGS_DATA & i) >0) && (aa != null)){
               // RadioButton aa = rg.findViewById(i);
                //int nn = aa.getResources();
               rg.check(i);
                Log.d("AddRem", "Radioooooooooo " + aa + "  i " + i);
            }


        }


       // --------------------------------------------------------------------Positive / Neg Buttons
        builder.setView(view);
        //builder.setTitle("Title");
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getDialog().dismiss();
                    }
                }
        );

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getDialog().dismiss();
                    }
                }
        );
        return builder.create();
    }

    //********************************************************************************************** Radio Buttons Listener
    private class radioButtonsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int selOption = v.getId();
            resetBits();
            SETTINGS_DATA = SETTINGS_DATA + selOption;
            showHideWeekDays(selOption);

            Log.d("AddRem", "Data Decr: " + SETTINGS_DATA + "  BIN: "
                    + Integer.toBinaryString(SETTINGS_DATA));

            /*switch(selOption) {
                case (AddReminderActivity.REMIND_ONCE):
                   //
                    break;
                case (AddReminderActivity.REMIND_ALWAYS):
                    //
                    break;
                case (AddReminderActivity.REMIND_ON_SELECTED_DAYS):
                    //
                    break;
            }*/
        }
    }
    
    private void resetBits(){
        if ((SETTINGS_DATA & AddReminderActivity.REMIND_ONCE) > 0) {
            SETTINGS_DATA = SETTINGS_DATA - AddReminderActivity.REMIND_ONCE;
        }
        if ((SETTINGS_DATA & AddReminderActivity.REMIND_ALWAYS) > 0) {
            SETTINGS_DATA = SETTINGS_DATA - AddReminderActivity.REMIND_ALWAYS;
        }
        if ((SETTINGS_DATA & AddReminderActivity.REMIND_ON_SELECTED_DAYS) > 0) {
            SETTINGS_DATA = SETTINGS_DATA - AddReminderActivity.REMIND_ON_SELECTED_DAYS;
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


            // ------------------------------------------------------------------------ Setting Data
            if(v.isSelected()){
                SETTINGS_DATA = SETTINGS_DATA +  v.getId();
                Log.d("AddRem", "Data Incr: " + SETTINGS_DATA + "  BIN: "
                        + Integer.toBinaryString(SETTINGS_DATA));
            }else{
                if ((SETTINGS_DATA & v.getId()) > 0) {
                    SETTINGS_DATA = SETTINGS_DATA -  v.getId();
                }

                Log.d("AddRem", "Data Decr: " + SETTINGS_DATA + "  BIN: "
                        + Integer.toBinaryString(SETTINGS_DATA));
            }

            dayColorSettings(v);
            //dayColorSettings(v);
        }
    }

    // --------------------------------------------------------------------------------------------- Adding Week Days to Layout
    private void addDaysOfTheWeek(){
        //reviewsLayout = view.findViewById(R.id.week_days_layout);
        boolean isSelected;

        for(int i=0; i<=6; i++) {
            View weekView = View.inflate(getActivity(), R.layout.week_item, null);

            // Read states
            int currDay = AddReminderActivity.WEEK_DAYS[i];
            isSelected = (SETTINGS_DATA & currDay) > 0;

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

    /*// ---------------------------------------------------------------------------------------------  Displaying the selected days of the week and setting data
    private void dayColorSettings(View selectedView){
        ImageView curr_day_circle = selectedView.findViewById(R.id.day_circle_iv);

        if(selectedView.isSelected()){
            curr_day_circle.setColorFilter(getResources().getColor(R.color.colorPrimary));
            selectedView.setSelected(true);

           SETTINGS_DATA = SETTINGS_DATA +  selectedView.getId();

            Log.d("AddRem", "Data Incr: " + SETTINGS_DATA + "  BIN: "
                    + Integer.toBinaryString(SETTINGS_DATA));

        }else{
            curr_day_circle.setColorFilter(getResources().getColor(R.color.light_gray));
            selectedView.setSelected(false);

            if ((SETTINGS_DATA & selectedView.getId()) > 0) {
                SETTINGS_DATA = SETTINGS_DATA -  selectedView.getId();
            }

            Log.d("AddRem", "Data Decr: " + SETTINGS_DATA + "  BIN: "
                    + Integer.toBinaryString(SETTINGS_DATA));
        }
    }*/

    private void showHideWeekDays(int selOption){
        boolean showDaysButtons;
        if(selOption == AddReminderActivity.REMIND_ON_SELECTED_DAYS){
            showDaysButtons = true;
            reviewsLayout.setAlpha(1f);
        }else{
            showDaysButtons = false;
            reviewsLayout.setAlpha(0.2f);
        }

        for ( int i = 0; i < reviewsLayout.getChildCount();  i++ ){
            View view2 = reviewsLayout.getChildAt(i);
            view2.setEnabled(showDaysButtons);
        }
    }

   /* @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings, container, true);


        //------------------------------------------------- Listener na key Back - zamyka Fragment z listą
        getDialog().setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getDialog().dismiss();
                    return true;
                }
                return false;
            }
        });
        return view;
    }*/


}
