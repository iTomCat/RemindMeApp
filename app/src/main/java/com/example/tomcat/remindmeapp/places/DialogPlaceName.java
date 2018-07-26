package com.example.tomcat.remindmeapp.places;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.tomcat.remindmeapp.sms.DialogPrepareSms;
import com.example.tomcat.remindmeapp.sms.DialogSelectContact;
import com.example.tomcat.remindmeapp.utilitis.Tools;

/**
 * Dialog - Enter Place Name
 */

public class DialogPlaceName extends DialogFragment {
    public static String placeName = null;

    //public DialogInterface.OnDismissListener onDismissSmsListener;

    //Button selectContact;
    TextInputLayout inputTxtLay;
    TextInputEditText inputTxt;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ViewGroup nullParent = null;
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_place_name, nullParent);

        // ---------------------------------------------------------------------- Input Text Message
        inputTxtLay = view.findViewById(R.id.place_name_input);
        inputTxt = view.findViewById(R.id.text_input_place_name);

        // -------------------------------------------------------------------------- Positive Button
        builder.setPositiveButton(getString(R.string.ok), null); //Setting to null. Is override bellow

        // ------------------------------------------------------------------------- Negative Button
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent data = new Intent();
                        data.putExtra(PlacesFragment.PALCE_NAME_DATA, getString(R.string.cancel));

                        // Set to onActivityResult
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(), PlacesFragment.PALCE_NAME_REQUEST, data);

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
                if (inputTxt.getText().toString().isEmpty()) {


                        inputTxtLay.setError(getString(R.string.error_place_name));


                } else {
                    inputTxtLay.setError(null);
                    String placeName = inputTxt.getText().toString();
                    Intent data = new Intent();
                    data.putExtra(PlacesFragment.PALCE_NAME_DATA, placeName);

                    // Set to onActivityResult
                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(), PlacesFragment.PALCE_NAME_REQUEST, data);

                    getDialog().dismiss();
                }
            }
        });
    }
}
