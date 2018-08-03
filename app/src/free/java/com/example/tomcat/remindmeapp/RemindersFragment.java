package com.example.tomcat.remindmeapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomcat.remindmeapp.data.ActionsContract;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Actions;
import com.example.tomcat.remindmeapp.models.Reminder;
import com.example.tomcat.remindmeapp.widget.RemindersWidget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment with Active RemindersFragment - Reminder List - Free Version
 */

public class RemindersFragment extends Fragment implements
        ReminderAdapter.ReminderAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks{

    private ReminderAdapter.ReminderAdapterOnClickHandler mClickHandler = this;
    private PublisherInterstitialAd mPublisherInterstitialAd = null;
    private boolean showAdv = true;

    private final static int REMINDERS_ID_LOADER = 24;
    private final static int ACTIONS_ID_LOADER = 28;

    private List<Reminder> mReminderList = null;
    List<Actions> mActionsList = null;
    private ReminderAdapter adapter;
    public static Cursor mRemindersData;
    private RecyclerView mRecyclerView;

    private Unbinder unbinder;
    TextView introTxt;
    ImageView imageIntro;

    public RemindersFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        // ----------------------------------------------------------------------------------------- Adver
        mPublisherInterstitialAd = new PublisherInterstitialAd(getContext());
        mPublisherInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        loadAdv();

        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if(showAdv) mPublisherInterstitialAd.show();
                showAdv = false;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                loadAdv();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                loadAdv();
            }
        });

        introTxt = getActivity().findViewById(R.id.remind_info);
        imageIntro = getActivity().findViewById(R.id.lines);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new ReminderAdapter(getActivity(), mClickHandler);

        mRecyclerView = view.findViewById(R.id.recycler_view_reminders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);


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
            String currPlaceID = selectedReminder.getPlaceID();

            Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(currPlaceID).build();

            Cursor cursor = getActivity().getContentResolver().query(uri,
                    null,
                    PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID,
                    null,
                    null);

            assert cursor != null;
            int IDinDB = cursor.getColumnIndex(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME);
            cursor.moveToFirst(); // MOVE TO FIRST
            String placeName = cursor.getString(IDinDB);
            cursor.close();


            intent.putExtra(AddReminderActivity.SELECTED_PLACE, placeName);

            // ------------------------------------------------------------------------- Actions SMS
            if (selectedReminder.getAction() == AddReminderActivity.ACTION_SEND_SMS){
                int currActionID = selectedReminder.getSmsID();
                Log.d("Lala", "currActionID " + currActionID);
                int posOnListByIDAction = -1;
                //
                for(int i=0; i<mActionsList.size(); i++){
                    int currIDinDB = mActionsList.get(i).getActionIDinDB();
                    if (currIDinDB == currActionID){
                        posOnListByIDAction = i;
                        break;
                    }
                }
                Log.d("Lala", "posOnListByIDAction " + posOnListByIDAction);
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

    // ********************************************************************************************* Update Widget
    private void setDataToWidget(){
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds
                (new ComponentName(getActivity(), RemindersWidget.class));

            RemindersWidget.updateIngriedentsWidgets(getContext(), appWidgetManager, appWidgetIds,
                    mReminderList);
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

                loadFromDB(ACTIONS_ID_LOADER); // Start Load Actions form DB
                break;
            case ACTIONS_ID_LOADER:
                Cursor mActionsData = (Cursor) loadedData;
                mActionsList = AppContentProvider.actionsListFromCursor(mActionsData);
                mReminderList = AppContentProvider.remindersListFromCursor(mRemindersData);
                setDataToWidget();

                adapter.setRemindersData(mReminderList);
                mRecyclerView.setAdapter(adapter);

                // Intro Txt
                if(MainActivity.selPage == 0) {
                    int visibility = (RemindersFragment.mRemindersData.getCount() > 0) ? View.GONE : View.VISIBLE;
                    introTxt.setVisibility(visibility);
                    imageIntro.setVisibility(visibility);
                }
                break;
            default:
                break;
        }
    }

    private void loadAdv(){
        PublisherAdRequest request = new PublisherAdRequest.Builder()
                .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")  // An example device ID
                .build();
        mPublisherInterstitialAd.loadAd(request);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //adapter.swapCursor(null);
        adapter.refresh();
    }
}
