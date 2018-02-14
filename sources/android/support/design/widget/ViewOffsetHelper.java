package android.support.design.widget;

import android.support.v4.view.ViewCompat;
import android.view.View;

final class ViewOffsetHelper {
    private int mLayoutLeft;
    private int mLayoutTop;
    private int mOffsetLeft;
    private int mOffsetTop;
    private final View mView;

    public ViewOffsetHelper(View view) {
        this.mView = view;
    }

    public final void onViewLayout() {
        this.mLayoutTop = this.mView.getTop();
        this.mLayoutLeft = this.mView.getLeft();
        updateOffsets();
    }

    private void updateOffsets() {
        ViewCompat.offsetTopAndBottom(this.mView, this.mOffsetTop - (this.mView.getTop() - this.mLayoutTop));
        ViewCompat.offsetLeftAndRight(this.mView, this.mOffsetLeft - (this.mView.getLeft() - this.mLayoutLeft));
    }

    public final boolean setTopAndBottomOffset(int offset) {
        if (this.mOffsetTop == offset) {
            return false;
        }
        this.mOffsetTop = offset;
        updateOffsets();
        return true;
    }

    public final int getTopAndBottomOffset() {
        return this.mOffsetTop;
    }
}
