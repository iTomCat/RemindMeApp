package com.example.tomcat.remindmeapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener{

    public static Typeface robotoFont;
    private int currentPage = 0;
    //@BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    //@BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.title) ImageView reminderLogo;
    //@BindView(R.id.viewpager) ViewPager viewPager;
    //@BindView(R.id.tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        robotoFont = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbarLayout.setExpandedTitleColor(Color.BLUE);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        reminderLogo = findViewById(R.id.image_xyz);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPageListener(viewPager);
    }

    public void plusFabButton(View view) {
        if (currentPage == 0){ // ------------------------------------------- + action for Reminders
            Log.d("Pagery", "+ Reminders");
        }else {  // ------------------------------------------------------------ + action for Places
            Log.d("Pagery", "+ Places");
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

    // ********************************************************************************************* Page Adapter
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Reminders(), getString(R.string.reminders));
        //adapter.addFragment(new RemindersArchived(), getString(R.string.arch_rem));
        adapter.addFragment(new YourPlaces(), getString(R.string.your_places));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
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
    private void viewPageListener(ViewPager viewPager){
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
