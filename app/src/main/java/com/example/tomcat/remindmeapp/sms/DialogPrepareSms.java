package com.example.tomcat.remindmeapp.sms;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.R;
import com.example.tomcat.remindmeapp.utilitis.Tools;

/**
 * Dialog prepare a text message - select contact and add txt
 */

public class DialogPrepareSms extends DialogFragment{
    public static String smsContact = null;
    public static String smsNumber = null;

    public DialogInterface.OnDismissListener onDismissSmsListener;

    Button selectContact;
    TextInputLayout inputTxtLay;
    TextInputEditText inputTxt;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ViewGroup nullParent = null;
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_sms, nullParent);

        selectContact = view.findViewById(R.id.btn_send_sms);
        Tools.setButtonAddDelete(true, selectContact, getActivity());

        // ---------------------------------------------------------------------- Input Text Message
        inputTxtLay = view.findViewById(R.id.sms_input);
        inputTxt = view.findViewById(R.id.text_input_sms);

        selectContact.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
              setButtonAddContact();
            }
        });

        // -------------------------------------------------------------------------- Positive Button
        builder.setPositiveButton(getString(R.string.ok), null); //Setting to null. Is override bellow

        // ------------------------------------------------------------------------- Negative Button
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getDialog().dismiss();
                    }
                }
        );
        builder.setView(view);
        return builder.create();
    }


    // ------------------------------------------------------------------- Positive Button @Override
    // Added to disable closing the dialog by positive button without meeting the conditions
    @Override
    public void onResume() {
        super.onResume();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((selectContact.isSelected() ||(inputTxt.getText().toString().isEmpty()))){

                    if (inputTxt.getText().toString().isEmpty()) {
                        inputTxtLay.setError(getString(R.string.enter_message_txt));
                    } else {
                        inputTxtLay.setError(null);
                    }

                    if (selectContact.isSelected()) selectContact.setTextColor(Color.RED);

                }else{
                    String smsMessage = inputTxt.getText().toString();
                    // Set data to Activity
                    ((AddReminderActivity) getActivity()).onActionsSetSMS
                            (smsContact, smsNumber, smsMessage);
                    getDialog().dismiss();
                }
            }
        });
    }

    // --------------------------------------------------------------------------------------------- Dismiss Dialog Listener
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissSmsListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissSmsListener != null) {
            onDismissSmsListener.onDismiss(dialog);
        }
    }

    private void setButtonAddContact(){
        if (!selectContact.isSelected()){
            Tools.setButtonAddDelete(true, selectContact, getActivity());
            smsContact = null;
            smsNumber = null;
            selectContact.setText(getString(R.string.choose_contact));
        }else{
            DialogSelectContact dialogContacts = new DialogSelectContact();
            dialogContacts.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    settingsAfterSelectingContact();
                }
            });
            dialogContacts.show(getActivity().getSupportFragmentManager(), "dialog_actions");
        }
    }

    private void settingsAfterSelectingContact(){
        Tools.setButtonAddDelete(false, selectContact, getActivity());
        selectContact.setTextColor(Color.BLACK);
        selectContact.setText(smsContact);
    }


}
