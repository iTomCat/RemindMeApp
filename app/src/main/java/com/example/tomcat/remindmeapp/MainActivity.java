package com.example.tomcat.remindmeapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.tomcat.remindmeapp.places.PlacesFragment;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener{

    public FabButtonListenerFromActivity activityListener;

    public static Typeface robotoLightFont;
    public static Typeface robotoFont;

    private int currentPage = 0;
    public static int selPage;

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


        appBarLayout.addOnOffsetChangedListener(this);

        viewPager.setPagingEnabled(true); //Swipe on/off
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        viewPageListener(viewPager);
    }



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
        if(selPage == 1) displayIntro(1);
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

                selPage = position;
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
