package com.example.tomcat.remindmeapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;

import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.animation.Animation;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import butterknife.ButterKnife;

/**
 * Add Reminder Activity
 */

public class AddReminderActivity extends AppCompatActivity{
    //@BindView(R.id.in_btn) ImageView inButton;
    //@BindView(R.id.reminder_settings)ConstraintLayout out1Button;
    private Handler handlCountDown;

    public final static int REMIND_ALWAYS = 1;
    public final static int REMIND_ONCE = 2;
    public final static int REMIND_ON_SELECTED_DAYS = 4;
    public final static int MON = 8;
    public final static int TUE = 16;
    public final static int WED = 32;
    public final static int THU = 64;
    public final static int FRI = 128;
    public final static int SAT = 256;
    public final static int SUN = 512;
    public final static int []WEEK_DAYS = {MON, TUE, WED, THU, FRI, SAT, SUN};

    final static int WHEN_ARRIVE = 0;
    final static int WHEN_GET_OUT = 1;
    static int CURRENT_STATE = WHEN_ARRIVE;

    final static int REMINDER_SETTINGS = 2; //Cyclic or one-time reminder settings
    final static int REMINDER_ACTIONS = 1; // Action related to the reminder settings E.g. sending SMS


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);


        Toolbar toolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.add_new_reminder));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        setInOutButtonsState(CURRENT_STATE);

        // ----------------------------------------------- Displaying a random example of a reminder
        TextInputLayout inputTxtLay = findViewById(R.id.text_input_lay);
        TextInputEditText inputTxt = findViewById(R.id.text_input_txt);
        Random generator = new Random();
        int random = generator.nextInt(4);
        String reference = "eg_" + Integer.toString(random);
        int refToString = getResources().getIdentifier(reference, "string", getPackageName());
        inputTxt.setHint(getString(refToString));

        //inputTxt.setFocusable(false);

        /*if(tiet.getText().toString().isEmpty()){
            til.setError("Please enter valid address.");
        }else{
            til.setError(null);
        }*/


        /*String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
        return;
        }*/







        // --------------------------------------------------------------------------




        //Add Data OK in SQLite:
        /*// Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_DESCRIPTION, "OK");

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, contentValues);

        Log.d("BeseOK", "URI:" + RemindersContract.RemindersEntry.CONTENT_URI + "   contentValues: "
                + contentValues);

        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }*/

    }


    private void init(){
        handlCountDown = new Handler();
        handlCountDown.postDelayed(placesAnimTimer, 300);
        //ConstraintLayout aa = findViewById(R.id.in_out_btn);
        ImageView inButton = findViewById(R.id.in_btn);
        inButton.setClickable(true);
        inButton.setOnClickListener(new arriveOrOutListener());
        inButton.setTag(WHEN_ARRIVE);

        ImageView outButton = findViewById(R.id.out_btn);
        outButton.setClickable(true);
        outButton.setOnClickListener(new arriveOrOutListener());
        outButton.setTag(WHEN_GET_OUT);

        ConstraintLayout remindersButton = findViewById(R.id.reminder_settings);
        remindersButton.setClickable(true);
        remindersButton.setOnClickListener(new actionsAndSettingsListener());
        remindersButton.setTag(REMINDER_SETTINGS);

        ConstraintLayout actionsButton = findViewById(R.id.reminder_actions);
        actionsButton.setClickable(true);
        actionsButton.setOnClickListener(new actionsAndSettingsListener());
        actionsButton.setTag(REMINDER_ACTIONS);

        setSettingsAndActions();
    }

    public void addReminder(View view) {
        Log.d("AddRem", "Click: ");
    }


    //********************************************************************************************** Arrive / Leave Buttons Listener
    private class arriveOrOutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CURRENT_STATE = (int) v.getTag();
            setInOutButtonsState(CURRENT_STATE);
        }
    }

    //********************************************************************************************** Arrive / Leave Buttons Listener
    private class actionsAndSettingsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int buttonTag = (int) v.getTag();

            if (buttonTag == REMINDER_SETTINGS) {
                Log.d("AddRem", "Click IN");
                showDialogSettings();

            }else if (buttonTag == REMINDER_ACTIONS){
                Log.d("AddRem", "Click OUT");
            }
        }
    }


    // --------------------------------------------------------------------------------------------- Show Dialog Shake Info and Dim screen
    private void showDialogSettings() {
      DialogFragment newFragment = new DialogSettings();
        //Bundle args = new Bundle();
        //args.putInt("title", 1);
        //newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    // get from Dialogs
    public void doPositiveClick() {
        // Do stuff here.
        Log.d("AddRem", " Tadaaaaa222222");
    }


    private void setInOutButtonsState(int currState){
        final TextView inTv = findViewById(R.id.in_tv);
        final TextView outTv = findViewById(R.id.out_tv);
        final ImageView inOutImage = findViewById(R.id.in_out_image);

        if (currState == WHEN_ARRIVE){
            inTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            outTv.setTextColor(getResources().getColor(R.color.gray));
            inOutImage.setBackground(getResources().getDrawable(R.drawable.in_place));
        }else if (currState == WHEN_GET_OUT){
            inTv.setTextColor(getResources().getColor(R.color.gray));
            outTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            inOutImage.setBackground(getResources().getDrawable(R.drawable.out_place));
        }
    }

    private Runnable placesAnimTimer = new Runnable() {
        @Override
        public void run() {
            placesPointerAnimator();
            handlCountDown.postDelayed(placesAnimTimer, 3200);
        }
    };


    // ********************************************************************************************* Settings And Actions
    private void setSettingsAndActions(){
        // -------------------------------------------------------------------------------- Settings
        TextView settingsTxt = findViewById(R.id.settings_tv);
        //TODO Add conditions once, always.....
        String settings = getString(R.string.remind_always);
        settingsTxt.setText(settings);

        TextView weekDays = findViewById(R.id.week_days);
        weekDays.setVisibility(View.VISIBLE);
        showPickedWeekDays();

        // --------------------------------------------------------------------------------- Actions
        TextView actionsTxt = findViewById(R.id.action_tv);
        //TODO Add actions.....
        String actions = getString(R.string.action) + " " + getString(R.string.act_remind_only);
        actionsTxt.setText(actions);

        TextView actionsDescrTxt = findViewById(R.id.action_desc_tv);
        //TODO Add actions description.....
        actionsDescrTxt.setVisibility(View.GONE);
        //actionsDescrTxt.setText(actions);
    }

    // ********************************************************************************************* Picked Week Days
    private void showPickedWeekDays(){
        final TextView weekDaysTv = findViewById(R.id.week_days);
        SpannableString[] finalString1 = new SpannableString[7];

        for(int i=0; i<=6; i++) {
            String dayName = "day_" + Integer.toString(i);
            int dayNameRef = getResources().getIdentifier
                    (dayName, "string", getPackageName());
            String originalText = getString(dayNameRef);

            if (i >0 ) originalText = "  " + originalText;
            SpannableString highlighted = new SpannableString(originalText);

            if (i == 1 || i == 4) {  // ---------------------------------------- Selected Weeks days
                highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                                (this, R.color.colorPrimary)),
                        0, originalText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            finalString1[i]= highlighted;
        }
        weekDaysTv.setText(TextUtils.concat(finalString1));
    }


    // ********************************************************************************************* Pointer Animation
    private void placesPointerAnimator(){
        final ImageView pointerImage = findViewById(R.id.pointer);
        final ImageView ovalImage = findViewById(R.id.oval_point);
        final int duration = 350;
        final int rpeatCount = 5;

        pointerImage.clearAnimation();
        TranslateAnimation pointerAnim = new TranslateAnimation(
                0,
                0,
                0,
                (getResources().getDimension(R.dimen.anim_dist)) * -1);
        pointerAnim.setStartOffset(0);
        pointerAnim.setDuration(duration);
        pointerAnim.setRepeatCount(rpeatCount);
        pointerAnim.setRepeatMode(ObjectAnimator.REVERSE);
        pointerAnim.setFillAfter(true);
        pointerAnim.setInterpolator(new FastOutLinearInInterpolator());
        pointerImage.startAnimation(pointerAnim);

        ovalImage.clearAnimation();
        ScaleAnimation ovalAnim = new ScaleAnimation(1f, 0.5f,
                1, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        ovalAnim.setStartOffset(0);
        ovalAnim.setDuration(duration);
        ovalAnim.setRepeatCount(rpeatCount);
        ovalAnim.setRepeatMode(ObjectAnimator.REVERSE);
        ovalAnim.setFillAfter(true);
        ovalAnim.setInterpolator(new FastOutLinearInInterpolator());
        ovalImage.startAnimation(ovalAnim);
    }
}
