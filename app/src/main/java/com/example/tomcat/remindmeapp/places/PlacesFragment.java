package com.example.tomcat.remindmeapp.places;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tomcat.remindmeapp.AddReminderActivity;
import com.example.tomcat.remindmeapp.MainActivity;
import com.example.tomcat.remindmeapp.R;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment Displays a list with your places
 */

public class PlacesFragment extends Fragment implements MainActivity.FabButtonListenerFromActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    @BindView(R.id.fab_places_fragm) ImageButton fabPlus;
    private Unbinder unbinder;
    private boolean ifEnterFromAddReminder = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.places_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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
                addPlace();
            }
        });
    }

    // ********************************************************************************************* PlacePicker
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
            Log.d("placeID", "Add222");
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
        Log.d("placeID", "requestCode: " + requestCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(getActivity(), data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            // Extract the place information from the API
            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            String placeID = place.getId();

            Log.d("placeID", "placeName: " + placeName);

            /*// Insert a new place into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeID);
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);

            Log.d("placeID", "PlaceID: " + placeID);

            // Get live data information
            refreshPlacesData();*/
        }
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ifEnterFromAddReminder) {
            ((AddReminderActivity) getActivity()).onEnterFromSelectPlace();
        }
    }
}
