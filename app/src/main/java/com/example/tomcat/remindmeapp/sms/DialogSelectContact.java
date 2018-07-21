package com.example.tomcat.remindmeapp.sms;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tomcat.remindmeapp.R;

import java.util.ArrayList;


public class DialogSelectContact extends DialogFragment{

    public DialogInterface.OnDismissListener onDismissListener;
    ListView lvContacts;
    View view;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_contacts, container, true);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);

        showContacts();
        return view;
    }

    // --------------------------------------------------------------------------------------------- Dismiss Dialog Listener
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private void showContacts() {
        // Check the permission is already granted or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted
            ArrayList<Contact> listContacts = new ContactFetcher(getActivity()).fetchAll();
            lvContacts = view.findViewById(R.id.lvContacts);
            ContactsAdapter adapterContacts = new ContactsAdapter(getActivity(), listContacts, getDialog());
            lvContacts.setAdapter(adapterContacts);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(getActivity(), getString(R.string.warning_permission),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
