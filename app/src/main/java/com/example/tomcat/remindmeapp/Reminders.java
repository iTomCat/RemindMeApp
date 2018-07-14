package com.example.tomcat.remindmeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.ArrayList;

/**
 * Fragment with Active Reminders - Reminder List
 */

public class Reminders extends Fragment implements
        ReminderAdapter.ReciepeAdapterOnClickHandler{

    private ArrayList<Reminder> mReminders = null;
    public ReminderAdapter.ReciepeAdapterOnClickHandler mClickHandler = this;

    public Reminders(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_fragment, container, false);

        /*getActivity().findViewById(R.id.fab_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TestOK", "Button");

                ScaleAnimation anim = new ScaleAnimation(0,1,0,1, 1, 0.5f, 1, 0.5f);
                anim.setFillBefore(true);
                anim.setFillAfter(true);
                anim.setFillEnabled(true);
                anim.setDuration(300);
                anim.setInterpolator(new OvershootInterpolator());
                getActivity().findViewById(R.id.fab_main).startAnimation(anim);
            }
        });*/


        mReminders = new ArrayList<>();
        for (int i = 0; i < 15; i++) {

            Reminder movie = new Reminder();

            // --------------------------------------------------------------------- Movie Title
            String name = "Tarlalala " + Integer.toString(i);
            movie.setRemindName(name);


            mReminders.add(movie);
            Log.d("RemFrag", "Data " + i + "  " + movie.getRemindName());
        }

        Log.d("RemFrag", "Data " + mReminders.get(1).getRemindName());


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);

        ReminderAdapter adapter = new ReminderAdapter(mReminders, mClickHandler);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_reminders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        return view;
    }

    // ********************************************************************************************* Clics on Reminder List
    @Override
    public void onClick(int position) {
        Log.d("RemFrag", "Button List " + position);
    }
}
