package android.support.v4.view;

import android.view.ViewGroup;

public final class NestedScrollingParentHelper {
    private int mNestedScrollAxes;
    private final ViewGroup mViewGroup;

    public NestedScrollingParentHelper(ViewGroup viewGroup) {
        this.mViewGroup = viewGroup;
    }

    public final void onNestedScrollAccepted$244b0b2e(int axes) {
        this.mNestedScrollAxes = axes;
    }

    public final int getNestedScrollAxes() {
        return this.mNestedScrollAxes;
    }

    public final void onStopNestedScroll$3c7ec8c3() {
        this.mNestedScrollAxes = 0;
    }
}
