package com.rachio.iro.ui.newschedulerulepath.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LockableViewPager extends ViewPager {
    private boolean locked = false;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public final void setLocked(boolean enabled) {
        this.locked = enabled;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.locked) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.locked) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable("super", superState);
        state.putBoolean("locked", this.locked);
        return state;
    }

    public void onRestoreInstanceState(Parcelable inState) {
        Bundle state = (Bundle) inState;
        super.onRestoreInstanceState(state.getParcelable("super"));
        this.locked = state.getBoolean("locked");
    }
}
