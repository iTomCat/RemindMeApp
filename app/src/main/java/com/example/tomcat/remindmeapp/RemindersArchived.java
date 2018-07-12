package com.example.tomcat.remindmeapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * Fragment with Archived Reminders - Archiver Reminder List
 */

public class RemindersArchived extends Fragment{

    public RemindersArchived(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reminders_fragment, container, false);
        TextView txt = rootView.findViewById(R.id.txt_fragm);
        txt.setText("lalala");
        return rootView;
    }

}
