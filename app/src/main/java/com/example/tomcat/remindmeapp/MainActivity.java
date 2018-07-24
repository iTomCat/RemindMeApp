package com.example.tomcat.remindmeapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tomcat.remindmeapp.places.PlacesFragment;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener,
        ConnectionCallbacks,
        OnConnectionFailedListener {

    public FabButtonListenerFromActivity activityListener;

    public void setActivityListener(FabButtonListenerFromActivity activityListener) {
        this.activityListener = activityListener;
    }



    public interface FabButtonListenerFromActivity {
        void fabButtonFromActivity();
    }


    /*public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;*/



    public static Typeface robotoLightFont;
    public static Typeface robotoFont;

    private int currentPage = 0;
    private GoogleApiClient mClient;


    @BindView(R.id.rem_logo) ImageView reminderLogo;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        robotoLightFont = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        robotoFont = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");

        //CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        //toolbarLayout.setExpandedTitleColor(Color.BLUE);

        //AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        //reminderLogo = findViewById(R.id.image_xyz);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //CustomViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        //TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPageListener(viewPager);


        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    // ********************************************************************************************* Google Play Services
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("ConnectServ", "CONNECT");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // ********************************************************************************************* Fab Button
    public void plusFabButton(View view) {
        if (currentPage == 0){ // ------------------------------------------------------------------ + action for RemindersFragment
            Intent addReminderIntent = new Intent(MainActivity.this,
                    AddReminderActivity.class);
            startActivity(addReminderIntent);

        }else {  // -------------------------------------------------------------------------------- + action for PlacesFragment

            if (null != activityListener) {
                activityListener.fabButtonFromActivity();
            }





                // Check the permission is already granted or not
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)  {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_FINE_LOCATION);
                } else {
                    Log.d("placeID", "in: " +  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
                    // Android version is lesser than 6.0 or the permission is already granted
                    try {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        Intent i = builder.build(this);
                        startActivityForResult(i, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
                        Toast.makeText(this, getString(R.string.play_services_problem),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
                        Toast.makeText(this, getString(R.string.play_services_problem),
                                Toast.LENGTH_LONG).show();
                    }
                }*/
        }
    }



    /*public static void addPlace(Activity activity, MainActivity mainActivity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            // Android version is lesser than 6.0 or the permission is already granted
            Log.d("placeID", "Add222");
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent i = builder.build(activity);
                mainActivity.startActivityForResult(i, PLACE_PICKER_REQUEST);
                //activity.startActivity(i);
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
                Toast.makeText(activity, activity.getString(R.string.play_services_problem),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
                Toast.makeText(activity, activity.getString(R.string.play_services_problem),
                        Toast.LENGTH_LONG).show();
            }
        }
    }*/


    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("placeID", "requestCode: " + requestCode);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            // Extract the place information from the API
            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            String placeID = place.getId();

            Log.d("placeID", "placeName: " + placeName);

            Insert a new place into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeID);
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);

            Log.d("placeID", "PlaceID: " + placeID);

            // Get live data information
            refreshPlacesData();
        }
    }*/

    // ********************************************************************************************* Logo Scaling
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        double m = Math.abs(verticalOffset) * (-0.5) + getResources()
                .getDimension(R.dimen.max_height);
        reminderLogo.getLayoutParams().height = (int)m;
        reminderLogo.requestLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // ********************************************************************************************* Page Adapter
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RemindersFragment(), getString(R.string.reminders));
        adapter.addFragment(new PlacesFragment(), getString(R.string.your_places));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // --------------------------------------------------------------------------------------------- View Pager Listener
    private void viewPageListener(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // ------------------------------------------------------------ FAB Button Animation
                if (state == 2) {
                    final ImageButton fabButton = findViewById(R.id.fab_main);
                    ScaleAnimation anim = new ScaleAnimation(
                            0,1,
                            0,1,
                            1, 0.5f,
                            1, 0.5f);
                    anim.setFillBefore(true);
                    anim.setFillAfter(true);
                    anim.setFillEnabled(true);
                    anim.setDuration(400);
                    anim.setInterpolator(new OvershootInterpolator());
                    fabButton.startAnimation(anim);
                }
            }
        });
    }
}
