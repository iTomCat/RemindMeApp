package com.example.tomcat.remindmeapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.animation.Animation;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Add Reminder Activity
 */

public class AddReminderActivity extends AppCompatActivity{
    //@BindView(R.id.in_btn) ImageView inButton;
    //@BindView(R.id.text_input_txt)TextInputEditText inputTxt;
    final int WHEN_ARRIVE = 0;
    final int WHEN_GET_OUT = 1;
    int CURRENT_STATE = WHEN_ARRIVE;
    private Handler handlCountDown;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        ButterKnife.bind(this);

        /*Toolbar toolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

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




        // -----------------------------------------------------------------------------------------
        final TextView weekDaysTv = findViewById(R.id.week_days);
        SpannableString[] finalString1 = new SpannableString[7];

        for(int i=0; i<=6; i++) {

            String dayName = "day_" + Integer.toString(i);
            int dayNameRef = getResources().getIdentifier(dayName, "string", getPackageName());
            //String originalText = "dupa biskupa";
            String originalText = getString(dayNameRef);

            if (i >0 ) originalText = "  " + originalText;

            //Spannable highlighted = new SpannableString(originalText);
            SpannableString highlighted = new SpannableString(originalText);
            //Log.d("SpannTest", "Name: " + originalText);


            if (i == 1 || i == 4) {
                highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                                (this, R.color.colorPrimary)),
                        0, originalText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            finalString1[i]= highlighted;
        }
        weekDaysTv.setText(TextUtils.concat(finalString1));
        Log.d("SpannTest", "Table: " + finalString1[0]);


        // SPANNABLE OK
        /*SpannableString[] finalString = new SpannableString[3];
        String originalText = "dupa biskupa" + " ";
        String originalText3 = " " + "miimimim";
        SpannableString highlighted = new SpannableString(originalText);
        SpannableString highlighted2 = new SpannableString("alllaaaa");
        SpannableString highlighted3 = new SpannableString(originalText3);

            highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                            (this, R.color.colorPrimary)),
                    0, originalText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        highlighted3.setSpan(new ForegroundColorSpan(ContextCompat.getColor
                        (this, R.color.colorPrimary)),
                0, originalText3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        finalString[0] = highlighted;
        finalString[1] = highlighted2;
        finalString[2] = highlighted3;

        weekDaysTv.setText(TextUtils.concat(finalString));*/


        // --------------------------------------------------------------------------




        //Add Data OK:
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
        inButton.setOnClickListener(new buttonsListener());
        inButton.setTag(WHEN_ARRIVE);

        ImageView outButton = findViewById(R.id.out_btn);
        outButton.setClickable(true);
        outButton.setOnClickListener(new buttonsListener());
        outButton.setTag(WHEN_GET_OUT);
    }

    //**********************************************************************************************  Buttons Listener
    private class buttonsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //int buttonTag = (int) v.getTag();
            CURRENT_STATE = (int) v.getTag();
            setInOutButtonsState(CURRENT_STATE);

           /* if (buttonTag == WHEN_ARRIVE) { // -----------------------------------------------------
                Log.d("AddRem", "Click IN");
                CURRENT_STATE == WHEN_ARRIVE
            }else if (buttonTag == WHEN_GET_OUT){
                Log.d("AddRem", "Click OUT");
            } else { // ----------------------------------------------------------------------------
              //
            }*/
        }
    }

    private void setInOutButtonsState(int currState){
        final TextView inTv = findViewById(R.id.in_tv);
        final TextView outTv = findViewById(R.id.out_tv);
        final ImageView inOutImage = findViewById(R.id.in_out_image);

        if (currState == WHEN_ARRIVE){
            inTv.setTextColor(getResources().getColor(R.color.colorPrimary));
            outTv.setTextColor(getResources().getColor(R.color.colorGray));
            inOutImage.setBackground(getResources().getDrawable(R.drawable.in_place));
        }else if (currState == WHEN_GET_OUT){
            inTv.setTextColor(getResources().getColor(R.color.colorGray));
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
