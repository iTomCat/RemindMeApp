package com.example.tomcat.remindmeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.ArrayList;

/**
 * Fragment with Active RemindersFragment - Reminder List
 */

public class RemindersFragment extends Fragment implements
        ReminderAdapter.ReciepeAdapterOnClickHandler{

    private ArrayList<Reminder> mReminders = null;
    public ReminderAdapter.ReciepeAdapterOnClickHandler mClickHandler = this;

    public RemindersFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_fragment, container, false);



        mReminders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

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

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view_reminders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("RemFrag", "Moved " );
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                Log.d("RemFrag", "Long Presed " );
                return super.isLongPressDragEnabled();
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                Log.d("RemFrag", "Swipped " + swipeDir);

            }


           /* @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getItemViewType() == ITEM_TYPE_ACTION_WIDTH_NO_SPRING) return 0;
                return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                        ItemTouchHelper.START|ItemTouchHelper.END);
            }*/

          /*  @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ReminderAdapter.ViewHolder){
                    Log.d("RemFrag", "aaaaa" );
                    return 0;
                }
                Log.d("RemFrag", "bbbb" );
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            */
        }).attachToRecyclerView(mRecyclerView);


        return view;
    }

    // ********************************************************************************************* Clics on Reminder List
    @Override
    public void onClick(int position) {
        Log.d("RemFrag", "Button List " + position);
    }
}
