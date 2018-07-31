package com.example.tomcat.remindmeapp;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Reminder;
import com.example.tomcat.remindmeapp.places.Geofencing;
import com.example.tomcat.remindmeapp.places.PlacesFragment;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener{

    public FabButtonListenerFromActivity activityListener;

    public static Typeface robotoLightFont;
    public static Typeface robotoFont;

    private int currentPage = 0;
    /*private GoogleApiClient mClient;
    private Geofencing mGeofencing;*/


    @BindView(R.id.rem_logo) ImageView reminderLogo;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.viewpager) CustomViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.google_privacy) LinearLayout privacy;


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
        viewPager.setPagingEnabled(true); //Swipe on/off
        setupViewPager(viewPager);

        //TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPageListener(viewPager);
        //displayIntro(0);




       /* mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        mGeofencing = new Geofencing(this, mClient);*/
    }

    /*// ********************************************************************************************* Google Play Services
    //Called when the Google API Client is successfully connected
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
*/



    // ********************************************************************************************* Fab Button
    public void setActivityListener(FabButtonListenerFromActivity activityListener) {
        this.activityListener = activityListener;
    }

    public interface FabButtonListenerFromActivity {
        void fabButtonFromActivity();
    }

    public void plusFabButton(View view) {
        if (currentPage == 0){ // ------------------------------------------------------------------ + action for RemindersFragment
            Intent addReminderIntent = new Intent(MainActivity.this,
                    AddReminderActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putInt(AddReminderActivity.NEW_OR_EDIT, AddReminderActivity.NEW_REMINDER);
            addReminderIntent.putExtras(mBundle);
            startActivity(addReminderIntent);

        }else {  // -------------------------------------------------------------------------------- + action for PlacesFragment

            if (null != activityListener) {
                activityListener.fabButtonFromActivity();
            }
        }
    }

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

    private void displayIntro(int currentPage){
        int visibility;
        final TextView introTxt =  findViewById(R.id.remind_info);
        final ImageView imageIntro = findViewById(R.id.lines);


        if (currentPage == 1) { // Places
            visibility = View.GONE;
        }else { // Reminders
            visibility = (RemindersFragment.mRemindersData.getCount() > 0) ? View.GONE : View.VISIBLE;
        }

        introTxt.setVisibility(visibility);
        imageIntro.setVisibility(visibility);

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

                if (position == 1) { // Places
                    privacy.setVisibility(View.VISIBLE);
                }else { // Reminders
                    privacy.setVisibility(View.GONE);
                }

                displayIntro(position);
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
