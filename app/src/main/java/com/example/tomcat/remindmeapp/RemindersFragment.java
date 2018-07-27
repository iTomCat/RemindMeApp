package com.example.tomcat.remindmeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomcat.remindmeapp.data.ActionsContract;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Actions;
import com.example.tomcat.remindmeapp.models.Places;
import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.List;

/**
 * Fragment with Active RemindersFragment - Reminder List
 */

public class RemindersFragment extends Fragment implements
        ReminderAdapter.ReminderAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks{

    public ReminderAdapter.ReminderAdapterOnClickHandler mClickHandler = this;

    private final static int REMINDERS_ID_LOADER = 24;
    private final static int PLACES_ID_LOADER = 26;
    private final static int ACTIONS_ID_LOADER = 28;

    private List<Reminder> mReminderList = null;
    List<Places> mPlacesList = null;
    List<Actions> mActionsList = null;
    private ReminderAdapter adapter;
    public static Cursor mRemindersData;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new ReminderAdapter(mClickHandler);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view_reminders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

       /* new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return super.isLongPressDragEnabled();
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
            }
        }).attachToRecyclerView(mRecyclerView);*/


        return view;
    }

    // ********************************************************************************************* Click on Reminder List
    @Override
    public void onClick(int position,  boolean logngClick) {

        if(logngClick){ // ------------------------------------------------------------------------- Long Click - DELETE REMINDER
            deleteReminder(position);
        }else{ // ---------------------------------------------------------------------------------- Click - EDIT REMINDER
            Reminder selectedReminder =  mReminderList.get(position);

            Intent intent = new Intent(getActivity(), AddReminderActivity.class);
            intent.putExtra(AddReminderActivity.SELECTED_REMINDER, selectedReminder);

            Bundle extras = intent.getExtras();
            assert extras != null;
            Bundle mBundle = new Bundle();

            mBundle.putInt(AddReminderActivity.NEW_OR_EDIT, AddReminderActivity.EDIT_REMINDER);

            // -------------------------------------------------------------------------- Place Name
            int currPlaceID = selectedReminder.getPlaceID();
            int posOnListByID = -1;
            //
            for(int i=0; i<mPlacesList.size(); i++){
                int currIDinDB = mPlacesList.get(i).getPlaceIDinDB();
                if (currIDinDB == currPlaceID){
                    posOnListByID = i;
                    break;
                }
            }

            Places selectedPlace =  mPlacesList.get(posOnListByID);
            intent.putExtra(AddReminderActivity.SELECTED_PLACE, selectedPlace);

            // ------------------------------------------------------------------------- Actions SMS
            if (selectedReminder.getAction() == AddReminderActivity.ACTION_SEND_SMS){
                int currActionID = selectedReminder.getSmsID();
                int posOnListByIDAction = -1;
                //
                for(int i=0; i<mActionsList.size(); i++){
                    int currIDinDB = mActionsList.get(i).getActionIDinDB();
                    if (currIDinDB == currActionID){
                        posOnListByIDAction = i;
                        break;
                    }
                }
                Actions selectedAction =  mActionsList.get(posOnListByIDAction);
                intent.putExtra(AddReminderActivity.SELECTED_ACTION, selectedAction);
            }

            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    // --------------------------------------------------------------------------------------------- Delete Reminder
    private void deleteReminder(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.removing_reminder));
        builder.setMessage(R.string.removing_reminder_message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String stringId = Integer.toString(position);
                Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getActivity().getContentResolver().delete(uri, null, null);

                loadFromDB(REMINDERS_ID_LOADER);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromDB(REMINDERS_ID_LOADER);
    }


    // *********************************************************************************************
    // ********************************************************************************************* Start Load
    private void loadFromDB(int laoder) {
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Object movieSearchLoader = loaderManager.getLoader(laoder);
        if (movieSearchLoader == null){
            //loaderManager.initLoader(REMINDERS_ID_LOADER, queryBundle, this);
            loaderManager.initLoader(laoder, null, this);
        }else {
            loaderManager.restartLoader(laoder, null, this);
        }
    }

    // --------------------------------------------------------------------------------------------- Async Task Loader
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch(id) {
            default: // ------------------------------------------------------------- Load REMINDERS
                return new CursorLoader(getActivity(),
                        RemindersContract.RemindersEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            case PLACES_ID_LOADER: // -------------------------------------------------- Load PLACES
                return new CursorLoader(getActivity(),
                        PlacesContract.PlacesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            case ACTIONS_ID_LOADER: // ------------------------------------------------ Load Actions
                return new CursorLoader(getActivity(),
                        ActionsContract.ActionsEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object loadedData) {
        switch (loader.getId()) {
            case REMINDERS_ID_LOADER:
                mRemindersData = (Cursor) loadedData;
                mReminderList = AppContentProvider.remindersListFromCursor(mRemindersData);
                loadFromDB(ACTIONS_ID_LOADER); // Start Load Actions form DB
                break;
            case ACTIONS_ID_LOADER:
                Cursor mActionsData = (Cursor) loadedData;
                mActionsList = AppContentProvider.actionsListFromCursor(mActionsData);
                loadFromDB(PLACES_ID_LOADER); // Start Load Places form DB
                break;
            case PLACES_ID_LOADER:
                Cursor mPlacesData = (Cursor) loadedData;
                mPlacesList = AppContentProvider.placesListFromCursor(mPlacesData);
                adapter.setRemindersData(mReminderList, mPlacesList);
                break;
            default:
                break;
        }
        //mRecyclerView.smoothScrollToPosition(listPos);
    }

    @Override
    public void onLoaderReset(Loader loader) {

        //TODO MAKE LOAD RESET  -- w popular movie jest pusty
        Log.d("RemFrag", "RESET " );
        adapter.refresh();
    }
}
