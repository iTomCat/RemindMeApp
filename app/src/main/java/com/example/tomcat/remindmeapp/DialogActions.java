package com.example.tomcat.remindmeapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.tomcat.remindmeapp.sms.DialogPrepareSms;
import com.example.tomcat.remindmeapp.utilitis.Tools;

/**
 * Dialog Actions - Action related to the reminder settings. E.g. sending SMS
 */

public class DialogActions extends DialogFragment {
    Button sendSMS;
    String currSmsContact;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ViewGroup nullParent = null;
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_actions, nullParent);

        currSmsContact = getArguments().getString(AddReminderActivity.REMINDER_ACTIONS);

        // ------------------------------------------------------------------------ Description Text
        final com.example.tomcat.remindmeapp.utilitis.TextViewRoboto
                whenYouTxt = view.findViewById(R.id.when_you);
        String currString;
        if (AddReminderActivity.CURRENT_STATE == AddReminderActivity.WHEN_ARRIVE){
           currString = getString(R.string.in) + ":";
        } else {
            currString = getString(R.string.out) + ":";
        }
        whenYouTxt.setText(currString);


        // ------------------------------------------------------------------------- Send SMS Button
        sendSMS = view.findViewById(R.id.btn_send_sms);
        setButtonSendSMS();
        sendSMS.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setButtonAddContact();
            }
        });

        // -------------------------------------------------------------------------- Positive Button
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Set data to Activity
                        //((AddReminderActivity) getActivity()).onActionsSetSMS(777);
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

    private void setButtonSendSMS(){
        if (currSmsContact == null){
            Tools.setButtonAddDelete(true, sendSMS, getActivity());
        }else{
            Tools.setButtonAddDelete(false, sendSMS, getActivity());
            String buttonInfo = getString(R.string.enter_message_btn) + " " + currSmsContact;
            sendSMS.setText(buttonInfo);
        }
    }

    private void setButtonAddContact(){
        if (!sendSMS.isSelected()){
            Tools.setButtonAddDelete(true, sendSMS, getActivity());
            ((AddReminderActivity) getActivity()).onActionsSetSMS
                    (null, null, null);
            sendSMS.setText(getString(R.string.actn_send_sms));
        }else{
            DialogPrepareSms dialogSMS = new DialogPrepareSms();
            dialogSMS.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getDialog().dismiss();
                }
            });
            dialogSMS.show(getActivity().getSupportFragmentManager(), "dialog_actions");
        }
    }


}
