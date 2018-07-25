package com.example.tomcat.remindmeapp;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.places.PlacesFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Add Reminder Activity
 */

public class AddReminderActivity extends AppCompatActivity {
    @BindView(R.id.add_button) Button addButton;
    @BindView(R.id.in_btn) ImageView inButton;
    @BindView(R.id.out_btn) ImageView outButton;
    @BindView(R.id.reminder_settings) ConstraintLayout remindersButton;
    @BindView(R.id.reminder_actions) ConstraintLayout actionsButton;
    @BindView(R.id.text_input_lay) TextInputLayout inputTxtLay;
    @BindView(R.id.text_input_txt) TextInputEditText inputTxt;
    @BindView(R.id.select_place_tv)
    com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight placeNameTxt;




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

    public final static int REMIND_IS_ACTIVE = 1;
    public final static int REMIND_IS_INACTIVE = 0;

    final static int WHEN_ARRIVE = 0;
    final static int WHEN_GET_OUT = 1;
    static int CURRENT_STATE = WHEN_ARRIVE;

    final static String REMINDER_SETTINGS = "reminder"; //Cyclic or one-time reminder settings
    final static String REMINDER_ACTIONS = "actions"; // Action related to the reminder settings E.g. sending SMS
    public final static String REMINDER_PLACES = "places"; // Select Place

    static int CURRENT_SETTINGS = -1;

    final static int ACTION_REMIND_ONLY = 0;
    final static int ACTION_SENS_SMS = 1;
    static int CURRENT_ACTION = 0;


    private String smsContact = null;
    private String smsNumber = null;
    private String smsMessage = null;

    private int placeID = -1;


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
        addButton.setVisibility(View.VISIBLE);


        if(CURRENT_SETTINGS == -1) CURRENT_SETTINGS = 250; // Set default settings > BIN: 11111100

        init();
        setInOutButtonsState(CURRENT_STATE);

        /*if(tiet.getText().toString().isEmpty()){
            til.setError("Please enter valid address.");
        }else{
            til.setError(null);
        }*/

        /*Uri uri = Uri.parse("smsto:732660660");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", "The SMS text");
        startActivity(it);*/


        /*String input = ((EditText) findViewById(R.id.editTextTaskDescription)).getText().toString();
        if (input.length() == 0) {
        return;
        }*/
    }
    private void init(){
        handlCountDown = new Handler();
        handlCountDown.postDelayed(placesAnimTimer, 300);

        inButton.setClickable(true);
        inButton.setOnClickListener(new arriveOrOutListener());
        inButton.setTag(WHEN_ARRIVE);

        outButton.setClickable(true);
        outButton.setOnClickListener(new arriveOrOutListener());
        outButton.setTag(WHEN_GET_OUT);

        remindersButton.setClickable(true);
        remindersButton.setOnClickListener(new actionsAndSettingsListener());
        remindersButton.setTag(REMINDER_SETTINGS);

        actionsButton.setClickable(true);
        actionsButton.setOnClickListener(new actionsAndSettingsListener());
        actionsButton.setTag(REMINDER_ACTIONS);
        setInfoOnActionButton();

        setSettingsAndActions();

        // ----------------------------------------------- Displaying a random example of a reminder
        inputTxtLay = findViewById(R.id.text_input_lay);
        inputTxt = findViewById(R.id.text_input_txt);
        Random generator = new Random();
        int random = generator.nextInt(4);
        String reference = "eg_" + Integer.toString(random);
        int refToString = getResources().getIdentifier(reference, "string", getPackageName());
        inputTxt.setHint(getString(refToString));

        //inputTxt.setFocusable(false);
    }


    private void writingData(){
        ContentValues contentValues = new ContentValues();

        // ------------------------------------------------------------------------------------ Name
        String remName = inputTxt.getText().toString();
        //nie moze byc puste != null
        if(remName.isEmpty()){
            inputTxtLay.setError("Please enter valid address.");
        }else{
            inputTxtLay.setError(null);
            contentValues.put(RemindersContract.RemindersEntry.COLUMN_NAME, remName);
        }


        // ------------------------------------------------------------------- Remind when In or Out
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT, CURRENT_STATE);

        // -------------------------------------------------------------------------- Place ID in DB
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_PLACES_DB_ID, placeID);
        // nie moze byc puste / musi byc >=0

        // ---------------------------------------------------------------------- Reminder is ACTIVE
        contentValues.put(RemindersContract.RemindersEntry.COLUMNM_ACTIVE, REMIND_IS_ACTIVE);

        // ----------------------------------------------------------------------- Reminder SETTINGS
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS, CURRENT_SETTINGS);

        // ------------------------------------------------------------------------- Reminder ACTION
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION, CURRENT_ACTION);


        getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, contentValues);

        //Uri uri = getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, contentValues);

        /*if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }*/


        ////// TODO do zapisywania miejsca

        // Add Place
        contentValues.clear();
        //ContentValues contentValues1 = new ContentValues();
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID, "colID777");
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME, "place nammee");
        Uri uri1 = getContentResolver().insert(PlacesContract.PlacesEntry.CONTENT_URI, contentValues);


        Log.d("TableErr", "333:" + PlacesContract.PlacesEntry.CONTENT_URI
                + "   uri: " + uri1);

    }

    // ********************************************************************************************* Select Place
    public void selectPlace(View view) {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.select_place));
        addButton.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        PlacesFragment placesFragmentFragment = new PlacesFragment();

        Bundle args = new Bundle();
        args.putBoolean(REMINDER_PLACES, true);
        placesFragmentFragment.setArguments(args);


            fragmentManager.beginTransaction()
                    .add(R.id.places_fragment, placesFragmentFragment)
                    .addToBackStack("places_fragm")
                    .commit();
    }
    public void onEnterFromSelectPlace(int placeID, String name){
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.add_new_reminder));
        addButton.setVisibility(View.VISIBLE);

        this.placeID = placeID;
        placeNameTxt.setText(name);
        handlCountDown.removeCallbacks(placesAnimTimer);
    }

    //********************************************************************************************** Arrive / Leave Buttons Listener
    private class arriveOrOutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CURRENT_STATE = (int) v.getTag();
            setInOutButtonsState(CURRENT_STATE);
        }
    }

    //********************************************************************************************** Settings / Actions Buttons Listener
    private class actionsAndSettingsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String buttonTag = (String) v.getTag();

            if (buttonTag.equals(REMINDER_SETTINGS)) {
                showDialogSettings();

            }else if (buttonTag.equals(REMINDER_ACTIONS)){
               showDialogActions();
            }
        }
    }


    // --------------------------------------------------------------------------------------------- Show Dialog Settings
    private void showDialogSettings() {
      DialogFragment newFragment = new DialogSettings();
      Bundle args = new Bundle();
      args.putInt(REMINDER_SETTINGS, CURRENT_SETTINGS);
      newFragment.setArguments(args);
      newFragment.show(getSupportFragmentManager(), "dialog_settings");
    }

    // Get data from Dialog Settings
    public void onSettingsChanges(int dataSettings) {
        CURRENT_SETTINGS = dataSettings;
        setSettingsAndActions();
    }

    // --------------------------------------------------------------------------------------------- Show Dialog Actions
    private void showDialogActions(){
        DialogFragment newFragment = new DialogActions();
        Bundle args = new Bundle();
        args.putString(REMINDER_ACTIONS, smsContact);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "dialog_actions");

    }

    // Get data from Dialog Actions
    public void onActionsSetSMS(String smsContact, String smsNumber, String smsMessage) {
        this.smsContact = smsContact;
        this.smsNumber = smsNumber;
        this.smsMessage = smsMessage;

        setInfoOnActionButton();
    }

    private void setInfoOnActionButton(){
        // ----------------------------------------------------------------------------------------- ACTIONS
        final TextView actionsTxt = findViewById(R.id.action_tv);
        final TextView actionsDescrTxt = findViewById(R.id.action_desc_tv);
        String actions;
        if( smsContact == null) {
            actions = getString(R.string.action) + " " + getString(R.string.act_remind_only);
            actionsDescrTxt.setVisibility(View.GONE);
            CURRENT_ACTION = ACTION_REMIND_ONLY;
        }else{
            actions = getString(R.string.action) + " " + getString(R.string.sms_header);
            String descr = getString(R.string.action_message_btn) + " " + smsContact;
            actionsDescrTxt.setVisibility(View.VISIBLE);
            actionsDescrTxt.setText(descr);
            CURRENT_ACTION = ACTION_SENS_SMS;
        }
        actionsTxt.setText(actions);
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
            handlCountDown.postDelayed(placesAnimTimer, 3200);}
    };


    // ********************************************************************************************* Settings And Actions
    private void setSettingsAndActions(){
        final TextView weekDays = findViewById(R.id.week_days);
        weekDays.setVisibility(View.GONE);

        // ----------------------------------------------------------------------------------------- SETTINGS
        final TextView settingsTxt = findViewById(R.id.settings_tv);
        String currSettings;

        if((CURRENT_SETTINGS & REMIND_ONCE) > 0){ // ----------------------------------- Remind Once
            currSettings = getString(R.string.remind_once);

        }else if ((CURRENT_SETTINGS & REMIND_ALWAYS) > 0){ // ------------------------ Remind Always
            currSettings = getString(R.string.remind_always);

        }else { // ------------------------------------------------------ Remind on days of the week
            currSettings = getString(R.string.remind_me_on);
            weekDays.setVisibility(View.VISIBLE);
            showPickedWeekDays();
        }

        settingsTxt.setText(currSettings);
    }

    // ********************************************************************************************* Picked Week Days
    private void showPickedWeekDays(){
        final TextView weekDaysTv = findViewById(R.id.week_days);
        SpannableString[] finalString = new SpannableString[7];

        for(int i=0; i<=6; i++) {
            String dayName = "day_" + Integer.toString(i);
            int dayNameRef = getResources().getIdentifier
                    (dayName, "string", getPackageName());
            String originalText = getString(dayNameRef);

            if (i >0 ) originalText = "  " + originalText;
            SpannableString highlighted = new SpannableString(originalText);

            int currDay = AddReminderActivity.WEEK_DAYS[i];

            if ((CURRENT_SETTINGS & currDay) > 0){
               highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                               (this, R.color.colorPrimary)),
                       0, originalText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            finalString[i]= highlighted;
        }
        weekDaysTv.setText(TextUtils.concat(finalString));
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

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
