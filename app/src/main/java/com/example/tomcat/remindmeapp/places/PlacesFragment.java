package com.example.tomcat.remindmeapp.places;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.MainActivity;
import com.example.tomcat.remindmeapp.R;
import com.example.tomcat.remindmeapp.RemindersFragment;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Places;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment Displays a list with your places
 */

public class PlacesFragment extends Fragment implements
        MainActivity.FabButtonListenerFromActivity,
        PlacesAdapter.PlacesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public PlacesAdapter.PlacesAdapterOnClickHandler mClickHandler = this;

    private GoogleApiClient mClient;
    private Geofencing mGeofencing;


    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final int PALCE_NAME_REQUEST = 2;
    public static final String PALCE_NAME_DATA = "place_name";
    public static final String PLACE_EXIST = "place_exist";

    private PlacesAdapter adapter;
    private final static int PLACES_ID_LOADER = 33;
    private List<Places> mPlacesList = null;
    String placeID = null;

    @BindView(R.id.fab_places_fragm) ImageButton fabPlus;
    private Unbinder unbinder;
    private boolean ifEnterFromAddReminder = false;
    private boolean placeWasAdded = false;
    //Cursor mRemindersData = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), this)
                .build();
        mGeofencing = new Geofencing(getActivity(), mClient);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.places_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new PlacesAdapter(mClickHandler);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view_places);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadPlacesFromDB();

        // ------- Checks if the entrance was from the main Activity or from the AddReminderActivity
        if(getArguments()!=null){
            ifEnterFromAddReminder = getArguments()
                    .getBoolean(AddReminderActivity.REMINDER_PLACES, false);
        }

        if(ifEnterFromAddReminder) { // ------------------------------------ Enter from Add Reminder
            fabButtonFromFragment();
            // Start Fab Button listener form Fragment
        }else{ // --------------------------------------------------------- Enter from Main Activity
            ((MainActivity) getActivity()).setActivityListener(PlacesFragment.this);
            // Start Fab Button listener form Activity
        }
    }

    @Override
    public void fabButtonFromActivity() {
                addPlace();
    }
    private void fabButtonFromFragment(){
        fabPlus.setVisibility(View.VISIBLE);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifEnterFromAddReminder) placeWasAdded = true;
                addPlace();
            }
        });
    }

    public void refreshGeoPlacesData(List<Places> placesList) {
        Log.d("GeofTest", "REFRESH");

        List<String> guids = new ArrayList<>();
        for (Places placeModel : placesList) {
            String placeID = placeModel.getPlaceGoogleID();
            guids.add(placeID);
        }

        PendingResult<PlaceBuffer> placeResult = com.google.android.gms.location.places.Places.GeoDataApi.getPlaceById(mClient,
                guids.toArray(new String[guids.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                //mAdapter.swapPlaces(places);
                mGeofencing.updateGeofencesList(places);
                //if (mIsEnabled) mGeofencing.registerAllGeofences();
                mGeofencing.registerAllGeofences();
            }
        });
    }

    // ********************************************************************************************* Add Place PlacePicker
    public void addPlace(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent i = builder.build(getActivity());
                startActivityForResult(i, PLACE_PICKER_REQUEST);
                //activity.startActivity(i);
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
                Toast.makeText(getActivity(), getActivity().getString(R.string.play_services_problem),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
                Toast.makeText(getActivity(), getActivity().getString(R.string.play_services_problem),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ----------------------------------------------------------------------------------------- PICK PLACE
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            String placeName;

            Place place = PlacePicker.getPlace(getActivity(), data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }
            placeID = place.getId();

            // --------------------------------------------- The place was already added to the list
            if (AppContentProvider.checkGoogleIdInDB(getActivity(), placeID)){
                placeName = AppContentProvider.getPlaceNameBasedGoogleID(getActivity(), placeID);

            }else{ // ----------------------------------------------------------------- Place is New
                placeName = null;
            }

            showDialogPlaceName(placeName);
        }

        // ----------------------------------------------------------------------------------------- ENTER PLACE NAME
        if (requestCode == PALCE_NAME_REQUEST ) {
            assert  data.getExtras() != null;
            String placeName = data.getExtras().getString(PALCE_NAME_DATA);
            boolean placeExisted = data.getExtras().getBoolean(PLACE_EXIST);

            assert placeName != null;
            if(!placeName.equals(getString(R.string.cancel))){ // ------------------------------- OK
                addPlaceToDB(placeName, placeID, placeExisted);
            }else{ // ----------------------------------------------------------------------- CANCEL

                Toast.makeText(getActivity(),getString(R.string.add_place_cancel),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // --------------------------------------------------------------------------------------------- Writing Place to DB
    private void addPlaceToDB(String placeName, String placeID, boolean plceExisted){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID, placeID);
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME, placeName);

        if (!plceExisted) { // ----------------------------------------------------------- New Place
            getActivity().getContentResolver()
                    .insert(PlacesContract.PlacesEntry.CONTENT_URI, contentValues);
        }else { // ---------------------------------------------------------------------- Edit Place
            Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(placeID).build();

            getActivity().getContentResolver().update(uri, contentValues, null, null);
        }

        //assert uri != null;
        //int placeIDinDB = (Long.valueOf(uri.getLastPathSegment())).intValue(); // Get action sms ID

        if(ifEnterFromAddReminder && placeWasAdded)closeFragment(placeName, placeID);

    }

    // --------------------------------------------------------------------------------------------- Show Dialog Place Name
    private void showDialogPlaceName(String placeName) {
        DialogFragment dialogPlaceName = new DialogPlaceName();
        Bundle args = new Bundle();
        args.putString(PALCE_NAME_DATA, placeName);
        dialogPlaceName.setArguments(args);
        dialogPlaceName.setTargetFragment(this, PALCE_NAME_REQUEST);
        dialogPlaceName.show(getActivity().getSupportFragmentManager(), "dialog_place_name");
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // ********************************************************************************************* Click on Places List
    @Override
    public void onClick(int position, boolean longClick) {
        // click from listener

        if(longClick){ // -------------------------------------------------------------- onLongClick
            deletePlaceDialog(position);
        }else{// --------------------------------------------------------------------------- onClick

            if (ifEnterFromAddReminder) { // --------------------------------------when Add Reminder
                String placeName = mPlacesList.get(position).getPlaceName();
                String placeIDinDB = mPlacesList.get(position).getPlaceGoogleID();
                closeFragment(placeName, placeIDinDB);
            }
        }
    }

    // --------------------------------------------------------------------------------------------- Dialog Delete Place
    private void deletePlaceDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.removing_place));
        builder.setMessage(getString(R.string.removing_place_message));

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Checking if the place is used in reminders
                StringBuilder remindersNames = new StringBuilder();
                ArrayList<Integer> remindersID = new ArrayList<>();
                Cursor reminderCursor = RemindersFragment.mRemindersData;

                for(int i=0; i<reminderCursor.getCount(); i++) {
                    RemindersFragment.mRemindersData.moveToPosition(i);

                    final int reminderPlaceIDcolumn = reminderCursor.getColumnIndex
                            (RemindersContract.RemindersEntry.COLUMN_PLACES_GOOGLE_ID);
                    String id =  reminderCursor.getString(reminderPlaceIDcolumn);

                    String requiredGoogleID =
                            AppContentProvider.getGoogleIDbyID(getActivity(), position);



                     if (id.equals(requiredGoogleID)) {
                        int reminderNameColumn = reminderCursor.getColumnIndex
                                (RemindersContract.RemindersEntry.COLUMN_NAME);
                        String reminderName = reminderCursor.getString(reminderNameColumn);

                        remindersNames.append(reminderName);
                        if (i<reminderCursor.getCount() -1) {
                            remindersNames.append(", ");
                        }

                        final int IDcolumn = reminderCursor.getColumnIndex
                                (RemindersContract.RemindersEntry._ID);
                        int reminderID = reminderCursor.getInt(IDcolumn);
                            remindersID.add(reminderID);
                    }
                }

                if (remindersID.size() > 0){
                    placeIsUsedInRemindersDialog(position, remindersNames.toString(), remindersID);
                }else{
                    deletePlace(position);
                }

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


    // --------------------------------------------------------------------------------------------- Dialog if the place is used in reminders
    private void  placeIsUsedInRemindersDialog(final int position, String places, final ArrayList<Integer> remindersID){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.removing_place));

        String removingMessage = getString(R.string.removing_place_in_reminders) + "\n"  + "\n"
                + places + "\n" + "\n"
                + getString(R.string.removing_place_in_reminders_end);
        builder.setMessage(removingMessage);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deletePlace(position);
                deleteRemindersWithRemovablePlaces(remindersID);
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

    private void deletePlace(int position){
        // Delete Row
        String stringId = Integer.toString(position);
        Uri uri = PlacesContract.PlacesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        getActivity().getContentResolver().delete(uri, null, null);
        loadPlacesFromDB();

    }

    private void deleteRemindersWithRemovablePlaces(ArrayList<Integer> remindersID){
        for(int i=0; i<remindersID.size(); i++) {
            int currID = remindersID.get(i);
            String stringId = Integer.toString(currID);
            Uri uri = RemindersContract.RemindersEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            getActivity().getContentResolver().delete(uri, null, null);
        }
    }

    private void closeFragment(String placeName, String placeIDinDB){
        ((AddReminderActivity) getActivity()).onEnterFromSelectPlace(placeIDinDB, placeName);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    // *********************************************************************************************
    // ********************************************************************************************* Start Load
    private void loadPlacesFromDB() {
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Object movieSearchLoader = loaderManager.getLoader(PLACES_ID_LOADER);

        if (movieSearchLoader == null){
            loaderManager.initLoader(PLACES_ID_LOADER, null, this);
        }else {
            loaderManager.restartLoader(PLACES_ID_LOADER, null, this);
        }
    }

    // --------------------------------------------------------------------------------------------- Async Task Loader
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                PlacesContract.PlacesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object loadedData) {
        Cursor mPlacesData = (Cursor) loadedData;
        mPlacesList = AppContentProvider.placesListFromCursor(mPlacesData);
        adapter.setRemindersData(mPlacesList);

        if (mPlacesList.size() > 0) refreshGeoPlacesData(mPlacesList);

        // if Place List is Empty - Enter to Add Place Dialog
        if(ifEnterFromAddReminder && (mPlacesList.size() == 0) && (placeID == null)){
            addPlace();
            placeWasAdded = true;
        }
    }
    @Override
    public void onLoaderReset(Loader loader) {
        adapter.refresh();
    }

    // ********************************************************************************************* Google Play Services
    @Override // ----------------------- Called when the Google API Client is successfully connected
    public void onConnected(@Nullable Bundle bundle) {
        if (mPlacesList.size() > 0) refreshGeoPlacesData(mPlacesList);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
