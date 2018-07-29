package com.example.tomcat.remindmeapp;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomcat.remindmeapp.data.ActionsContract;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Actions;
import com.example.tomcat.remindmeapp.models.Reminder;
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
    @BindView(R.id.notes_input) EditText notesTxt;
    @BindView(R.id.select_place_tv)
    com.example.tomcat.remindmeapp.utilitis.TextViewRobotoLight placeNameTxt;

    private Handler handlCountDown;
    private boolean errorInput = false;
    private int countBackClick = 0;

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

    @SuppressWarnings("unused")
    public final static int REMIND_IS_INACTIVE = 0;
    public final static int REMIND_IS_ACTIVE = 1;

    public final static int WHEN_ENTER = 1;
    public final static int WHEN_EXIT = 2;
    static int CURRENT_STATE = WHEN_ENTER;

    final static String REMINDER_SETTINGS = "reminder"; //Cyclic or one-time reminder settings
    final static String REMINDER_ACTIONS = "actions"; // Action related to the reminder settings E.g. sending SMS
    public final static String REMINDER_PLACES = "places"; // Select Place

    static int CURRENT_SETTINGS = -1;

    final static int ACTION_REMIND_ONLY = 0;
    final static int ACTION_SEND_SMS = 1;
    static int CURRENT_ACTION = 0;

    private String smsContact = null;
    private String smsNumber = null;
    private String smsMessage = null;

    private String placeID = null;

    public final static String SELECTED_REMINDER = "sel_rem";
    public final static String NEW_OR_EDIT = "new_edit";
    public final static String SELECTED_PLACE = "sel_place";
    public final static String SELECTED_ACTION = "sel_action";
    public final static int NEW_REMINDER = 1;
    public final static int EDIT_REMINDER = 2;
    int editOrNewRem;
    int reminderID = -1;
    int actionID = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);
        init();

        assert getIntent().getExtras() != null;
        editOrNewRem = getIntent().getExtras().getInt(NEW_OR_EDIT);
        String title = null;

        switch(editOrNewRem) {
            case NEW_REMINDER: // ----------------------------------------------------------------- NEW REMINDER
                title = getString(R.string.add_new_reminder);
                CURRENT_SETTINGS = 250;
                break;

            case EDIT_REMINDER: // ----------------------------------------------------------------- EDIT REMINDER
                title = getString(R.string.edit_reminder);

                Reminder selecterReminder = getIntent().getParcelableExtra(SELECTED_REMINDER);
                //Places selectPlace = getIntent().getParcelableExtra(SELECTED_PLACE);

                reminderID = selecterReminder.getRemIDinDB();

                placeID = selecterReminder.getPlaceID();
                placeNameTxt.setText(
                        AppContentProvider.getPlaceNameBasedGoogleID(this, placeID)); // ---- Place

                CURRENT_STATE = selecterReminder.getInOut(); // -------------------------- In or OUT
                inputTxt.setText(selecterReminder.getName()); // --------------------- Reminder name

                CURRENT_SETTINGS = selecterReminder.getSettings(); // --------------------- Settings

                CURRENT_ACTION = selecterReminder.getAction();  // -------------------------- Action

                if (CURRENT_ACTION == ACTION_SEND_SMS) {  // ----------------------------------- SMS
                    Actions action = getIntent().getParcelableExtra(SELECTED_ACTION);
                    actionID = action.getActionIDinDB();
                    smsContact = action.getSmsContact();
                    smsNumber = action.getSmsNumber();
                    smsMessage = action.getSmsMessage();

                    Log.d("SMSTAg", "smsContact ININININI " + smsContact);
                }
                notesTxt.setText(selecterReminder.getNotes());  // --------------------------- NOTES

            default:
                break;
        }

        setInfoOnSettings();
        setInfoOnAction();

        Toolbar toolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        addButton.setVisibility(View.VISIBLE);

        // Default settings > BIN: 11111100
        setInOutButtonsState(CURRENT_STATE);
    }
    private void init(){
        handlCountDown = new Handler();
        handlCountDown.postDelayed(placesAnimTimer, 300);

        inButton.setClickable(true);
        inButton.setOnClickListener(new arriveOrOutListener());
        inButton.setTag(WHEN_ENTER);

        outButton.setClickable(true);
        outButton.setOnClickListener(new arriveOrOutListener());
        outButton.setTag(WHEN_EXIT);

        remindersButton.setClickable(true);
        remindersButton.setOnClickListener(new actionsAndSettingsListener());
        remindersButton.setTag(REMINDER_SETTINGS);

        actionsButton.setClickable(true);
        actionsButton.setOnClickListener(new actionsAndSettingsListener());
        actionsButton.setTag(REMINDER_ACTIONS);

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

    // --------------------------------------------------------------------------------------------- Add Remind
    private void addRemind() {
        String remName = inputTxt.getText().toString();

        if ((placeID == null || (remName.isEmpty()))) {

            errorInput = true;

            if (remName.isEmpty()) {
                inputTxtLay.setError(getString(R.string.enter_reminder_txt));
            } else {
                inputTxtLay.setError(null);
            }

            if (placeID == null) {
                placeNameTxt.setTextColor(Color.RED);
            }

        } else {
            writingData();
            errorInput = false;
        }
    }

    // ********************************************************************************************* Writing Data to DB
    private void writingData(){
        int smsID;
        ContentValues contentValues = new ContentValues();

        // ----------------------------------------------------------------------------------------- Action SMS
        smsID = -1;

        Log.d("SMSTAg", "CURRENT_ACTION " + CURRENT_ACTION);

        if(CURRENT_ACTION == ACTION_SEND_SMS) {
            contentValues.put(ActionsContract.ActionsEntry.COLUMN_SMS_CONTACT, smsContact);
            contentValues.put(ActionsContract.ActionsEntry.COLUMN_SMS_NUMBER, smsNumber);
            contentValues.put(ActionsContract.ActionsEntry.COLUMN_SMS_MESSAGE, smsMessage);

            Log.d("SMSTAg", "smsContact " + smsContact);

            if (editOrNewRem == NEW_REMINDER || editOrNewRem == EDIT_REMINDER) {

                Uri uri = getContentResolver().insert(ActionsContract.ActionsEntry.CONTENT_URI, contentValues);
                assert uri != null;
                smsID = (Long.valueOf(uri.getLastPathSegment())).intValue(); // Get action sms ID
                Log.d("SMSTAg", "smsID " + smsID);
            }

            /*if (editOrNewRem == NEW_REMINDER) {
                Log.d("SMSTAg", "smsID " + smsID);
                Uri uri = getContentResolver().insert(ActionsContract.ActionsEntry.CONTENT_URI, contentValues);
                assert uri != null;
                smsID = (Long.valueOf(uri.getLastPathSegment())).intValue(); // Get action sms ID
            }*/

            if (editOrNewRem == EDIT_REMINDER && actionID > 0) {
                String stringId = Integer.toString(actionID);
                Uri uriEdit = ActionsContract.ActionsEntry.CONTENT_URI;
                uriEdit = uriEdit.buildUpon().appendPath(stringId).build();
                Log.d("SMSTAg", "Edit Reminder " + stringId);

                getContentResolver().update(uriEdit, contentValues, null, null);
            }
        }

        // ----------------------------------------------------------------------------------------- Reminder
        contentValues.clear();
        // ------------------------------------------------------------------------------------ Name
        String remName = inputTxt.getText().toString();
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_NAME, remName);

        // ------------------------------------------------------------------- Remind when In or Out
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_IN_OR_OUT, CURRENT_STATE);

        // -------------------------------------------------------------------------- Place ID in DB
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID, placeID);

        // ---------------------------------------------------------------------- Reminder is ACTIVE
        contentValues.put(RemindersContract.RemindersEntry.COLUMNM_ACTIVE, REMIND_IS_ACTIVE);

        // ----------------------------------------------------------------------- Reminder SETTINGS
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_REMIND_SETTINGS, CURRENT_SETTINGS);

        // ------------------------------------------------------------------------- Reminder ACTION
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_REMIND_ACTION, CURRENT_ACTION);

        // ---------------------------------------------------------------------------------- SMS ID
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_REMIND_SMS_ID, smsID);

        // ----------------------------------------------------------------------------------- NOTES
        String notes = notesTxt.getText().toString();
        contentValues.put(RemindersContract.RemindersEntry.COLUMN_NOTES, notes);

        if (editOrNewRem == NEW_REMINDER) {
            getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, contentValues);
        }

        if (editOrNewRem == EDIT_REMINDER) {
            String stringId = Integer.toString(reminderID);
            Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            getContentResolver().update(uri, contentValues, null, null);
        }



        //Uri uri = getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI,
        // contentValues);

        /*if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }*/
        finish();
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
    public void onEnterFromSelectPlace(String placeID, String name){
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(getString(R.string.add_new_reminder));
        addButton.setVisibility(View.VISIBLE);

        this.placeID = placeID;
        placeNameTxt.setText(name);
        placeNameTxt.setTextColor(Color.BLACK);
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
        setInfoOnSettings();
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

        setInfoOnAction();
    }

    private void setInfoOnAction(){
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
            CURRENT_ACTION = ACTION_SEND_SMS;
        }
        actionsTxt.setText(actions);
    }

    private void setInOutButtonsState(int currState){
        final TextView inTv = findViewById(R.id.in_tv);
        final TextView outTv = findViewById(R.id.out_tv);
        final ImageView inOutImage = findViewById(R.id.in_out_image);

        if (currState == WHEN_ENTER){
            inTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            outTv.setTextColor(getResources().getColor(R.color.gray));
            inOutImage.setBackground(getResources().getDrawable(R.drawable.in_place));
        }else if (currState == WHEN_EXIT){
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
    private void setInfoOnSettings(){
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

    public void addButton(View view){
        addRemind();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        addRemind();

        if(errorInput && countBackClick < 1){
            countBackClick++;
        }else{
            finish();
            if(errorInput) Toast.makeText(getBaseContext(),
                    R.string.no_reminder_data, Toast.LENGTH_LONG).show();
        }

    }
}
