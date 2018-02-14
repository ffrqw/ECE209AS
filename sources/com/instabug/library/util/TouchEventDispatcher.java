package com.instabug.library.util;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.instabug.library.w;

public class TouchEventDispatcher {
    private final w mUserStepsTracker;
    private int previousX;
    private int previousY;

    public TouchEventDispatcher(w wVar) {
        this.mUserStepsTracker = wVar;
    }

    public boolean dispatchTouchEvent(Activity activity, MotionEvent motionEvent) {
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        if (motionEvent.getAction() == 0) {
            this.previousX = rawX;
            this.previousY = rawY;
        } else if (motionEvent.getAction() == 1 && this.previousX == rawX && this.previousY == rawY) {
            onViewTapped(activity, findTargetView(activity.getWindow().getDecorView(), rawX, rawY));
            return true;
        }
        return false;
    }

    private void onViewTapped(Activity activity, View view) {
        if (view != null) {
            String str = null;
            if (view.getId() > 0) {
                try {
                    str = activity.getResources().getResourceEntryName(view.getId());
                } catch (Throwable e) {
                    InstabugSDKLogger.e(this, "Something went wrong while getting resource with id = " + view.getId(), e);
                }
            }
            this.mUserStepsTracker.a(activity.getClass().getName(), str, view.getClass().getName());
        }
    }

    private View findTargetView(View view, int i, int i2) {
        View view2 = null;
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        if (i2 < iArr[1] || i < iArr[0] || i2 > iArr[1] + view.getHeight() || i > iArr[0] + view.getWidth()) {
            return null;
        }
        if (!(view instanceof ViewGroup)) {
            return view;
        }
        View childAt;
        for (int i3 = 0; i3 < ((ViewGroup) view).getChildCount(); i3++) {
            childAt = ((ViewGroup) view).getChildAt(i3);
            if (childAt instanceof ViewGroup) {
                childAt = findTargetView((ViewGroup) childAt, i, i2);
                if (childAt == null) {
                    childAt = view2;
                }
                view2 = childAt;
            } else {
                childAt = findTargetView(childAt, i, i2);
                if (childAt != null) {
                    view2 = childAt;
                }
            }
            if (view2 != null) {
                break;
            }
        }
        childAt = view2;
        if (childAt == null) {
            return view;
        }
        return childAt;
    }
}
