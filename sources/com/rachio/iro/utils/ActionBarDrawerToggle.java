package com.rachio.iro.utils;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class ActionBarDrawerToggle extends android.support.v7.app.ActionBarDrawerToggle {
    private float lastKnownPosition = 0.0f;
    private boolean mCancelled = false;
    private long mDuration = 300;
    private boolean mEnabled;

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public final void setDrawerIndicatorEnabled(boolean enable) {
        this.mEnabled = true;
    }

    public final boolean onOptionsItemSelected(MenuItem item) {
        return this.mEnabled ? super.onOptionsItemSelected(item) : false;
    }

    public final void syncState() {
        if (this.mEnabled) {
            super.syncState();
        } else {
            onDrawerSlide(null, 1.0f);
        }
    }

    public final void showHamburger(boolean animate) {
        if (animate) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            ofFloat.setDuration(this.mDuration);
            ofFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator animation) {
                    ActionBarDrawerToggle.this.onDrawerSlide(null, ((Float) animation.getAnimatedValue()).floatValue());
                }
            });
            ofFloat.start();
            return;
        }
        onDrawerSlide(null, 0.0f);
    }

    public final void onDrawerSlide(View drawerView, float slideOffset) {
        this.lastKnownPosition = Math.min(1.0f, Math.max(0.0f, slideOffset));
        super.onDrawerSlide(drawerView, slideOffset);
    }

    public final boolean isHamburger() {
        return this.lastKnownPosition == 0.0f;
    }
}
