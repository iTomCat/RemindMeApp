package com.example.tomcat.remindmeapp.places;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.MainActivity;
import com.example.tomcat.remindmeapp.R;
import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.models.Places;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

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
        LoaderManager.LoaderCallbacks{

    public PlacesAdapter.PlacesAdapterOnClickHandler mClickHandler = this;

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final int PALCE_NAME_REQUEST = 2;
    public static final String PALCE_NAME_DATA = "place_name";

    private PlacesAdapter adapter;
    private final static int PLACES_ID_LOADER = 28;
    private List<Places> mPlacesList = null;
    String placeID = null;

    @BindView(R.id.fab_places_fragm) ImageButton fabPlus;
    private Unbinder unbinder;
    private boolean ifEnterFromAddReminder = false;
    private boolean placeWasAdded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onResume() {
        super.onResume();
        makeMovieSearchQuery();

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

    /***
     * Called when the Place Picker Activity returns back with a selected place (or after canceling)
     *
     * @param requestCode The request code passed when calling startActivityForResult
     * @param resultCode  The result code specified by the second activity
     * @param data        The Intent that carries the result data.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ------------------------------------------------------------------------------ PICK PLACE
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getActivity(), data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            //Extract the place information from the API
            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            placeID = place.getId();
            Log.d("placeID", "placeName: " + placeName + " Adress: " + placeAddress);

            showDialogPlaceName();
        }

        // ------------------------------------------------------------------------ ENTER PLACE NAME
        if (requestCode == PALCE_NAME_REQUEST ) {
            assert  data.getExtras() != null;
            String placeName = data.getExtras().getString(PALCE_NAME_DATA);

            assert placeName != null;
            if(!placeName.equals(getString(R.string.cancel))){ // ------------------------------- OK
                addPlaceToDB(placeName, placeID);
            }else{ // ----------------------------------------------------------------------- CANCEL

                Toast.makeText(getActivity(),getString(R.string.add_place_cancel),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // --------------------------------------------------------------------------------------------- Writing Place to DB
    private void addPlaceToDB(String placeName, String placeID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_GOOGLE_ID, placeID);
        contentValues.put(PlacesContract.PlacesEntry.COLUMN_PLACE_NAME, placeName);
        Uri uri = getActivity().getContentResolver()
                .insert(PlacesContract.PlacesEntry.CONTENT_URI, contentValues);

        assert uri != null;
        int placeIDinDB = (Long.valueOf(uri.getLastPathSegment())).intValue(); // Get action sms ID

        if(ifEnterFromAddReminder && placeWasAdded)closeFragment(placeName, placeIDinDB);
    }

    // --------------------------------------------------------------------------------------------- Show Dialog Place Name
    private void showDialogPlaceName() {
        DialogFragment dialogPlaceName = new DialogPlaceName();
        //Bundle args = new Bundle();
        //args.putInt(REMINDER_SETTINGS, CURRENT_SETTINGS);
        //dialogPlaceName.setArguments(args);
        dialogPlaceName.setTargetFragment(this, PALCE_NAME_REQUEST);
        dialogPlaceName.show(getActivity().getSupportFragmentManager(), "dialog_place_name");
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // ********************************************************************************************* Click on Places List
    @Override
    public void onClick(int position) {
        // click from listener
        if (ifEnterFromAddReminder) { // ------------------------------------------when Add Reminder
            String placeName = mPlacesList.get(position).getPlaceName();
            int placeIDinDB = mPlacesList.get(position).getPlaceIDinDB();
          closeFragment(placeName, placeIDinDB);
        }
    }

    private void closeFragment(String placeName, int placeIDinDB){
        ((AddReminderActivity) getActivity()).onEnterFromSelectPlace(placeIDinDB, placeName);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    // *********************************************************************************************
    // ********************************************************************************************* Start Load
    private void makeMovieSearchQuery() {
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Object movieSearchLoader = loaderManager.getLoader(PLACES_ID_LOADER);

        if (movieSearchLoader == null){
            //loaderManager.initLoader(REMINDERS_ID_LOADER, queryBundle, this);
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

    //@SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object loadedData) {
        Cursor mPlacesData = (Cursor) loadedData;
        mPlacesList = AppContentProvider.placesListFromCursor(mPlacesData);
        adapter.setRemindersData(mPlacesList);


        // if Place List is Empty - Enter to Add Place Dialog
        if(ifEnterFromAddReminder && (mPlacesList.size() == 0) && (placeID == null)){
            addPlace();
            placeWasAdded = true;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO MAKE LOAD RESET
    }
}
