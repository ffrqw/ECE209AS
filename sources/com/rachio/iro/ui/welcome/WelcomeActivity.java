package com.rachio.iro.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.DashboardActivity;
import com.rachio.iro.ui.newschedulerulepath.views.LockableViewPager;
import com.rachio.iro.ui.welcome.fragments.BaseWelcomeFragment;
import com.rachio.iro.ui.welcome.fragments.RemoteControlFragment;
import com.rachio.iro.ui.welcome.fragments.ScheduleFragment;
import com.rachio.iro.ui.welcome.fragments.TopFragment;
import com.rachio.iro.ui.welcome.fragments.WeatherStationFragment;
import com.rachio.iro.ui.welcome.fragments.ZonesFragment;
import com.viewpagerindicator.CirclePageIndicator;

public class WelcomeActivity extends BaseActivity {
    private static final BaseWelcomeFragment[] fragments = new BaseWelcomeFragment[]{new TopFragment(), new ScheduleFragment(), new ZonesFragment(), new WeatherStationFragment(), new RemoteControlFragment()};
    private CirclePageIndicator indicator;
    private LockableViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_welcome);
        final String userId = getUserIdFromExtras();
        final boolean readOnly = getIntent().getBooleanExtra("readonly", false);
        ((ImageView) findViewById(R.id.welcome_exit)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DashboardActivity.goToDashboard(WelcomeActivity.this, WelcomeActivity.this.prefsWrapper, userId, true, readOnly, null, null, false, true);
                WelcomeActivity.this.finish();
            }
        });
        this.viewPager = (LockableViewPager) findViewById(R.id.welcome_pager);
        this.indicator = (CirclePageIndicator) findViewById(R.id.welcome_indicator);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            public final Fragment getItem(int position) {
                return WelcomeActivity.fragments[position];
            }

            public final int getCount() {
                return WelcomeActivity.fragments.length;
            }
        };
        this.viewPager.setOffscreenPageLimit(5);
        this.viewPager.setAdapter(adapter);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            int last = 0;

            public final void onPageSelected(int position) {
                WelcomeActivity.fragments[this.last].onInactive();
                WelcomeActivity.fragments[position].onActive();
                this.last = position;
            }

            public final void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 0:
                        for (BaseWelcomeFragment fragment : WelcomeActivity.fragments) {
                            fragment.onScrollStopped();
                        }
                        return;
                    default:
                        return;
                }
            }

            public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
        this.indicator.setViewPager(this.viewPager);
    }
}
